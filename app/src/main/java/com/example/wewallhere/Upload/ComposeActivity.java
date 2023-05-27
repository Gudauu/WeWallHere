package com.example.wewallhere.Upload;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wewallhere.R;


import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import Helper.ToastHelper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ComposeActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextContent;
    private Button buttonUpload;
    private Boolean isVideo;
    private Uri fileUri;
    private double latitude;
    private double longitude;
    private String url_upload = "http://54.252.196.140:3000/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        // Get the boolean value from the intent
        Intent intent = getIntent();
        isVideo = intent.getBooleanExtra("isVideo", false);
        fileUri = intent.getParcelableExtra("Uri");
        latitude = intent.getDoubleExtra("latitude", 0.0);
        longitude = intent.getDoubleExtra("longitude", 0.0);

        // Initialize views
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        buttonUpload = findViewById(R.id.buttonUpload);



        // Set click listener for buttonUpload
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString();
                String content = editTextContent.getText().toString();

                if (title.length() == 0) {
                    ToastHelper.showLongToast(getApplicationContext(), "Title cannot be empty.", Toast.LENGTH_SHORT);
                    return;
                }
                if (content.length() == 0) {
                    ToastHelper.showLongToast(getApplicationContext(), "Content cannot be empty.", Toast.LENGTH_SHORT);
                    return;
                }
                String message = "Latitude: " + latitude + "\nLongitude: " + longitude;
                ToastHelper.showLongToast(getApplicationContext(), message, Toast.LENGTH_SHORT);
                if(isVideo){
                    uploadVideoToServer(fileUri, latitude, longitude, title, content);
                }else{
                    uploadImageToServer(fileUri, latitude, longitude, title, content);
                }


            }
        });
    }
    private void uploadImageToServer(Uri imageUri, double latitude, double longitude, String title, String content) {
        try {
            // Create a Retrofit instance
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url_upload) // Replace with your server's IP address
                    .build();

            // Create the request body for image, latitude and longitude
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), getBytesFromInputStream(inputStream));
            String fileName = "image_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("file", fileName, requestBody);

            RequestBody latitudeBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(latitude));
            RequestBody longitudeBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(longitude));

            RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
            RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"), content);


            // Create an instance of the API service interface
            UploadService uploadService = retrofit.create(UploadService.class);

            // Send the image file to the server
            Call<ResponseBody> call = uploadService.uploadImage(imagePart, latitudeBody, longitudeBody,titleBody, contentBody);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        // Image uploaded successfully
                        Toast.makeText(ComposeActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle error response
                        Toast.makeText(ComposeActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        ToastHelper.showLongToast(getApplicationContext(), response.message(),Toast.LENGTH_LONG);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Handle network failure
                    ToastHelper.showLongToast(getApplicationContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG);
                }
            });
        } catch (Exception e) {
            ToastHelper.showLongToast(ComposeActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG);
        }
    }
    // helper function in video upload
    private byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[4 * 1024]; // Adjust the buffer size as needed
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, bytesRead);
        }
        return byteBuffer.toByteArray();
    }
    private void uploadVideoToServer(Uri videoUri,  double latitude, double longitude, String title, String content){
        try{
            // Create the Retrofit instance
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url_upload)
                    .build();



            // Create the request body for video, latitude and longitude
            InputStream inputStream = getContentResolver().openInputStream(videoUri);
            RequestBody requestBody = RequestBody.create(MediaType.parse("video/*"), getBytesFromInputStream(inputStream));
            String fileName = "video_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000);
            MultipartBody.Part videoPart = MultipartBody.Part.createFormData("file", fileName, requestBody);

            RequestBody latitudeBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(latitude));
            RequestBody longitudeBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(longitude));

            RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
            RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"), content);


            // Create the API service interface
            UploadService uploadService = retrofit.create(UploadService.class);

            // Create the API call to upload the video
            Call<ResponseBody> call = uploadService.uploadVideo(videoPart, latitudeBody, longitudeBody,titleBody, contentBody);

            // Enqueue the API call
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        // Video uploaded successfully
                        Toast.makeText(ComposeActivity.this, "Video uploaded successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // Video upload failed
                        Toast.makeText(ComposeActivity.this, "video upload failed:", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Handle the upload failure
                    ToastHelper.showLongToast(ComposeActivity.this, "Video upload failed: " + t.getMessage(), Toast.LENGTH_LONG);
                }
            });
        } catch (Exception e){
            ToastHelper.showLongToast(ComposeActivity.this, "Video upload failed:" + e, Toast.LENGTH_LONG);

        }

    }

}

