package com.omolayoseun.fuprett;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("tutorial-screen", Context.MODE_PRIVATE);

        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));

        if (!sharedPreferences.getBoolean("has-shown-tutorial-screen", false))
            startActivity(new Intent(SplashScreenActivity.this, OnBoardingActivity.class));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        finish();
    }

}