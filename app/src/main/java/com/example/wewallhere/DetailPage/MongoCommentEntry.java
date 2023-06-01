package com.example.wewallhere.DetailPage;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MongoCommentEntry implements Serializable {
    private String ID;
    private String ID_reply;
    private String type;
    private String filename;
    private String path;
    private String timestamp;
    private String title;
    private String content;
    private String username;
    private String email;
    private int comment_count;




    public MongoCommentEntry(String ID, String ID_reply, String filename, String path, String type,
                             String timestamp, String title, String content, String uploaderName, String email,
                             int comment_count) {
        this.ID = ID;
        this.type = type;
        this.ID_reply = ID_reply;
        this.filename = filename;
        this.path = path;
        this.timestamp = timestamp;
        this.title = title;
        this.content = content;
        this.username = uploaderName;
        this.email = email;
        this.comment_count = comment_count;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
    public String getID() {
        return ID;
    }
    public String getType() {
        return type;
    }
    public String getID_reply() {
        return ID_reply;
    }

    public String getFilename() {
        return filename;
    }

    public String getPath() {
        return path;
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
