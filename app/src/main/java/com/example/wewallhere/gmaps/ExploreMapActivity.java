package com.example.wewallhere.gmaps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wewallhere.DetailPage.DetailPageActivity;
import com.example.wewallhere.ExploreByList.ExploreListActivity;
import com.example.wewallhere.ExploreByList.MongoMetaService;
import com.example.wewallhere.Main.MainActivity;
import com.example.wewallhere.R;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.wewallhere.ExploreByList.MongoMediaEntry;
import com.example.wewallhere.Upload.UploadActivity;
import com.example.wewallhere.User.InfoHomeActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    private Marker selectedMarker = null;


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
        iniBottomMenu();

        // Obtain the MapView and initialize it
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }
    private void initTopBar(){
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

    private void iniBottomMenu(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.explore);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.upload) {
                startActivity(new Intent(ExploreMapActivity.this, UploadActivity.class));
                return true;
            }  else if (itemId == R.id.info) {
                startActivity(new Intent(ExploreMapActivity.this, InfoHomeActivity.class));
                return true;
            }
//            } else if (itemId == R.id.navigation_item3) {
//                startActivity(new Intent(CurrentActivity.this, Activity3.class));
//                return true;
//            }

            return false;
        });

    }
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Enable the user's current location on the map
        if (!checkSingleLocationPermission()) {
            ToastHelper.showLongToast(getApplicationContext(), "No location permission.", Toast.LENGTH_SHORT);
            Intent intent = new Intent();
            intent.setClass(ExploreMapActivity.this, MainActivity.class);
            startActivity(intent);
            return;
        }
        // Enable the "My Location" button
        googleMap.setMyLocationEnabled(true);

        // Set a click listener on the "My Location" button to center the map
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                // Center the map around the user's current location
                if (ContextCompat.checkSelfPermission(ExploreMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Get the user's current location
                    FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(ExploreMapActivity.this);
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        // Center the map on the user's location
                                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12f);
                                        googleMap.animateCamera(cameraUpdate);
                                    }
                                }
                            });
                }
                return true;
            }
        });

        // Add markers for media files on the map
        updateMedia();
    }


    private void updateMediaMarkers() {
        // Retrieve the list of media files with their latitude and longitude
        // Add markers for each media file on the map
        googleMap.clear();
        for (MongoMediaEntry media : mongoMetaList) {
            LatLng position = new LatLng(media.getLatitude(), media.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(position)
                    .title(media.getTitle())
                    .snippet(media.getUploaderName());
            Marker marker = googleMap.addMarker(markerOptions);
            marker.setTag(media); // Set the media entry as the tag of the marker
        }

        // Set a custom info window adapter for the markers
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null; // Return null to use the default info window layout
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate a custom info window layout
                View infoWindow = getLayoutInflater().inflate(R.layout.item_map_marker_info, null);

                // Find the views within the custom info window layout
                TextView titleTextView = infoWindow.findViewById(R.id.titleTextView);
                TextView uploaderTextView = infoWindow.findViewById(R.id.uploaderTextView);

                // Retrieve the media entry from the marker's tag
                MongoMediaEntry media = (MongoMediaEntry) marker.getTag();

                // Set the title and uploader name in the custom info window layout
                titleTextView.setText(media.getTitle());
                uploaderTextView.setText(media.getUploaderName());

                return infoWindow;
            }
        });

        // Set an info window click listener for the markers
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // Retrieve the media entry from the marker's tag
                MongoMediaEntry media = (MongoMediaEntry) marker.getTag();
                Intent intent = new Intent(ExploreMapActivity.this, DetailPageActivity.class);
                // Pass the selected media entry as an extra to the new activity
                intent.putExtra("MongoMediaEntry", media);
                startActivity(intent);
            }
        });
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
                    updateMediaMarkers();

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

    // go to main page when scrolling back
    @Override
    public void onBackPressed() {
        // Start the main activity or perform any other navigation action
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear all previous activities
        startActivity(intent);
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

