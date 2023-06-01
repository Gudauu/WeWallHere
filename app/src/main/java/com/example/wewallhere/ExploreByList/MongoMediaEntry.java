package com.example.wewallhere.ExploreByList;

import com.example.wewallhere.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MongoMediaEntry implements Serializable {
    private String ID;
    private String filename;
    private String path;
    private double latitude;
    private double longitude;
    private String timestamp;
    private String title;
    private String content;
    private String username;
    private String email;
    private int comment_count;


    public MongoMediaEntry(String ID, String filename, String path, double latitude, double longitude,
                           String timestamp,String title, String content, String uploaderName, String uploaderEmail,
                           int comment_count) {
        this.ID = ID;
        this.filename = filename;
        this.path = path;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.title = title;
        this.content = content;
        this.username = uploaderName;
        this.email = uploaderEmail;
        this.comment_count = comment_count;
    }

    public String getID() {
        return ID;
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

    public String getUploaderEmail() {
        return email;
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
        return username;
    }
}


