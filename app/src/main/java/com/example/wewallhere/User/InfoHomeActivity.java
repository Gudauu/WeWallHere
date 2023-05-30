package com.example.wewallhere.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.wewallhere.R;


import androidx.appcompat.app.AppCompatActivity;

public class InfoHomeActivity extends AppCompatActivity {

    // Declare your views
    private ImageView profileImageView;
    private TextView usernameTextView;
    private TextView phoneTextView;
    private TextView emailTextView;
    private Button editButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_home);

        // Initialize your views
        profileImageView = findViewById(R.id.profileImageView);
        usernameTextView = findViewById(R.id.usernameTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        emailTextView = findViewById(R.id.emailTextView);
        editButton = findViewById(R.id.editButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Set the user's profile picture, username, phone, and email
        // Replace the placeholders with your actual data from shared preferences or any other source
        profileImageView.setImageResource(R.drawable.user_profile_pic);
        usernameTextView.setText("John Doe");
        phoneTextView.setText("Phone: +123456789");
        emailTextView.setText("Email: johndoe@example.com");
    }

    public void onEditProfileClicked(View view) {
        // Handle the edit profile button click
        // Start the activity to edit the profile information
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }

    public void onLogoutClicked(View view) {
        // Handle the log out button click
        // Start the PhoneVerificationActivity to log out the user
        Intent intent = new Intent(this, PhoneVerificationActivity.class);
        startActivity(intent);
        finish(); // Optional: Finish the current activity to prevent the user from coming back here after logging out
    }
}

