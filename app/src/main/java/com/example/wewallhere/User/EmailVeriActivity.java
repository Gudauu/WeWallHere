//package com.example.wewallhere.User;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//import com.example.wewallhere.R;
//
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.util.Properties;
//import java.util.Random;
//
//import javax.mail.Authenticator;
//import javax.mail.Message;
//import javax.mail.MessagingException;
//import javax.mail.PasswordAuthentication;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
//
//public class EmailVeriActivity extends AppCompatActivity {
//
//    private EditText inputEmail;
//    private EditText inputVCode;
//    private RelativeLayout loadingPanel;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_mail_verify);
//
//        // Find views by their IDs
//        inputEmail = findViewById(R.id.input_email);
//        inputVCode = findViewById(R.id.input_vcode);
//        loadingPanel = findViewById(R.id.loadingPanel);
//
//        // Set OnClickListener for "Send verification code" button
//        Button sendVCodeButton = findViewById(R.id.send_vcode);
//        sendVCodeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendVerificationCode();
//            }
//        });
//
//        // Set OnClickListener for "Verify and log in" button
//        Button verifyButton = findViewById(R.id.verify);
//        verifyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                verifyCode();
//            }
//        });
//    }
////
////    private void sendVerificationCode() {
////        // Get the entered email
////        String email = inputEmail.getText().toString().trim();
////
////        // TODO: Implement code to send the verification code to the email
////
////        sendEmail(email, "WeWallHere login", "your code is: 551");
////        // Show the loading panel
////        loadingPanel.setVisibility(View.VISIBLE);
////
////        // Simulating a delay for demonstration purposes (replace with actual email sending logic)
////        new android.os.Handler().postDelayed(
////                new Runnable() {
////                    public void run() {
////                        // Hide the loading panel
////                        loadingPanel.setVisibility(View.GONE);
////                        Toast.makeText(EmailVeriActivity.this, "Verification code sent to " + email, Toast.LENGTH_SHORT).show();
////                    }
////                },
////                2000 // 2 seconds delay
////        );
////    }
//
//    private void verifyCode() {
//        // Get the entered verification code
//        String verificationCode = inputVCode.getText().toString().trim();
//
//        // TODO: Implement code to verify the entered verification code
//
//        // Show the loading panel
//        loadingPanel.setVisibility(View.VISIBLE);
//
//        // Simulating a delay for demonstration purposes (replace with actual verification logic)
//        new android.os.Handler().postDelayed(
//                new Runnable() {
//                    public void run() {
//                        // Hide the loading panel
//                        loadingPanel.setVisibility(View.GONE);
//                        Toast.makeText(EmailVeriActivity.this, "Verification code verified", Toast.LENGTH_SHORT).show();
//                    }
//                },
//                2000 // 2 seconds delay
//        );
//    }
//
//    private String generateVerificationCode(){
//        String vcode = "";  // clear the previous one
//        for(int i=0;i<6;i++){
//            vcode += String.valueOf(new Random().nextInt(10));
//        }
//        return vcode;
//    }
//
////    private void sendVerificationCode() {
////        // Get the entered email
////        final String email = inputEmail.getText().toString().trim();
////
////        // Generate a random verification code (you can replace this with your own logic)
////        final String verificationCode = generateVerificationCode();
////
////        // Show the loading panel
////        loadingPanel.setVisibility(View.VISIBLE);
////
////        // Send the verification code via email using JavaMail API
////        new Thread(new Runnable() {
////            public void run() {
////                try {
////                    // Set up mail server properties
////                    Properties properties = new Properties();
////                    properties.put("mail.smtp.auth", "true");
////                    properties.put("mail.smtp.starttls.enable", "true");
////                    properties.put("mail.smtp.host", "smtp.example.com"); // Replace with your SMTP server
////                    properties.put("mail.smtp.port", "587"); // Replace with the appropriate port number
////
////                    // Create a session with authentication
////                    Session session = Session.getInstance(properties, new Authenticator() {
////                        @Override
////                        protected PasswordAuthentication getPasswordAuthentication() {
////                            // Replace with your email credentials
////                            return new PasswordAuthentication("your_email@example.com", "your_password");
////                        }
////                    });
////
////                    // Create a new message
////                    Message message = new MimeMessage(session);
////                    message.setFrom(new InternetAddress("your_email@example.com")); // Replace with your email address
////                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
////                    message.setSubject("Verification Code");
////                    message.setText("Your verification code is: " + verificationCode);
////
////                    // Send the message
////                    Transport.send(message);
////
////                    // Hide the loading panel and display a toast on the main thread
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            loadingPanel.setVisibility(View.GONE);
////                            Toast.makeText(EmailVeriActivity.this, "Verification code sent to " + email, Toast.LENGTH_SHORT).show();
////                        }
////                    });
////                } catch (final MessagingException e) {
////                    e.printStackTrace();
////                    // Hide the loading panel and display a toast on the main thread
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            loadingPanel.setVisibility(View.GONE);
////                            Toast.makeText(EmailVeriActivity.this, "Failed to send verification code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
////                        }
////                    });
////                }
////            }
////        }).start();
////    }
//
//
//}
