package com.example.wewallhere.User;

import android.net.Uri;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private Uri uri_pfp;
    private String username;
    private String phone;
    private String email;


    public UserInfo() {
        this.uri_pfp = null;
        this.username = "PotatoHead";
        this.phone = null;
        this.email = null;
    }

    public UserInfo(Uri uri_pfp, String username, String phone, String email) {
        this.uri_pfp = uri_pfp;
        this.username = username;
        this.phone = phone;
        this.email = email;
    }

    public Uri getUri_pfp() {
        return uri_pfp;
    }

    public void setUri_pfp(Uri uri_pfp) {
        this.uri_pfp = uri_pfp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
