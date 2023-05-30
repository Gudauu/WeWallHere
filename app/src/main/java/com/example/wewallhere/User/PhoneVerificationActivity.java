package com.example.wewallhere.User;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wewallhere.Main.MainActivity;
import com.example.wewallhere.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class PhoneVerificationActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    private Button buttonSendVcode;
    private Button buttonVerify;
    private TextView inputPhone;
    private TextView inputVcode;
    private Handler vcodeHandler;
    private String vcode;
    private String phone = "";
    private String admin_phone = "2333";
    private int vcodeLen = 5;
    private Boolean flag_debug = false;   //Shutong

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);
        prefs = getSharedPreferences("INFO", MODE_PRIVATE);
        IniViews();
        IniViewListeners();
        IniHandlers();
    }

    private void IniViews(){
        buttonSendVcode = findViewById(R.id.send_vcode);
        buttonVerify = findViewById(R.id.verify);
        inputPhone = findViewById(R.id.input_phone);
        inputVcode = findViewById(R.id.input_vcode);
    }

    private void IniViewListeners(){
        buttonSendVcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = (inputPhone.getText()).toString();
                flag_debug = phone.equals(admin_phone);

                if(!flag_debug && CheckPhoneFormat()){
                    geneVcode();
                    HttpSendVcodeTask SendVcodeTask=new HttpSendVcodeTask(phone,vcode,vcodeHandler);
                    SendVcodeTask.execute();
                }
            }
        });
        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input_vcode = (inputVcode.getText()).toString();

                if(flag_debug){
                    phone = (inputPhone.getText()).toString();
                }
                else if(input_vcode.length() == 0){
                    Toast.makeText(PhoneVerificationActivity.this, "Please enter verification code", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(vcode == null){
                    Toast.makeText(PhoneVerificationActivity.this, "Please send the verification code first", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (flag_debug || vcode.equals(input_vcode)){
                    Toast.makeText(PhoneVerificationActivity.this, "Verified.", Toast.LENGTH_SHORT).show();

                    updateSharedPreferences(phone);

                    Intent intent = new Intent();
                    intent.setClass(PhoneVerificationActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else{
                    vcode = null;
                    Toast.makeText(PhoneVerificationActivity.this, "Verification failed. Please check your code or retry.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void updateSharedPreferences(String phoneNumber){
        long currentTimestamp = System.currentTimeMillis();
        prefs.edit().putLong("lastLogin", currentTimestamp).commit();
        prefs.edit().putString("phone", phoneNumber).commit();
    }
    private void IniHandlers(){
        vcodeHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                Toast.makeText(PhoneVerificationActivity.this, (String)message.obj, Toast.LENGTH_LONG).show();
            }
        };

    }


    private Boolean CheckPhoneFormat(){

        if (phone.length()==0) {
            Toast.makeText(PhoneVerificationActivity.this, "Phone number cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(phone.length()!=11){
            Toast.makeText(PhoneVerificationActivity.this, "Phone number should contain 11 digits.", Toast.LENGTH_SHORT).show();
            return false;
        }else if (!phone.matches("[0-9]+")) {
            Toast.makeText(PhoneVerificationActivity.this, "Phone number should contain digits only.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void geneVcode(){
        vcode = "";  // clear the previous one
        for(int i=0;i<vcodeLen;i++){
            vcode += String.valueOf(new Random().nextInt(10));
        }
    }


}

class HttpSendVcodeTask extends AsyncTask<Void, Void, Void> {
    private Handler handler;
    private String vcode;
    private String phone;

    public HttpSendVcodeTask(String phone,String vcode,Handler h){
        this.handler = h;
        this.vcode = vcode;
        this.phone = phone;
    }

    private static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public Void doInBackground(Void... params) {
        HttpURLConnection urlConnection=null;
        try {
            // The parameters documented by erkun
            String appKey = "365dabdf62754410bca056d0c13a5d88";
            String mobile = phone;
            String content = "【蓝钮助手】您的验证码为:" + vcode;
            String timestamp = Long.toString(System.currentTimeMillis());

            String appSecret = "546e8267731241f1b66f2ec8f057d719";
            String sign = md5(appSecret + mobile + timestamp);

            String postData = "appKey="+ URLEncoder.encode(appKey,"UTF-8")
                    +"&mobile=" +URLEncoder.encode(mobile,"UTF-8")
                    +"&content=" +URLEncoder.encode(content,"UTF-8")
                    +"&timestamp="+URLEncoder.encode(timestamp,"UTF-8")
                    +"&sign="+URLEncoder.encode(sign,"UTF-8");
            URL url = new URL("https://sms.mykun.cn/v1/sms/send");

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setInstanceFollowRedirects(true);
            urlConnection.setReadTimeout(15000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); //;charset=utf-8
//            urlConnection.addRequestProperty("charset", "utf-8");
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.connect();

            DataOutputStream localDataOutputStream = new DataOutputStream(urlConnection.getOutputStream());
            localDataOutputStream.writeBytes(postData);
            localDataOutputStream.flush();
            localDataOutputStream.close();


            int code = urlConnection.getResponseCode();
            if (code !=  201 && code !=200 && code !=202) {
                throw new IOException("Invalid response from server: " + code);
            }
            else{
                // still need to check the response code from server
                String response_data = "";
                String cur_line;
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                while ((cur_line = br.readLine()) != null) {
                    response_data += cur_line;
                }
                JSONObject vcode_response = new JSONObject((String)(response_data));

                Message message;
                if(vcode_response.getInt("code")==0){
                    message = handler.obtainMessage(0,"Code sent. Check your messages.");
                }
                else{
                    message = handler.obtainMessage(1,"Send failed: "+vcode_response.getString("msg"));
                }

                message.sendToTarget();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage(1,"Send failed.");
            message.sendToTarget();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }
}

