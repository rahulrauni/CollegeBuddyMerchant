package com.test.collegebuddymerchant;


import android.graphics.Paint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class OrdersFrag extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_orders, container, false);

        return v;
    }

}