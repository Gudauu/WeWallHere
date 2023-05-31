package com.example.wewallhere.User;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wewallhere.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

import Helper.ToastHelper;

public class EmailVerificationActivity extends AppCompatActivity {
    private EditText inputEmail;
    private Button sendlinkButton;
    private Button verifyButton;
    private RelativeLayout loadingPanel;
    private ActionCodeSettings actionCodeSettings;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_verify);

        inputEmail = findViewById(R.id.input_email);
        verifyButton = findViewById(R.id.verify);
        sendlinkButton = findViewById(R.id.send_vlink);
        loadingPanel = findViewById(R.id.loadingPanel);

        firebaseAuth = FirebaseAuth.getInstance();

        actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        // URL you want to redirect back to. The domain (www.example.com) for this
                        // URL must be whitelisted in the Firebase Console.
                        .setUrl("https://www.example.com/finishSignUp?cartId=1234")
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setIOSBundleId("com.example.wewallhere")
                        .setAndroidPackageName(
                                "com.example.wewallhere",
                                true, /* installIfNotAvailable */
                                "12"    /* minimumVersion */)
                        .build();


        sendlinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                SharedPreferences prefs = getSharedPreferences("INFO", MODE_PRIVATE);
                prefs.edit().putString("email", email).commit();

                if(formatCheck(email)){
                    try{
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.sendSignInLinkToEmail(email, actionCodeSettings)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(Task<Void> task) {
                                        if(task.isSuccessful()){
                                            ToastHelper.showLongToast(getApplicationContext(), "Login link sent.", Toast.LENGTH_SHORT);
                                        }
                                    }
                                });
                    } catch (Exception e){
                        ToastHelper.showLongToast(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                    }


                }
            }
        });
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                Intent intent = getIntent();
                String emailLink = intent.getData().toString();

                // Confirm the link is a sign-in with email link.
                if (auth.isSignInWithEmailLink(emailLink)) {
                    // Retrieve this from wherever you stored it
                    SharedPreferences prefs = getSharedPreferences("INFO", MODE_PRIVATE);
                    String email = prefs.getString("email", "someemail@domain.com");

                    // The client SDK will parse the code from the link for you.
                    auth.signInWithEmailLink(email, emailLink)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        ToastHelper.showLongToast(getApplicationContext(), "Successfully signed in with email link!", Toast.LENGTH_SHORT);
                                        AuthResult result = task.getResult();
                                        // You can access the new user via result.getUser()
                                        // Additional user info profile *not* available via:
                                        // result.getAdditionalUserInfo().getProfile() == null
                                        // You can check if the user is new or existing:
                                        // result.getAdditionalUserInfo().isNewUser()
                                    } else {
                                        ToastHelper.showLongToast(getApplicationContext(), "Error signing in with email link", Toast.LENGTH_SHORT);
                                    }
                                }
                            });
                }
            }
        });

    }


    private boolean formatCheck(String email){
        // check email format
        if(email.length() == 0){
            ToastHelper.showLongToast(getApplicationContext(), "Email cannot be empty.", Toast.LENGTH_SHORT);
            return false;
        }
        return true;
    }
    private void showLoading() {
        loadingPanel.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        loadingPanel.setVisibility(View.GONE);
    }
}

