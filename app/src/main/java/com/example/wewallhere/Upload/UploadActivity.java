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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
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
import com.example.wewallhere.gmaps.SingleLocation;

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

public class UploadActivity extends AppCompatActivity implements SingleLocation.LocationCallback {

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_SELECT_VIDEO = 1001;
    private static final int REQUEST_SINGLE_LOCATION = 1002;

    private SingleLocation singleLocation;


    private Button selectImageButton;
    private Button selectVideoButton;
    private  Button testSingleLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        singleLocation = new SingleLocation(this);

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

        testSingleLocationButton = findViewById(R.id.fetch_single_location);
        testSingleLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchSingleLocation();
            }
        });

    }


    ////// location
    private void fetchSingleLocation() {
        if (checkSingleLocationPermission()) {
            singleLocation.getLocation(UploadActivity.this);
        } else {
            // Request the necessary permissions
            String[] permissions = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
            ActivityCompat.requestPermissions(this, permissions, REQUEST_SINGLE_LOCATION);
        }
    }
    @Override
    public void onLocationReceived(double latitude, double longitude) {
        // Handle the received latitude and longitude
        String message = "Latitude: " + latitude + "\nLongitude: " + longitude;
        ToastHelper.showLongToast(getApplicationContext(), message, Toast.LENGTH_SHORT);
    }
    private boolean checkSingleLocationPermission(){
        int fine_location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarse_location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        return fine_location == PackageManager.PERMISSION_GRANTED &&
                coarse_location == PackageManager.PERMISSION_GRANTED;
    }

    // permission
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
                startVideoPickerIntent();
            } else {
                Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == REQUEST_SINGLE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                singleLocation.getLocation(UploadActivity.this);
            } else {
                ToastHelper.showLongToast(getApplicationContext(), "Location permissions not granted.", Toast.LENGTH_LONG);
            }
        }
    }


    ////// media
    // media: check permission Select image & video from gallery
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
    // media: invoke picker
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
    // media: permission
    private boolean checkVideoPermission() {
        // Check if the required permissions are granted
        int readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        return readStoragePermission == PackageManager.PERMISSION_GRANTED &&
                writeStoragePermission == PackageManager.PERMISSION_GRANTED &&
                cameraPermission == PackageManager.PERMISSION_GRANTED;
    }

    // media: picker result(uri) handler
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            // Call getLocation before uploading the image
            singleLocation.getLocation(new SingleLocation.LocationCallback() {
                @Override
                public void onLocationReceived(double latitude, double longitude) {
                    // Location received, now you can upload the image
                    String message = "Latitude: " + latitude + "\nLongitude: " + longitude;
                    ToastHelper.showLongToast(getApplicationContext(), message, Toast.LENGTH_SHORT);
                    uploadImageToServer(selectedImageUri, latitude, longitude);
                }
            });
        }else if (requestCode == REQUEST_SELECT_VIDEO && resultCode == RESULT_OK && data != null) {
            Uri selectedVideoUri = data.getData();
            // Call getLocation before uploading the video
            singleLocation.getLocation(new SingleLocation.LocationCallback() {
                @Override
                public void onLocationReceived(double latitude, double longitude) {
                    // Location received, now you can upload the video
                    String message = "Latitude: " + latitude + "\nLongitude: " + longitude;
                    ToastHelper.showLongToast(getApplicationContext(), message, Toast.LENGTH_SHORT);
                    uploadVideoToServer(selectedVideoUri, latitude, longitude);
                }
            });
        }
    }

    //media: upload
    private void uploadImageToServer(Uri imageUri, double latitude, double longitude) {
        try {
            // Create a Retrofit instance
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://54.252.196.140:3000") // Replace with your server's IP address
                    .build();

            // Create the request body for image, latitude and longitude
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), getBytesFromInputStream(inputStream));
            String fileName = "image_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("file", fileName, requestBody);

            RequestBody latitudeBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(latitude));
            RequestBody longitudeBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(longitude));

            // Create an instance of the API service interface
            ApiService apiService = retrofit.create(ApiService.class);

            // Send the image file to the server
            Call<ResponseBody> call = apiService.uploadImage(imagePart, latitudeBody, longitudeBody);
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
    private void uploadVideoToServer(Uri videoUri,  double latitude, double longitude){
        try{
            // Create the Retrofit instance
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://54.252.196.140:3000")
                    .build();



            // Create the request body for video, latitude and longitude
            InputStream inputStream = getContentResolver().openInputStream(videoUri);
            RequestBody requestBody = RequestBody.create(MediaType.parse("video/*"), getBytesFromInputStream(inputStream));
            String fileName = "video_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000);
            MultipartBody.Part videoPart = MultipartBody.Part.createFormData("file", fileName, requestBody);

            RequestBody latitudeBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(latitude));
            RequestBody longitudeBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(longitude));

            // Create the API service interface
            ApiService apiService = retrofit.create(ApiService.class);

            // Create the API call to upload the video
            Call<ResponseBody> call = apiService.uploadVideo(videoPart, latitudeBody, longitudeBody);

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
