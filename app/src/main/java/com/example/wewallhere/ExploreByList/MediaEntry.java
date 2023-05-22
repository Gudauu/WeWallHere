package com.example.wewallhere.ExploreByList;

import android.net.Uri;

import com.example.wewallhere.R;

public class MediaEntry {
    private boolean isVideo;
    private Uri mediaUri;
    private int profilePicResId;
    private String title;
    private String uploaderName;
    private String uploadDate;



    public MediaEntry() {
        this.isVideo = false;
        this.mediaUri = null;
        this.profilePicResId = R.drawable.ic_launcher_foreground;
        this.title = "Too busy for trivial titles";
        this.uploaderName = "Nobody";
        this.uploadDate = "2023-01-01";
    }
    public MediaEntry(boolean isVideo, Uri mediaUri, int profilePicResId, String title, String uploaderName, String uploadDate) {
        this.isVideo = isVideo;
        this.mediaUri = mediaUri;
        this.profilePicResId = profilePicResId;
        this.title = title;
        this.uploaderName = uploaderName;
        this.uploadDate = uploadDate;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public Uri getMediaUri() {
        return mediaUri;
    }

    public int getProfilePicResId() {
        return profilePicResId;
    }

    public String getTitle() {
        return title;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public String getUploadDate() {
        return uploadDate;
    }
    public void setVideo(boolean video) {
        isVideo = video;
    }

    public void setMediaUri(Uri mediaUri) {
        this.mediaUri = mediaUri;
    }

    public void setProfilePicResId(int profilePicResId) {
        this.profilePicResId = profilePicResId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }


}


