package com.example.wewallhere;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;

public class IntroductoryActivity extends AppCompatActivity {

    ImageView logo, appName, bgImg;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introductory);
        logo = findViewById(R.id.logo);
        appName = findViewById(R.id.app_name);
        bgImg = findViewById(R.id.img);
        lottieAnimationView = findViewById(R.id.lottie);

        bgImg.animate().translationY(-26000).setDuration(1000).setStartDelay(4000);
        appName.animate().translationY(1600).setDuration(1000).setStartDelay(4000);
        logo.animate().translationY(1850).setDuration(1000).setStartDelay(4000);
        lottieAnimationView.animate().translationY(1600).setDuration(1000).setStartDelay(4000);
    }
}