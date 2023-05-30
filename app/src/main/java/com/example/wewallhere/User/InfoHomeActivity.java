package com.example.wewallhere.User;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.wewallhere.R;
import com.google.gson.Gson;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

import Helper.ToastHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InfoHomeActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private EditText usernameEditText;
    private EditText phoneEditText;
    private TextView emailTextView;
    private Button saveButton;
    private Button logoutButton;
    private UserInfo userInfo;
    private String url_uploadUserInfo = "http://54.252.196.140:3000/uploadUser/";
    private int REQUEST_IMAGE_PICK = 8762;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_home);

        profileImageView = findViewById(R.id.profile_picture);
        usernameEditText = findViewById(R.id.username);
        phoneEditText = findViewById(R.id.phone);
        emailTextView = findViewById(R.id.email);
        saveButton = findViewById(R.id.save_info_button);
        logoutButton = findViewById(R.id.log_out_button);

        fetchUserInfo();


        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImagePermissionCheck();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void fetchUserInfo(){
    }



    private boolean fetchEditTexts(){
        String phone = phoneEditText.getText().toString().trim();
        String name = usernameEditText.getText().toString().trim();

        if(CheckPhoneFormat(phone) && CheckNameFormat(name)){
            userInfo.setPhone(phone);
            userInfo.setUsername(name);
            return true;
        }else{
            return false;
        }

    }
    private void saveUserInfo() {
        if(!fetchEditTexts()){
            return;
        }
        // Make an HTTP POST request to the server to save the UserInfo
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        // Convert the UserInfo object to JSON
        Gson gson = new Gson();
        String json = gson.toJson(userInfo);

        // Create the request body
        RequestBody requestBody = RequestBody.create(JSON, json);

        // Create the request
        Request request = new Request.Builder()
                .url(url_uploadUserInfo)
                .post(requestBody)
                .build();

        // Send the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                ToastHelper.showLongToast(getApplicationContext(),"Save user info failed, please check your network.", Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    ToastHelper.showLongToast(getApplicationContext(),"Saved.", Toast.LENGTH_SHORT);
                } else {
                    ToastHelper.showLongToast(getApplicationContext(),"Save user info failed, please check your network.", Toast.LENGTH_SHORT);
                }
            }
        });
    }


    private void logout() {
        // Handle the log out button click
        // Start the PhoneVerificationActivity to log out the user
        Intent intent = new Intent(this, PhoneVerificationActivity.class);
        startActivity(intent);
        finish(); // Optional: Finish the current activity to prevent the user from coming back here after logging out
    }

    // pfp image
    private boolean checkImagePermission() {
        // Check if the required permissions are granted
        int readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return readStoragePermission == PackageManager.PERMISSION_GRANTED;
    }
    private void startImagePickerIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }
    private void selectImagePermissionCheck() {
        // Check if the required permission to read external storage is granted
        if (checkImagePermission()) {
            startImagePickerIntent();
        } else {
            // Request the necessary permissions
            String[] permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
            ActivityCompat.requestPermissions(this, permissions, REQUEST_IMAGE_PICK);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_PICK) {
            // Check if all required permissions are granted, including media & location
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                startImagePickerIntent();
            } else {
                Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            // Call getLocation and then upload the image
            profileImageView.setImageURI(selectedImageUri);
            userInfo.setUri_pfp(selectedImageUri);
        }
    }
    private Boolean CheckPhoneFormat(String phone){
        if (phone.length()==0) {
            Toast.makeText(InfoHomeActivity.this, "Phone number cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }else if (!phone.matches("[0-9]+")) {
            Toast.makeText(InfoHomeActivity.this, "Phone number should contain digits only.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private Boolean CheckNameFormat(String name){
        if (name.length()==0) {
            Toast.makeText(InfoHomeActivity.this, "Name cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}


