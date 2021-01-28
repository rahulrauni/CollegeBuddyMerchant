package com.test.collegebuddymerchant;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class OrdersFrag extends Fragment  implements SwipeRefreshLayout.OnRefreshListener{
    SharedPreferences details;
    private RecyclerView mRecyclerView;
    private LottieAnimationView tv_no_item;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    int totalItems, scrolledOutItems;
    private LinearLayoutManager manager;
    Query query;
    private DocumentSnapshot lastDocumentSnapshot;
    Boolean isScrolling = false;
    LottieAnimationView noData;
    String cityName,collegeName,loginId;
    DocumentReference stausref;
    SwipeRefreshLayout mSwipeRefreshLayout;
    OrderAdapter myAdapter;
    Handler handler;
    private Runnable r;
    Calendar myCalendar = Calendar.getInstance();
    EditText chooseDateTextView;
    ImageView searchbar;
    String currentDate;
    Double sum=0.00;
    TextView earnedText;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_orders, container, false);

        details = getActivity().getSharedPreferences("com.test.collegebuddymerchant", Context.MODE_PRIVATE);
        cityName = details.getString("cityName","");
        collegeName = details.getString("collegeName","");
        loginId = details.getString("loginId","");
        mRecyclerView = v.findViewById(R.id.my_recycler_view);
        tv_no_item = v.findViewById(R.id.tv_no_cards);
        noData =  v.findViewById(R.id.no_data);
        stausref = db.collection(cityName).document(collegeName);
        chooseDateTextView =v.findViewById(R.id.choose_date);
        searchbar= v.findViewById(R.id.search_bar);
        earnedText =v.findViewById(R.id.earnedText);
        if (mRecyclerView != null) {
            //to enable optimization of recyclerview
            mRecyclerView.setHasFixedSize(true);
        }
        lastDocumentSnapshot=null;
        sum=0.00;
        manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);
        myAdapter= new OrderAdapter(mRecyclerView,getContext(),new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>() , new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(),new ArrayList<String>(), new ArrayList<String>());
        mRecyclerView.setAdapter(myAdapter);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.authui_colorPrimaryDark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(true);
                LoadData();
                // Fetching data from server
            }
        });
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date2 = new Date();
        currentDate = formatter.format(date2);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        chooseDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        chooseDateTextView.setText(currentDate);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItems = manager.getItemCount();
                scrolledOutItems =manager.findLastVisibleItemPosition();
                if(isScrolling && scrolledOutItems+1>=totalItems){
                    isScrolling = false;
                    LoadData();
                }
            }
        });

        searchbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(chooseDateTextView.getText().toString())){
                    currentDate = chooseDateTextView.getText().toString();
                   // Log.e("date5",currentDate);
                    sum=0.00;
                    lastDocumentSnapshot=null;
                    myAdapter= new OrderAdapter(mRecyclerView,getContext(),new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>() , new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(),new ArrayList<String>(), new ArrayList<String>());
                    mRecyclerView.setAdapter(myAdapter);
                    LoadData();
                }else{
                    Toast.makeText(getActivity(), "please enter the date", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return v;
    }


    private void LoadData() {
        mSwipeRefreshLayout.setRefreshing(true);
        if(lastDocumentSnapshot == null){
            query = db.collection(cityName).document(collegeName).collection("productOrders").whereEqualTo("status","Delivered").limit(10);
        }else{
            query = db.collection(cityName).document(collegeName).collection("productOrders").whereEqualTo("status","Delivered").startAfter(lastDocumentSnapshot).limit(10);
        }
        query.get().addOnSuccessListener(getActivity(), new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    if (tv_no_item.getVisibility() == View.VISIBLE) {
                        tv_no_item.setVisibility(View.GONE);
                    }
                }
                for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    lastDocumentSnapshot = documentSnapshot;
                    if (tv_no_item.getVisibility() == View.VISIBLE) {
                        tv_no_item.setVisibility(View.GONE);
                    }
                    long milliseconds=documentSnapshot.getTimestamp("orderedTime").toDate().getTime();
                    String dateString= DateFormat.format("dd/MM/yyyy",new Date(milliseconds)).toString();
                    if(currentDate.equals(dateString)){
                        sum+=Double.parseDouble(documentSnapshot.getString("cost"));
                        String key = documentSnapshot.getId();
                        String productName = documentSnapshot.getString("productName");
                        String quantity = documentSnapshot.getString("quantity");
                        String status = documentSnapshot.getString("status");
                        String price = documentSnapshot.getString("price");
                        String mrp = documentSnapshot.getString("mrp");
                        String discount = documentSnapshot.getString("discount");
                        String productImage = documentSnapshot.getString("productImage");
                        String orderId = documentSnapshot.getString("orderId");
                        ((OrderAdapter)mRecyclerView.getAdapter()).update(key,productName,quantity,status,price,mrp,discount,productImage, orderId);
                    }

                }
                earnedText.setText(String.valueOf(sum));

                mSwipeRefreshLayout.setRefreshing(false);
            }
        }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        chooseDateTextView.setText(currentDate);
        lastDocumentSnapshot =null;
        sum=0.00;
        myAdapter= new OrderAdapter(mRecyclerView,getContext(),new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>() , new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(),new ArrayList<String>(), new ArrayList<String>());
        mRecyclerView.setAdapter(myAdapter);
        LoadData();
    }

    private class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder>{
        RecyclerView recyclerView;
        Context context;
        ArrayList<String> productKeys=new ArrayList<>();
        ArrayList<String> productNames=new ArrayList<>();
        ArrayList<String>  quantities= new ArrayList<>();
        ArrayList<String>  statuses= new ArrayList<>();
        ArrayList<String> prices= new ArrayList<>();
        ArrayList<String> mrps= new ArrayList<>();
        ArrayList<String> discounts= new ArrayList<>();
        ArrayList<String> productImages= new ArrayList<>();
        ArrayList<String> orderIds= new ArrayList<>();

        public void update(String key,String productName, String quantity, String status, String price, String mrp, String discount, String productImage, String orderId){
            productKeys.add(key);
            productNames.add(productName);
            quantities.add(quantity);
            statuses.add(status);
            prices.add(price);
            mrps.add(mrp);
            discounts.add(discount);
            productImages.add(productImage);
            orderIds.add(orderId);
            notifyDataSetChanged();  //refershes the recyler view automatically...

        }

        public OrderAdapter(RecyclerView recyclerView, Context context, ArrayList<String> productKeys,ArrayList<String> productNames, ArrayList<String> quantities, ArrayList<String> statuses, ArrayList<String> prices, ArrayList<String> mrps, ArrayList<String> discounts,ArrayList<String> productImages, ArrayList<String> orderIds) {
            this.recyclerView = recyclerView;
            this.context = context;
            this.productKeys = productKeys;
            this.productNames = productNames;
            this.quantities = quantities;
            this.statuses = statuses;
            this.prices = prices;
            this.mrps = mrps;
            this.discounts = discounts;
            this.productImages = productImages;
            this.orderIds =  orderIds;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
            holder.productName.setText(productNames.get(position));
            holder.quantity.setText("Quantity: " + quantities.get(position));


        }

        @Override
        public int getItemCount() {
            return productNames.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView quantity, status, productName, price, mrp, discount, orderNo;
            ImageView productImage;
            Button confirm_button,cancel_button;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                productName=itemView.findViewById(R.id.productname);
                quantity = itemView.findViewById(R.id.quantityTextView);
                status = itemView.findViewById(R.id.statusTextView);
                price=itemView.findViewById(R.id.price);
                mrp= itemView.findViewById(R.id.mrp);
                mrp.setPaintFlags(mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                discount= itemView.findViewById(R.id.discount);
                productImage = itemView.findViewById(R.id.productimage);
                orderNo = itemView.findViewById(R.id.orderid);
                confirm_button= itemView.findViewById(R.id.confirmButton);
                confirm_button.setVisibility(View.GONE);


            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        chooseDateTextView.setText(currentDate);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        chooseDateTextView.setText(sdf.format(myCalendar.getTime()));
    }

}