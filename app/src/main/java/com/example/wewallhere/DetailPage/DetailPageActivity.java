package com.example.wewallhere.DetailPage;

import android.os.Bundle;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wewallhere.ExploreByList.MediaAdapter;
import com.example.wewallhere.ExploreByList.MongoMediaEntry;
import com.example.wewallhere.ExploreByList.MongoMetaService;
import com.example.wewallhere.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import Helper.ToastHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailPageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MediaAdapter mediaAdapter;
    private List<MongoMediaEntry> mongoMetaList = new ArrayList<>();
    private String url_media_service = "http://54.252.196.140:3000/";
    private String url_download = "http://54.252.196.140:3000/download/";
    private Toolbar toptitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_list);

        MongoMediaEntry top_mongoEntry = (MongoMediaEntry) getIntent().getSerializableExtra("MongoEntry");
        mongoMetaList.clear();
        mongoMetaList.add(top_mongoEntry);

        initTitleBar(top_mongoEntry.getTitle());

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create and set the adapter for empty mediaList
        mediaAdapter = new MediaAdapter(mongoMetaList, url_download, getApplicationContext());
        recyclerView.setAdapter(mediaAdapter);
        updateMedia();
    }



    private void updateMedia() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url_media_service)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        updateRecyclerView();
//        // Create a service interface for your API endpoints
//        com.example.wewallhere.ExploreByList.MongoMetaService mongoMetaService = retrofit.create(MongoMetaService.class);
//
//        // Make an API call to retrieve media files
//        Call<List<MongoMediaEntry>> call = mongoMetaService.getMetaDataList(media_type);  // , "image_1684667427711_388"
//        call.enqueue(new Callback<List<MongoMediaEntry>>() {
//            @Override
//            public void onResponse(Call<List<MongoMediaEntry>> call, Response<List<MongoMediaEntry>> response) {
//                if (response.isSuccessful()) {
//                    List<MongoMediaEntry> mediaEntries = response.body();
//
//                    // Handle the retrieved media entries
//                    mongoMetaList.clear();
//                    mongoMetaList.addAll(mediaEntries);
//                    updateRecyclerView();
//
//                } else {
//                    ToastHelper.showLongToast(getApplicationContext(), response.message(), Toast.LENGTH_LONG);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<MongoMediaEntry>> call, Throwable t) {
//                // Handle network or other errors
//                ToastHelper.showLongToast(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
//            }
//        });

    }

    private void updateRecyclerView() {
        // Create a new adapter with the updated media list
        mediaAdapter = new MediaAdapter(mongoMetaList, url_download, getApplicationContext());
        recyclerView.setAdapter(mediaAdapter);
    }
    private void initTitleBar(String title){
        toptitle = findViewById(R.id.titlebar);
        // Set as the action bar
        setSupportActionBar(toptitle);
    }
}
