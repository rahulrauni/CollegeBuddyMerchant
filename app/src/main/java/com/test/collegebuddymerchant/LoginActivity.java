package com.test.collegebuddymerchant;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    EditText login,password;
    Button loginButton;
    ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences detail = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        detail = getSharedPreferences("com.test.collegebuddymerchant", Context.MODE_PRIVATE);
        login = findViewById(R.id.login);
        password =  findViewById(R.id.password);
        loginButton=findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String loginid= login.getText().toString();
                String verified = password.getText().toString().trim();
                if(!loginid.isEmpty()  && !verified.isEmpty()){
                    login(loginid,verified);
                }else{
                    Toast.makeText(LoginActivity.this, "Please fill all the login credential", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void login(String loginid, String verified) {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Getting user data..");
        progressDialog.setCancelable(false);
        progressDialog.show();
        db.collection("Merchants").document(loginid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    progressDialog.dismiss();
                    String passworduser = documentSnapshot.getString("pass");
                    Log.e("password",passworduser+ " "+verified);
                    if(!passworduser.equals(verified)){
                        Toast.makeText(LoginActivity.this, "Reenter Yur Password", Toast.LENGTH_SHORT).show();
                        password.setText("");
                    }else{
                        SharedPreferences.Editor editor = detail.edit();
                        editor.putBoolean("logged",true);
                        editor.putString("cityName",documentSnapshot.getString("cityId"));
                        editor.putString("collegeName",documentSnapshot.getString("collegeId") );
                        editor.putString("loginId",documentSnapshot.getString("loginId"));
                        editor.commit();
                        Toast.makeText(LoginActivity.this, "Successfully Logged", Toast.LENGTH_SHORT).show();
                        Intent detailFill = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(detailFill);
                        finish();
                    }

                }else{
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Invalid Login Credentials", Toast.LENGTH_SHORT).show();
                    login.setText("");
                    password.setText("");
                }
            }
        });

    }
}