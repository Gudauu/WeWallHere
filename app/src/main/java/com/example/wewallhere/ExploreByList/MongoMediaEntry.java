package com.example.wewallhere.ExploreByList;

import com.example.wewallhere.R;

public class MongoMediaEntry {
    private String filename;
    private String path;
    private double latitude;
    private double longitude;
    private String date;


    public MongoMediaEntry(String filename, String path, double latitude, double longitude, String date) {
        this.filename = filename;
        this.path = path;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    public String getFilename() {
        return filename;
    }

    public String getPath() {
        return path;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDate() {
        return date;
    }
}


