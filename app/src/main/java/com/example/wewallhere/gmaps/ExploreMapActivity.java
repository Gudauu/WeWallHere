package com.example.wewallhere.gmaps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.wewallhere.ExploreByList.ExploreListActivity;
import com.example.wewallhere.ExploreByList.MongoMetaService;
import com.example.wewallhere.R;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.wewallhere.ExploreByList.MongoMediaEntry;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import Helper.ToastHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExploreMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private MapView mapView;

    private TabLayout tabLayout;
    private Toolbar topbar;
    private Spinner dropdownMenu;
    private List<MongoMediaEntry> mongoMetaList = new ArrayList<>();
    private String url_media_service = "http://54.252.196.140:3000/";

    private String media_type = "image";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_maps);
        initTopBar();

        // Obtain the MapView and initialize it
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }
    private void initTopBar(){
        // remove the top left app title
//        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        // Initialize toolbar view
        topbar = findViewById(R.id.toolbar);
        // Set the toolbar as the action bar
        setSupportActionBar(topbar);
        tabLayout = findViewById(R.id.tabLayout);
        // Set up tabs for ExploreListActivity
        TabLayout.Tab listViewTab = tabLayout.newTab().setText("List View");
        TabLayout.Tab mapViewTab = tabLayout.newTab().setText("Map View");

        tabLayout.addTab(listViewTab);
        tabLayout.addTab(mapViewTab);
        mapViewTab.select();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    // Handle the click on the "List View" tab
                    Intent listIntent = new Intent(ExploreMapActivity.this, ExploreListActivity.class);
                    startActivity(listIntent);
                } else if (position == 1) {
                    // do nothing
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
                    updateMedia();
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        dropdownMenu.setSelection(0); // Set the initial selection to the first item ("Images")

    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Enable the user's current location on the map
        if (!checkSingleLocationPermission()) {
            ToastHelper.showLongToast(getApplicationContext(), "No location permission.", Toast.LENGTH_SHORT);
            return;
        }
        googleMap.setMyLocationEnabled(true);
        // Add markers for media files on the map
        updateMedia();
    }


    private void addMediaMarkers() {
        // Retrieve the list of media files with their latitude and longitude
        // Add markers for each media file on the map
        for (MongoMediaEntry media : mongoMetaList) {
            LatLng position = new LatLng(media.getLatitude(), media.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(position)
                    .title(media.getTitle())
                    .snippet(media.getUploaderName());
            googleMap.addMarker(markerOptions);
        }
    }

    private void updateMedia() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url_media_service)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create a service interface for your API endpoints
        MongoMetaService mongoMetaService = retrofit.create(MongoMetaService.class);

        // Make an API call to retrieve media files
        Call<List<MongoMediaEntry>> call = mongoMetaService.getMetaDataList(media_type);  // , "image_1684667427711_388"
        call.enqueue(new Callback<List<MongoMediaEntry>>() {
            @Override
            public void onResponse(Call<List<MongoMediaEntry>> call, Response<List<MongoMediaEntry>> response) {
                if (response.isSuccessful()) {
                    List<MongoMediaEntry> mediaEntries = response.body();
                    // Handle the retrieved media entries
                    mongoMetaList.clear();
                    mongoMetaList.addAll(mediaEntries);
                    addMediaMarkers();

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


    private boolean checkSingleLocationPermission(){
        int fine_location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarse_location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        return fine_location == PackageManager.PERMISSION_GRANTED &&
                coarse_location == PackageManager.PERMISSION_GRANTED;
    }



    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
