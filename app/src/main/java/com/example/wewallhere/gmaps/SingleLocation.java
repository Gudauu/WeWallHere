package com.example.wewallhere.gmaps;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;

import Helper.ToastHelper;

public class SingleLocation {
    private Context context;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Handler timeoutHandler;
    private LocationCallback locationCallback;
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

                // Cancel the timeout handler as a location update has been received
                timeoutHandler.removeCallbacksAndMessages(null);

                // Stop listening for location updates after receiving the first location
                locationManager.removeUpdates(this);

                // Check if a callback is registered and notify MainActivity
                if (locationCallback != null) {
                    locationCallback.onLocationReceived(latitude, longitude);
                }

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

    public void getLocation(LocationCallback callbackActicity) {
        this.locationCallback = callbackActicity;
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

    // call back functions in the previous activity that handles the new location
    public interface LocationCallback {
        void onLocationReceived(double latitude, double longitude);
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
