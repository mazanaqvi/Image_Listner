package com.example.fyp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.*;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

     EditText txt_user;
     EditText txt_pass;
     Button btn_login;
     Spinner _spinner;
      DataBaseHelper mydb;
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adapter_view_layout1);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        txt_user = (EditText) findViewById(R.id.txt_username);
        txt_pass = (EditText) findViewById(R.id.txt_password);
        btn_login = (Button) findViewById(R.id.button6);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-7302854544676084/1786480578", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error

                        mInterstitialAd = null;
                    }
                });

        mydb = new DataBaseHelper(this);
        Intent intent = new Intent(MainActivity.this, take_photo.class);
        startActivity(intent);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("Login","loginbtn");
                mFirebaseAnalytics.logEvent("login_btn", bundle);
                if (!txt_user.getText().toString().equals("") && !txt_pass.getText().toString().equals("")) {
                    String item = "User";

                        String u = txt_user.getText().toString();
                        String p = txt_pass.getText().toString();
                        boolean var =  mydb.insert_hard_coded_data();
                         var = mydb.CheckUser(u,p);
                        if(var)
                        {
                            Toast.makeText(MainActivity.this, "Login Successfully by regular user", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, take_photo.class);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Username or Password is incorrect", Toast.LENGTH_SHORT).show();
                        }


                }
            }

        });
    }
}