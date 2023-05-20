package com.example.wewallhere.Upload;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.wewallhere.Main.MainActivity;
import com.example.wewallhere.R;

import java.io.IOException;

import Helper.ToastHelper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UploadActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_SELECT_VIDEO = 1001;


    private Button selectImageButton;
    private Button selectVideoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        selectImageButton = findViewById(R.id.select_image_button);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromAlbum();
            }
        });
        selectVideoButton = findViewById(R.id.select_video_button);
        selectVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectVideoFromGallery();
            }
        });
    }

    private void selectImageFromAlbum() {
        // Check if the required permission to read external storage is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_IMAGE_PICK);
        } else {
            // Permission already granted, start the image picker
            startImagePickerIntent();
        }
    }
    private void selectVideoFromGallery() {
        // Check if the necessary permissions are granted
        if (checkVideoPermission()) {
            // Permissions are already granted, open the video selection intent
            startVideoPickerIntent();
        } else {
            // Request the necessary permissions
            String[] permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            };
            ActivityCompat.requestPermissions(this, permissions, REQUEST_SELECT_VIDEO);
        }
    }

    private boolean checkVideoPermission() {
        // Check if the required permissions are granted
        int readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        return readStoragePermission == PackageManager.PERMISSION_GRANTED &&
                writeStoragePermission == PackageManager.PERMISSION_GRANTED &&
                cameraPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void startImagePickerIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void startVideoPickerIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");
        startActivityForResult(intent, REQUEST_SELECT_VIDEO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_PICK) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start the image picker
                startImagePickerIntent();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_SELECT_VIDEO) {
            // Check if all required permissions are granted
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // All permissions are granted, open the video selection intent
                startVideoPickerIntent();
            } else {
                // Permissions are not granted, show a message or take appropriate action
                Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_SHORT).show();
            }
        }
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            // Now you have the selected image URI, you can upload it to the server
            uploadImageToServer(selectedImageUri);
        }else if (requestCode == REQUEST_SELECT_VIDEO && resultCode == RESULT_OK && data != null) {
            Uri selectedVideoUri = data.getData();
            uploadVideoToServer(selectedVideoUri);
        }
    }
    private String getVideoFilePath(Uri videoUri) {
        String filePath = null;
        String[] projection = {MediaStore.Video.Media.DATA};
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(videoUri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }
        return filePath;
    }


    private String getImageFilePath(Uri imageUri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(imageUri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }
        return filePath;
    }

    private void uploadImageToServer(Uri imageUri) {
        // Implement the logic to upload the image to the server here
        // You can use libraries like Retrofit or Volley to make the HTTP request to the server
        // Include the imageUri as a parameter in the request

        // Example code:
        try {
            // Resolve the image URI to a file path
            String filePath = getImageFilePath(imageUri);

            if (filePath == null) {
                Toast.makeText(this, "Failed to get image file path", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a Retrofit instance
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://54.252.196.140:3000") // Replace with your server's IP address
                    .build();

            // Create an instance of the API service interface
            ApiService apiService = retrofit.create(ApiService.class);

            // Create a file from the image URI
            File imageFile = new File(filePath);

            // Create a request body with the image file
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);

            // Create a MultipartBody.Part from the request body
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);

            // Send the image file to the server
            Call<ResponseBody> call = apiService.uploadImage(imagePart);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        // Image uploaded successfully
                        Toast.makeText(UploadActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle error response
                        Toast.makeText(UploadActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
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
            ToastHelper.showLongToast(UploadActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG);
        }
    }
    private void uploadVideoToServer(Uri videoUri){
        try{
            // Create a file object from the video URI
            String filePath = getVideoFilePath(videoUri);
            File videoFile = new File(filePath);

            // Create a request body with the video file
            RequestBody requestBody = RequestBody.create(MediaType.parse("video/*"), videoFile);

            // Create a multipart request body part with the request body
            MultipartBody.Part videoPart = MultipartBody.Part.createFormData("file", videoFile.getName(), requestBody);

            // Create the Retrofit instance
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://54.252.196.140:3000")
                    .build();

            // Create the API service interface
            ApiService apiService = retrofit.create(ApiService.class);

            // Create the API call to upload the video
            Call<ResponseBody> call = apiService.uploadVideo(videoPart);

            // Enqueue the API call
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        // Video uploaded successfully
                        Toast.makeText(UploadActivity.this, "Video uploaded successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // Video upload failed
                        Toast.makeText(UploadActivity.this, "video upload failed:", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Handle the upload failure
                    ToastHelper.showLongToast(UploadActivity.this, "Video upload failed: " + t.getMessage(), Toast.LENGTH_LONG);
                }
            });
        } catch (Exception e){
            ToastHelper.showLongToast(UploadActivity.this, "Video upload failed:" + e, Toast.LENGTH_LONG);

        }

    }
}
