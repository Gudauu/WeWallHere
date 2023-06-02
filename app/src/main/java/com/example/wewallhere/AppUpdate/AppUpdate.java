package com.example.wewallhere.AppUpdate;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.wewallhere.BuildConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import com.example.wewallhere.R;
import com.example.wewallhere.User.InfoHomeActivity;

import Helper.ToastHelper;


// Shutong: APP_VERSION needs updating each new release.

public class AppUpdate extends AsyncTask<String,Void,String> {
    private Context context;
    private Activity activity;
    private int appVersion;
    private int newVersion;
    private String url_media_service;
    private AlertDialog alertDialog;

    public void setContext(Context contextf) {
        context = contextf;
    }

    public void setVersion(int v) {
        appVersion = v;
    }

    public void setUrl(String u) {
        url_media_service = u + "apk/";
    }

    public AppUpdate(Activity activity) {
        this.activity = activity;
    }


    private boolean CheckVersion() throws IOException {
        String url_raw = url_media_service + "version";

        URL url = new URL(url_raw);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setInstanceFollowRedirects(false);
        c.setReadTimeout(15000);
        c.setConnectTimeout(15000);
        c.setRequestMethod("GET");
        c.connect();
        int temp_status_code = c.getResponseCode();
        if (temp_status_code != 200 && temp_status_code != 201 && temp_status_code != 202)
            return false;  // if fail to connect, simply abort

        BufferedReader rd = new BufferedReader(new InputStreamReader(c.getInputStream()));
        String content = "";
        content = rd.readLine();
        if (content == null)
            return false;  // if fail to load anything, simply abort
        int latest_version = Integer.parseInt(content);
        newVersion = latest_version;
        return latest_version == appVersion;

    }

    private void HandleVersionCheckFailure(String errorMessage) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, "Fail to check updates. Check your network", Toast.LENGTH_LONG).show();
//                Toast.makeText(activity, " " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void HandleInstallFailure(String errorMessage) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastHelper.showLongToast(context, "Autoupdate failed with: " + errorMessage, Toast.LENGTH_LONG);
            }
        });
    }


    private void ToggleFreezeUserInteraction(boolean start) {
        if (start) {  // freeze
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            (activity).findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);


        } else {  //unfreeze
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            //start loading icon
            activity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }
    }

    private String geneAppDownloadName(){
        return "WeWallHere_" + newVersion + "_" + System.currentTimeMillis() + ".apk";
    }

    private void InstallApp() {
        try {
            ToggleFreezeUserInteraction(true);

            //get destination to update file and set Uri
            String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
            String fileName = geneAppDownloadName();
            destination += fileName;

            //Delete update file if exists
            File file = new File(destination);
            if (file.exists())
                file.delete();             //file.delete() - test this, I think sometimes it doesnt work

            final Uri uri = Uri.parse("file://" + destination);
            final Uri uri_provider = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider",
                    new File(destination));

            //get url of app on server
            String url = url_media_service + "download";

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//        request.setDescription(Main.this.getString(R.string.notification_description));
//        request.setTitle(Main.this.getString(R.string.app_name));
            request.setDestinationUri(uri);

            // get download service and enqueue file
            final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            final long downloadId = manager.enqueue(request);
            //set BroadcastReceiver to install app when .apk is downloaded
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                public void onReceive(Context ctxt, Intent intent) {
                    Intent install = new Intent(Intent.ACTION_VIEW);
//                    install.setAction(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                    install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                    install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    install.setDataAndType(uri_provider, manager.getMimeTypeForDownloadedFile(downloadId));

                    try {
                        context.startActivity(install);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ToggleFreezeUserInteraction(false);

                    context.unregisterReceiver(this);
                    //Shutong: try
                    ((Activity) context).finish();
                }
            };
            //register receiver for when .apk download is compete
            context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        } catch (Exception e) {
            ToggleFreezeUserInteraction(false);
            e.printStackTrace();
            HandleInstallFailure(e.getMessage());
        }

    }


    protected void onPreExecute() {
        super.onPreExecute();
        alertDialog = new AlertDialog.Builder(activity).create();
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result == "noinstall"){
            ToastHelper.showLongToast(activity, "Already up to date.", Toast.LENGTH_SHORT);
            return;
        }
        alertDialog.setMessage("New version is available.\nUpdate now?");
        alertDialog.setCancelable(false);
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                if (checkStoragePermission()) {
                    InstallApp();
                } else {
                    Toast.makeText(activity, "Storage permission missing.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ToggleFreezeUserInteraction(false);
                return;
            }
        });
        try {
            alertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean checkStoragePermission(){
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }




    @Override
    protected String doInBackground(String... argdummy) {
        try {
            if (!this.CheckVersion())
                return "install";
        } catch (IOException e) {
            e.printStackTrace();
            HandleVersionCheckFailure(e.getMessage());
            return "noinstall";   // if fail to check version, pretend nothing happended and abort
        }
        return "noinstall";
    }
}