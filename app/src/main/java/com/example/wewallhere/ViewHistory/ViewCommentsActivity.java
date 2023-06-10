package com.example.wewallhere.ViewHistory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wewallhere.DetailPage.CommentAdapter;
import com.example.wewallhere.DetailPage.MongoCommentEntry;
import com.example.wewallhere.DetailPage.MongoCommentService;
import com.example.wewallhere.ExploreByList.ExploreListActivity;
import com.example.wewallhere.ExploreByList.MongoMediaEntry;
import com.example.wewallhere.R;
import com.example.wewallhere.Upload.UploadActivity;
import com.example.wewallhere.User.InfoHomeActivity;
import com.example.wewallhere.User.UserInfo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Helper.ToastHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewCommentsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<MongoCommentEntry> mongoCommentList = new ArrayList<>();
    private String url_download;
    private String url_media_service;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_comments);
        url_media_service = getString(R.string.url_media_service);
        url_download = url_media_service + "download/";
        iniBottomMenu();
        // comments
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Create and set the adapter for empty mediaList
        commentAdapter = new CommentAdapter(mongoCommentList, url_download, getApplicationContext());
        recyclerView.setAdapter(commentAdapter);
        updateComment();
    }
    private void updateComment() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url_media_service)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // Create a service interface for your API endpoints
        MongoCommentService mongoCommentService = retrofit.create(MongoCommentService.class);
        // Make an API call to retrieve media files
        SharedPreferences prefs = getSharedPreferences("INFO", MODE_PRIVATE);
        String pref_email = prefs.getString("email", getString(R.string.default_email));
        Call<List<MongoCommentEntry>> call = mongoCommentService.getHistoryCommentMetaList(pref_email);  // , "image_1684667427711_388"
        call.enqueue(new Callback<List<MongoCommentEntry>>() {
            @Override
            public void onResponse(Call<List<MongoCommentEntry>> call, Response<List<MongoCommentEntry>> response) {
                if (response.isSuccessful()) {
                    List<MongoCommentEntry> commentEntries = response.body();
                    Collections.reverse(commentEntries);
                    // Handle the retrieved media entries
                    mongoCommentList.clear();
                    mongoCommentList.addAll(commentEntries);
                    updateRecyclerView();

                } else {
                    ToastHelper.showLongToast(getApplicationContext(), response.message(), Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<List<MongoCommentEntry>> call, Throwable t) {
                // Handle network or other errors
                ToastHelper.showLongToast(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
            }
        });

    }

    private void updateRecyclerView() {
        // Create a new adapter with the updated media list
        commentAdapter = new CommentAdapter(mongoCommentList, url_download, getApplicationContext());
        recyclerView.setAdapter(commentAdapter);
    }

    private void iniBottomMenu(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.info);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.upload) {
                startActivity(new Intent(ViewCommentsActivity.this, UploadActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }  else if (itemId == R.id.info) {
                startActivity(new Intent(ViewCommentsActivity.this, InfoHomeActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }  else if (itemId == R.id.explore) {  // it's viewing history in info tag actually.
                startActivity(new Intent(ViewCommentsActivity.this, ExploreListActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }
            return false;
        });

    }


    @Override
    public void onBackPressed() {
        // do nothing
    }
}
