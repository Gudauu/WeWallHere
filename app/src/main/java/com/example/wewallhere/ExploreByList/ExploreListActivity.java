package com.example.wewallhere.ExploreByList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.wewallhere.R;
import com.example.wewallhere.Upload.UploadActivity;
import com.example.wewallhere.User.InfoHomeActivity;
import com.example.wewallhere.gmaps.ExploreMapActivity;
import com.example.wewallhere.gmaps.SingleLocation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Helper.ToastHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ExploreListActivity extends AppCompatActivity  implements SingleLocation.LocationCallback{
    private RecyclerView recyclerView;
    private MediaAdapter mediaAdapter;
    private TabLayout tabLayout;
    private Spinner dropdownMenu;
    private List<MongoMediaEntry> mongoMetaList = new ArrayList<>();
    private String url_media_service = "http://54.252.196.140:3000/";
    private String url_download = "http://54.252.196.140:3000/download/";
    private Toolbar topbar;
    private String media_type = "image";
    private boolean self_only = false;
    private SingleLocation singleLocation;
    private int REQUEST_SINGLE_LOCATION = 4277;
    private double latitude = 31;
    private double longitude = 121;
    private double ll_delta = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_explore_list);

        Intent intent = getIntent();
        self_only = intent.getBooleanExtra("self_only", false);


        initTopBar();
        iniBottomMenu();

        singleLocation = new SingleLocation(this);
        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create and set the adapter for empty mediaList
        mediaAdapter = new MediaAdapter(mongoMetaList, url_download, getApplicationContext());
        recyclerView.setAdapter(mediaAdapter);
//        updateMedia();
        updateBasedOnCondition();
    }
    private void initTopBar(){
        //          remove the top left app title
        //        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        topbar = findViewById(R.id.toolbar);
        // Set the toolbar as the action bar
        setSupportActionBar(topbar);
        tabLayout = findViewById(R.id.tabLayout);
        // Set up tabs for ExploreListActivity
        TabLayout.Tab listViewTab = tabLayout.newTab().setText("List View");
        TabLayout.Tab mapViewTab = tabLayout.newTab().setText("Map View");

        tabLayout.addTab(listViewTab);
        tabLayout.addTab(mapViewTab);

        listViewTab.select();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    // do nothing
                } else if (position == 1) {
                    // Handle the click on the "List View" tab
                    Intent listIntent = new Intent(ExploreListActivity.this, ExploreMapActivity.class);
                    listIntent.putExtra("self_only", self_only);
                    startActivity(listIntent);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Handle tab unselected event if needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle tab reselected event if needed
            }
        });


        // Set up dropdown menu options for image/video selection
        dropdownMenu = findViewById(R.id.dropdownMenu);
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"image", "video"});
        dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownMenu.setAdapter(dropdownAdapter);

        // Set up dropdown menu item selection listener
        dropdownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String new_media_type = parent.getItemAtPosition(position).toString();
                if(!new_media_type.equals(media_type)){
                    media_type = new_media_type;
                    updateBasedOnCondition();
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        dropdownMenu.setSelection(0); // Set the initial selection to the first item ("Images")




    }


    private void iniBottomMenu(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // if is viewing history, stay in "info" section
        if(!self_only) {
            bottomNavigationView.setSelectedItemId(R.id.explore);
        }else{
            bottomNavigationView.setSelectedItemId(R.id.info);

        }
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.upload) {
                startActivity(new Intent(ExploreListActivity.this, UploadActivity.class));
                return true;
            }  else if (itemId == R.id.info) {
                startActivity(new Intent(ExploreListActivity.this, InfoHomeActivity.class));
                return true;
            }  else if (itemId == R.id.explore && self_only) {  // it's viewing history in info tag actually.
                startActivity(new Intent(ExploreListActivity.this, ExploreListActivity.class));
                return true;
            }
