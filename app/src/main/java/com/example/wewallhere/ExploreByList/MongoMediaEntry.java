package com.example.wewallhere.ExploreByList;

import com.example.wewallhere.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MongoMediaEntry {
    private String filename;
    private String path;
    private double latitude;
    private double longitude;
    private String timestamp;
    private String title;
    private String content;
    private String uploaderName;
    private int comment_count;


    public MongoMediaEntry(String filename, String path, double latitude, double longitude,
                           String timestamp,String title, String content, String uploaderName,
                           int comment_count) {
        this.filename = filename;
        this.path = path;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.title = title;
        this.content = content;
        this.uploaderName = uploaderName;
        this.comment_count = comment_count;
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

    public String getTimestamp() {
        // Define the input and output date formats
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.CHINA);
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

        try {
            // Parse the input timestamp string into a Date object
            Date timestampDate = inputFormat.parse(timestamp);
            // Format the Date object into the desired output format
            String formattedTimestamp = outputFormat.format(timestampDate);
            return formattedTimestamp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getUploaderName() {
        return uploaderName;
    }
}


