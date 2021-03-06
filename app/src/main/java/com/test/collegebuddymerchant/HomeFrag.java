package com.test.collegebuddymerchant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
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

import java.util.ArrayList;


public class HomeFrag extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
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




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_home, container, false);
        details = getActivity().getSharedPreferences("com.test.collegebuddymerchant", Context.MODE_PRIVATE);
        cityName = details.getString("cityName","");
        collegeName = details.getString("collegeName","");
        loginId = details.getString("loginId","");
        mRecyclerView = v.findViewById(R.id.my_recycler_view);
        tv_no_item = v.findViewById(R.id.tv_no_cards);
        noData =  v.findViewById(R.id.no_data);
        stausref = db.collection(cityName).document(collegeName);
        if (mRecyclerView != null) {
            //to enable optimization of recyclerview
            mRecyclerView.setHasFixedSize(true);
        }
        noData.setVisibility(View.GONE);
        lastDocumentSnapshot=null;
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
        handler = new Handler();
        r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 10000);
                lastDocumentSnapshot =null;
                myAdapter= new OrderAdapter(mRecyclerView,getContext(),new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>() , new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(),new ArrayList<String>(), new ArrayList<String>());
                mRecyclerView.setAdapter(myAdapter);
                onRefresh();
            }
        };

        handler.postDelayed(r, 10000);
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
        return v;
    }

    private void LoadData() {
        mSwipeRefreshLayout.setRefreshing(true);
        if(lastDocumentSnapshot == null){
            query = db.collection(cityName).document(collegeName).collection("productOrders").whereEqualTo("status","Placed").limit(10);
        }else{
            query = db.collection(cityName).document(collegeName).collection("productOrders").whereEqualTo("status","Placed").startAfter(lastDocumentSnapshot).limit(10);
        }
        query.get().addOnSuccessListener(getActivity(), new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    if (tv_no_item.getVisibility() == View.VISIBLE) {
                        tv_no_item.setVisibility(View.GONE);
                    }
                    noData.setVisibility(View.GONE);
                }
                for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    lastDocumentSnapshot = documentSnapshot;
                    if (tv_no_item.getVisibility() == View.VISIBLE) {
                        tv_no_item.setVisibility(View.GONE);
                    }
                    noData.setVisibility(View.GONE);
                    String key = documentSnapshot.getId();
                    noData.setVisibility(View.GONE);
                    String productName = documentSnapshot.getString("productName");
                    String quantity = documentSnapshot.getString("quantity");
                    String status = documentSnapshot.getString("status");
                    String price = documentSnapshot.getString("price");
                    String mrp = documentSnapshot.getString("mrp");
                    String discount = documentSnapshot.getString("discount");
                    String productImage = documentSnapshot.getString("productImage");
                    String orderId = documentSnapshot.getString("orderId");
                    ((OrderAdapter)mRecyclerView.getAdapter()).update(key,productName,quantity,status,price,mrp,discount,productImage, orderId);

                    //quantity-copies,
                }
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
        lastDocumentSnapshot =null;
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
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.productName.setText(productNames.get(position));
            holder.quantity.setText("Quantity: " + quantities.get(position));
            holder.confirm_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                    builder.setMessage("Are You Sure").
                            setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    stausref.collection("productOrders").document(productKeys.get(position)).update("status","Packed")
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(getContext(), "Order Sucessfully marked Packed", Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        Toast.makeText(getContext(), "Server Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }).setNegativeButton("Cancel",null);
                    AlertDialog alert=builder.create();
                    alert.show();

                }
            });

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

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            handler.removeCallbacks(r);
            handler.removeCallbacksAndMessages(null);
            handler = null;
            r = null;

        }catch (Exception e){
            Log.e("ThreadUtil:","Error:"+e.toString());

        }
    }
}