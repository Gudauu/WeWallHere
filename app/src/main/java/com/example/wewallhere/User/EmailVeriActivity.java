package com.example.wewallhere.User;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.example.wewallhere.R;
import com.sun.mail.smtp.SMTPTransport;
import android.os.AsyncTask;



import androidx.appcompat.app.AppCompatActivity;

import java.security.Security;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class EmailVeriActivity extends AppCompatActivity {

    private EditText inputEmail;
    private EditText inputVCode;
    private RelativeLayout loadingPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_verify);

        // Find views by their IDs
        inputEmail = findViewById(R.id.input_email);
        inputVCode = findViewById(R.id.input_vcode);
        loadingPanel = findViewById(R.id.loadingPanel);

        // Set OnClickListener for "Send verification code" button
        Button sendVCodeButton = findViewById(R.id.send_vcode);
        sendVCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode();
            }
        });

        // Set OnClickListener for "Verify and log in" button
        Button verifyButton = findViewById(R.id.verify);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCode();
            }
        });
    }

    private void verifyCode() {
        // Get the entered verification code
        String verificationCode = inputVCode.getText().toString().trim();

        // TODO: Implement code to verify the entered verification code

        // Show the loading panel
        loadingPanel.setVisibility(View.VISIBLE);

        // Simulating a delay for demonstration purposes (replace with actual verification logic)
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // Hide the loading panel
                        loadingPanel.setVisibility(View.GONE);
                        Toast.makeText(EmailVeriActivity.this, "Verification code verified", Toast.LENGTH_SHORT).show();
                    }
                },
                2000 // 2 seconds delay
        );
    }

    private String generateVerificationCode(){
        String vcode = "";  // clear the previous one
        for(int i=0;i<6;i++){
            vcode += String.valueOf(new Random().nextInt(10));
        }
        return vcode;
    }

    private void sendVerificationCode() {
        // Get the entered email
        final String email = inputEmail.getText().toString().trim();

        // Generate a random verification code (you can replace this with your own logic)
        final String verificationCode = generateVerificationCode();

        // Show the loading panel
        loadingPanel.setVisibility(View.VISIBLE);

        // Send the verification code via email
        EmailSender.sendVerificationCode(email, verificationCode);
    }


}



class EmailSender {
    private static final String SMTP_HOST = "smtp-relay.sendinblue.com";
    private static final int SMTP_PORT = 587;
    private static final String SMTP_USERNAME = "gudautd@gmail.com";
    private static final String SMTP_PASSWORD = "LIbFJh3YHnRr5ECA";

    public static void sendVerificationCode(String userEmail, String verificationCode) {
        new SendEmailTask().execute(userEmail, verificationCode);
    }

    private static class SendEmailTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String userEmail = params[0];
            String verificationCode = params[1];

            // Enable STARTTLS encryption for secure connection
            Security.setProperty("ssl.SocketFactory.provider", "com.sun.mail.util.TrustAllSocketFactory");

            // Configure SMTP properties
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", SMTP_HOST);
            properties.put("mail.smtp.port", SMTP_PORT);

            // Create a Session object
            Session session = Session.getInstance(properties);

            try {
                // Create a MimeMessage object
                MimeMessage message = new MimeMessage(session);

                // Set the sender's email address
                message.setFrom(new InternetAddress(SMTP_USERNAME));

                // Set the recipient's email address
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));

                // Set the email subject
                message.setSubject("Verification Code");

                // Set the email body
                message.setText("Your verification code is: " + verificationCode);

                // Create an SMTPTransport instance
                SMTPTransport transport = (SMTPTransport) session.getTransport("smtp");

                // Connect to the SMTP server using your credentials
                transport.connect(SMTP_HOST, SMTP_PORT, SMTP_USERNAME, SMTP_PASSWORD);

                // Send the email
                transport.sendMessage(message, message.getAllRecipients());

                // Close the connection
                transport.close();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                System.out.println("Verification code email sent successfully.");
            } else {
                System.out.println("Error sending verification code email.");
            }
        }
    }
}

