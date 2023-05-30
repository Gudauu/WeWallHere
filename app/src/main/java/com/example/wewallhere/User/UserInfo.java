package com.example.wewallhere.User;

import android.net.Uri;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private String filename;
    private String username;
    private String phone;
    private String email;


    public UserInfo() {
        this.filename = null;
        this.username = "PotatoHead";
        this.phone = null;
        this.email = null;
    }

    public UserInfo(String filename, String username, String phone, String email) {
        this.filename = filename;
        this.username = username;
        this.phone = phone;
        this.email = email;
    }


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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
