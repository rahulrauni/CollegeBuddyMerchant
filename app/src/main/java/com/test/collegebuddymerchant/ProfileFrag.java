package com.test.collegebuddymerchant;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;


public class ProfileFrag extends Fragment {
    SharedPreferences details;
    String loginId;
    CircleImageView imageProfile;
    TextView nameProfile,des,location,phone,type,rating;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_profile, container, false);
        details = getActivity().getSharedPreferences("com.test.collegebuddymerchant", Context.MODE_PRIVATE);
        loginId = details.getString("loginId","");
        imageProfile=v.findViewById(R.id.imageProfile);
        nameProfile= v.findViewById(R.id.nameProfile);
        des =v.findViewById(R.id.des);
        location= v.findViewById(R.id.location);
        phone=v.findViewById(R.id.phone);
        type =v.findViewById(R.id.type);
        rating = v.findViewById(R.id.rating);
        loaddata();
        return v;
    }

    public void loaddata(){
        db.collection("Merchants").document(loginId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                nameProfile.setText(documentSnapshot.getString("name"));
                des.setText(documentSnapshot.getString("loginId"));
                location.setText(documentSnapshot.getString("fullAddress"));
                phone.setText(documentSnapshot.getString("mobile"));
                type.setText(documentSnapshot.getString("merchantFor2"));
                rating.setText(documentSnapshot.getDouble("rating").toString());
                //Picasso.with(getApplicationContext()).load(documentSnapshot.getString("imageLink")).placeholder(R.drawable.avtarimage).into(imageProfile);
            }
        });
    }
}