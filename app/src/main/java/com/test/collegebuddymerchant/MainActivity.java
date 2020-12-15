package com.test.collegebuddymerchant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SharedPreferences details;

    private BottomNavigationView nav_bar;
    private FrameLayout frame;
    private HomeFrag homeFrag;
    private OrdersFrag ordersFrag;
    private ProfileFrag profileFrag;
    private PackedOrderFrag packedOrderFrag;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        details = getApplicationContext().getSharedPreferences("com.test.collegebuddymerchant", Context.MODE_PRIVATE);
        Boolean logged= details.getBoolean("logged",false);

        if(!logged){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        frame=findViewById(R.id.frame_container);
        nav_bar=findViewById(R.id.nav_bar);
        nav_bar.setItemBackgroundResource(R.color.design_default_color_secondary);
        homeFrag=new HomeFrag();
        ordersFrag=new OrdersFrag();
        profileFrag= new ProfileFrag();
        packedOrderFrag=new PackedOrderFrag();



        setFragment(homeFrag);


        nav_bar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home :
                        //nav_bar.setItemBackgroundResource(R.color.design_default_color_secondary);
                        setFragment(homeFrag);
                        return true;
                    case R.id.nav_packed:
                        //nav_bar.setItemBackgroundResource(R.color.design_default_color_secondary);
                        setFragment(packedOrderFrag);
                        return true;
                    case R.id.nav_orders:
                        //nav_bar.setItemBackgroundResource(R.color.design_default_color_secondary);
                        setFragment(ordersFrag);
                        return true;
                    case R.id.nav_profile:
                      //  nav_bar.setItemBackgroundResource(R.color.design_default_color_secondary);
                        setFragment(profileFrag);
                        return true;

                    default:
                        return false;
                }
            }
        });



    }


    //Meathod for setting the fragment in this layout
    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container,fragment);
        fragmentTransaction.commit();
    }

}