//            } else if (itemId == R.id.navigation_item3) {
//                startActivity(new Intent(CurrentActivity.this, Activity3.class));
//                return true;
//            }

            return false;
        });

    }

    // ll_delta: search in range [latitude +- ll_delta, longitude +- ll_delta]
    private String geneFilter(){
        // Create a JSON filter based on your requirements
        String jsonFilter;

        if(self_only){
            SharedPreferences prefs = getSharedPreferences("INFO", MODE_PRIVATE);
            String email = prefs.getString("email", getString(R.string.default_email));
            jsonFilter = "{\"email\": \"" + email + "\"}";
        }else {
                double minLatitude = latitude - ll_delta;
                double maxLatitude = latitude + ll_delta;
                double minLongitude = longitude - ll_delta;
                double maxLongitude = longitude + ll_delta;
                // Format the latitude and longitude values with 8 decimal places
                String formattedMinLatitude = String.format("%.8f", minLatitude);
                String formattedMaxLatitude = String.format("%.8f", maxLatitude);
                String formattedMinLongitude = String.format("%.8f", minLongitude);
                String formattedMaxLongitude = String.format("%.8f", maxLongitude);

                // Construct the filter JSON string
                jsonFilter = String.format("{\"latitude\": {\"$gte\": %s, \"$lte\": %s}, \"longitude\": {\"$gte\": %s, \"$lte\": %s}}",
                        formattedMinLatitude, formattedMaxLatitude, formattedMinLongitude, formattedMaxLongitude);

        }
        return jsonFilter;


    }




    private void updateBasedOnCondition(){
        try{
            if(!self_only){
                // need location
                if (checkSingleLocationPermission()) {
                    // fetch location, then load
                    singleLocation.getLocation(new SingleLocation.LocationCallback() {
                        @Override
                        public void onLocationReceived(double la, double lo) {
                            latitude = la;
                            longitude = lo;
                            String filter = geneFilter();
                            updateMedia(filter);
                        }
                    });
                } else {
                    // Request the necessary permissions
                    String[] permissions = new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    };
                    ActivityCompat.requestPermissions(this, permissions, REQUEST_SINGLE_LOCATION);
                }

            }else{ // location doesn't matter
                String filter = geneFilter();
                updateMedia(filter);
            }
        }catch (Exception e){
            ToastHelper.showLongToast(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
        }

    }

    private void updateMedia(String filter) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url_media_service)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create a service interface for your API endpoints
        MongoMetaService mongoMetaService = retrofit.create(MongoMetaService.class);

        // Make an API call to retrieve media files
        Call<List<MongoMediaEntry>> call = mongoMetaService.getMetaDataList(media_type, filter);  // , "image_1684667427711_388"
        call.enqueue(new Callback<List<MongoMediaEntry>>() {
            @Override
            public void onResponse(Call<List<MongoMediaEntry>> call, Response<List<MongoMediaEntry>> response) {
                if (response.isSuccessful()) {
                    List<MongoMediaEntry> mediaEntries = response.body();
                    Collections.reverse(mediaEntries);

                    // Handle the retrieved media entries
                    mongoMetaList.clear();
                    mongoMetaList.addAll(mediaEntries);
                    updateRecyclerView();

                } else {
                    ToastHelper.showLongToast(getApplicationContext(), response.message(), Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<List<MongoMediaEntry>> call, Throwable t) {
                // Handle network or other errors
                ToastHelper.showLongToast(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
            }
        });

    }

    private void updateRecyclerView() {
        // Create a new adapter with the updated media list
        mediaAdapter = new MediaAdapter(mongoMetaList, url_download, getApplicationContext());
        recyclerView.setAdapter(mediaAdapter);
    }





    private boolean checkSingleLocationPermission(){
        int fine_location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarse_location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        return fine_location == PackageManager.PERMISSION_GRANTED &&
                coarse_location == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onLocationReceived(double latitude, double longitude) {
        // Handle the received latitude and longitude
        String message = "Latitude: " + latitude + "\nLongitude: " + longitude;
        ToastHelper.showLongToast(getApplicationContext(), message, Toast.LENGTH_SHORT);
    }
    // permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SINGLE_LOCATION) {
            // Check if all required permissions are granted, including media & location
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                updateBasedOnCondition();
            } else {
                ToastHelper.showLongToast(this, "Location permission not granted. \nRecommended location used.", Toast.LENGTH_LONG);
            }
        }
    }




}
