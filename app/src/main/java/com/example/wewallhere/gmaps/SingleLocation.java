package com.example.wewallhere.gmaps;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import Helper.ToastHelper;

public class SingleLocation {
    private Context context;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Handler timeoutHandler;
    private static final long TIMEOUT_MILLISECONDS = 5000; // 5 seconds
    private double latitude;
    private double longitude;



    public SingleLocation(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Display the current latitude and longitude in a Toast message
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String message = "Latitude: " + latitude + "\nLongitude: " + longitude;
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                // Stop listening for location updates after receiving the first location
                locationManager.removeUpdates(this);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };
        timeoutHandler = new Handler(Looper.getMainLooper());
    }

    public void getLocation() {
        try {
            // Request location updates using the best available provider
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            // Set a timeout to stop location updates if no location is received within the specified time
            timeoutHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopLocationUpdates();
                    ToastHelper.showLongToast(context, "Location fetching timed out.\nPlease check you network and GPS settings.", Toast.LENGTH_LONG);
                }
            }, TIMEOUT_MILLISECONDS);
        } catch (SecurityException e) {
            ToastHelper.showLongToast(context, e.toString(), Toast.LENGTH_LONG);
        }
    }
    private void stopLocationUpdates() {
        locationManager.removeUpdates(locationListener);
        timeoutHandler.removeCallbacksAndMessages(null);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
