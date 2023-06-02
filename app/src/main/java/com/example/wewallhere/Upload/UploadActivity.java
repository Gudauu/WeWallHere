package com.example.wewallhere.Upload;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.example.wewallhere.ExploreByList.ExploreListActivity;
import com.example.wewallhere.R;
import com.example.wewallhere.User.InfoHomeActivity;
import com.example.wewallhere.gmaps.SingleLocation;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

    private static final int REQUEST_IMAGE_PICK_SEND = 1301;
    private static final int REQUEST_VIDEO_PICK_SEND = 1302;



    private SingleLocation singleLocation;


    private Button selectImageButton;
    private Button selectVideoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        iniBottomMenu();

        singleLocation = new SingleLocation(this);

        selectImageButton = findViewById(R.id.select_image_button);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImagePermissionCheck();
            }
        });
        selectVideoButton = findViewById(R.id.select_video_button);
        selectVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectVideoPermissionCheck();
            }
        });



    }

    private void iniBottomMenu(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.upload);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.explore) {
                startActivity(new Intent(UploadActivity.this, ExploreListActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            } else if (itemId == R.id.info) {
                startActivity(new Intent(UploadActivity.this, InfoHomeActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }

            return false;
        });

    }
    ////// location
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
        if (requestCode == REQUEST_IMAGE_PICK_SEND || requestCode == REQUEST_VIDEO_PICK_SEND) {
            // Check if all required permissions are granted, including media & location
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                if (requestCode == REQUEST_IMAGE_PICK_SEND){
                    startImagePickerIntent();
                }else if (requestCode == REQUEST_VIDEO_PICK_SEND){
                    startVideoPickerIntent();
                }
            } else {
                ToggleFreezeUserInteraction(false);
                Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    ////// media
    // media: check permission Select image & video from gallery
    private void selectImagePermissionCheck() {
        ToggleFreezeUserInteraction(true);
        // Check if the required permission to read external storage is granted
        if (checkImagePermission() && checkSingleLocationPermission()) {
            startImagePickerIntent();
        } else {
            // Request the necessary permissions
            String[] permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
            ActivityCompat.requestPermissions(this, permissions, REQUEST_IMAGE_PICK_SEND);
        }
    }
    private void selectVideoPermissionCheck() {
        ToggleFreezeUserInteraction(true);
        // Check if the necessary permissions are granted
        if (checkVideoPermission() && checkSingleLocationPermission()) {
            // Permissions are already granted, open the video selection intent
            startVideoPickerIntent();
        } else {
            // Request the necessary permissions
            String[] permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
            ActivityCompat.requestPermissions(this, permissions, REQUEST_VIDEO_PICK_SEND);
        }
    }
    // media: invoke picker
    private void startImagePickerIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK_SEND);
    }
    private void startVideoPickerIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");
        startActivityForResult(intent, REQUEST_VIDEO_PICK_SEND);
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
    private boolean checkImagePermission() {
        // Check if the required permissions are granted
        int readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        return readStoragePermission == PackageManager.PERMISSION_GRANTED;
    }
    //media: upload

    // media: picker result(uri) handler. Send to server
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ToggleFreezeUserInteraction(true);
        if (requestCode == REQUEST_IMAGE_PICK_SEND && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            // Call getLocation and then upload the image
            singleLocation.getLocation(new SingleLocation.LocationCallback() {
                @Override
                public void onLocationReceived(double latitude, double longitude) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(UploadActivity.this, ComposeActivity.class);
                            intent.putExtra("isVideo", false); // Replace true with the actual boolean value
                            intent.putExtra("Uri", selectedImageUri);
                            intent.putExtra("latitude", latitude);
                            intent.putExtra("longitude", longitude);
                            ToggleFreezeUserInteraction(false);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                    });
//                    uploadImageToServer(selectedImageUri, latitude, longitude);
                }
            });
        } else if (requestCode == REQUEST_VIDEO_PICK_SEND && resultCode == RESULT_OK && data != null) {
            Uri selectedVideoUri = data.getData();
            // Call getLocation and then upload the video
            singleLocation.getLocation(new SingleLocation.LocationCallback() {
                @Override
                public void onLocationReceived(double latitude, double longitude) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(UploadActivity.this, ComposeActivity.class);
                            intent.putExtra("isVideo", true); // Replace true with the actual boolean value
                            intent.putExtra("Uri", selectedVideoUri);
                            intent.putExtra("latitude", latitude);
                            intent.putExtra("longitude", longitude);
                            ToggleFreezeUserInteraction(false);
                            startActivity(intent);
                        }
                    });
//                    uploadVideoToServer(selectedVideoUri, latitude, longitude);
                }
            });
        } else{  // user cancelled selection
            ToggleFreezeUserInteraction(false);
        }
    }
    private void ToggleFreezeUserInteraction(boolean start) {
        if (start) {  // freeze
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            (this).findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);


        } else {  //unfreeze
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            //start loading icon
            this.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }
    }
    @Override
    public void onBackPressed() {
        // do nothing
    }



}
