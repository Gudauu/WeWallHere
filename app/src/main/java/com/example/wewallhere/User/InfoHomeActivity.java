package com.example.wewallhere.User;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.wewallhere.ExploreByList.ExploreListActivity;
import com.example.wewallhere.R;
import com.example.wewallhere.Upload.UploadActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

import Helper.ToastHelper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InfoHomeActivity extends AppCompatActivity {
    private ImageView profileImageView;
    private EditText usernameEditText;
    private EditText phoneEditText;
    private TextView emailTextView;
    private Button saveButton;
    private Button logoutButton;
    private UserInfo userInfo;
    private Uri pfp_uri; // only useful at first selection
    private String url_media_service = "http://54.252.196.140:3000/";
    private String url_download = "http://54.252.196.140:3000/download/";
    private int REQUEST_IMAGE_PICK = 8762;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_home);
        iniBottomMenu();


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


    private void iniBottomMenu(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.info);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.upload) {
                startActivity(new Intent(InfoHomeActivity.this, UploadActivity.class));
                return true;
            }  else if (itemId == R.id.explore) {
                startActivity(new Intent(InfoHomeActivity.this, ExploreListActivity.class));
                return true;
            }
//            } else if (itemId == R.id.navigation_item3) {
//                startActivity(new Intent(CurrentActivity.this, Activity3.class));
//                return true;
//            }

            return false;
        });

    }


    private void fetchUserInfo(){
        SharedPreferences prefs = getSharedPreferences("INFO", MODE_PRIVATE);
        userInfo = new UserInfo();
        userInfo.setEmail(prefs.getString("email", getString(R.string.default_email)));
        userInfo.setUsername(prefs.getString("username", getString(R.string.default_usename)));
        emailTextView.setText(prefs.getString("email", "test@mail.com"));
        usernameEditText.setText(prefs.getString("username", getString(R.string.default_usename)));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url_media_service)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // Create a service interface for your API endpoints
        UserInfoService userInfoService = retrofit.create(UserInfoService.class);
        // Make an API call to retrieve media files
        retrofit2.Call<UserInfo> call = userInfoService.getUserInfo(userInfo.getEmail());
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(retrofit2.Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccessful()) {
                    UserInfo temp_userInfo = response.body();
                    userInfo = temp_userInfo;
                    if(userInfo.getFilename() != null){
                        LoadPfp(userInfo.getFilename());
                    }
                    if(userInfo.getPhone() != null){
                        phoneEditText.setText(userInfo.getPhone());
                    }
                    if(userInfo.getUsername() != null){
                        usernameEditText.setText(userInfo.getUsername());
                    }
                } else {
                    ToastHelper.showLongToast(getApplicationContext(), response.message(), Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                // Handle network or other errors
                // Shutong: new user will also get nothing
//                ToastHelper.showLongToast(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
            }
        });
//        userInfo.setEmail("test@mail.com");

    }

    private void LoadPfp(String filename){
        String imagedownloadUrl = url_download + "image/" + filename;
        Glide.with(getApplicationContext())
                .load(imagedownloadUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Handle the image loading failure if needed
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Image loading is successful, hide the loading panel here
                        return false;
                    }
                })
                .into(profileImageView);
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
        fetchEditTexts();
        // save username to sharedpreferences
        SharedPreferences prefs = getSharedPreferences("INFO", MODE_PRIVATE);
        prefs.edit().putString("username",userInfo.getUsername()).commit();

        try {
            // Create a Retrofit instance
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url_media_service) // Replace with your server's IP address
                    .build();

            // Create an instance of the API service interface
            UserInfoService uploadService = retrofit.create(UserInfoService.class);

            // Check if the profile photo URI is not null
            if (pfp_uri != null) {
                // Create the request body for other fields in UserInfo
                RequestBody usernameRequestBody = RequestBody.create(MediaType.parse("text/plain"), userInfo.getUsername());
                RequestBody phoneRequestBody = RequestBody.create(MediaType.parse("text/plain"), userInfo.getPhone());
                RequestBody emailRequestBody = RequestBody.create(MediaType.parse("text/plain"), userInfo.getEmail());

                // If profile photo is updated, create the request body for image, latitude, and longitude
                InputStream inputStream = getContentResolver().openInputStream(pfp_uri);
                RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/*"), getBytesFromInputStream(inputStream));
                String fileName = "pfp_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000);
                MultipartBody.Part imagePart = MultipartBody.Part.createFormData("file", fileName, imageRequestBody);

                // Send the image file and other fields to the server
                retrofit2.Call<ResponseBody> call = uploadService.uploadInfo(imagePart, usernameRequestBody, phoneRequestBody, emailRequestBody);
                call.enqueue(new retrofit2.Callback<ResponseBody>() {
                    @Override
                    public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            // Image uploaded successfully
                            Toast.makeText(InfoHomeActivity.this, "Saved.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Handle error response
                            Toast.makeText(InfoHomeActivity.this, "Failed to save. Please check your network.", Toast.LENGTH_SHORT).show();
                            ToastHelper.showLongToast(getApplicationContext(), response.message(), Toast.LENGTH_LONG);
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                        // Handle network failure
                        ToastHelper.showLongToast(getApplicationContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG);
                    }
                });
            } else {
                // If profile photo is not updated, send only the text fields to the server
                // Create a JsonObject and add your data
                JsonObject data = new JsonObject();
                data.addProperty("username", userInfo.getUsername());
                data.addProperty("phone", userInfo.getPhone());
                data.addProperty("email", userInfo.getEmail());

                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), data.toString());

                retrofit2.Call<Void> call = uploadService.uploadInfoNoImage(requestBody);
                call.enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                        if (response.isSuccessful()) {
                            // Data saved successfully
                            Toast.makeText(InfoHomeActivity.this, "Saved.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Handle error response
                            Toast.makeText(InfoHomeActivity.this, "Failed to save. Please check your network.", Toast.LENGTH_SHORT).show();
                            ToastHelper.showLongToast(getApplicationContext(), response.message(), Toast.LENGTH_LONG);
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                        // Handle network failure
                        ToastHelper.showLongToast(getApplicationContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG);
                    }
                });
            }
        } catch (Exception e) {
            ToastHelper.showLongToast(InfoHomeActivity.this, "Failed to save. Please check your network. " + e.getMessage(), Toast.LENGTH_LONG);
        }
    }


    private byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[4 * 1024]; // Adjust the buffer size as needed
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, bytesRead);
        }
        return byteBuffer.toByteArray();
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
            try{
                Uri selectedImageUri = data.getData();
                // Call getLocation and then upload the image
                profileImageView.setImageURI(selectedImageUri);
                pfp_uri = selectedImageUri;
            } catch (Exception e){
                ToastHelper.showLongToast(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
            }

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


