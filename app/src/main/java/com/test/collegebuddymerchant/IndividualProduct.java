package com.test.collegebuddymerchant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class IndividualProduct extends AppCompatActivity {
    String productNames,quantities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_product);
        productNames = getIntent().getStringExtra("productNames").trim();
        quantities = getIntent().getStringExtra("quantities").trim();

    }
}