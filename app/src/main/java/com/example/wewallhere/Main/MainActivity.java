package com.example.wewallhere.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.wewallhere.R;
import com.example.wewallhere.Upload.UploadActivity;
import com.example.wewallhere.ExploreByList.ExploreListActivity;

public class MainActivity extends AppCompatActivity {
    private Button buttonToUploadSection;
    private Button buttonToExploreList;
    private final String [] all_permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonToUploadSection = findViewById(R.id.go_to_upload_section);
        buttonToUploadSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });
        buttonToExploreList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ExploreListActivity.class);
                startActivity(intent);
            }
        });


        ActivityCompat.requestPermissions(this, all_permissions, 200);



    }
}