package com.example.wewallhere.ExploreByList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.example.wewallhere.R;

import java.util.ArrayList;
import java.util.List;

import Helper.ToastHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ExploreListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MediaAdapter mediaAdapter;
    private List<MongoMediaEntry> mongoMetaList = new ArrayList<>();
    private String url_download = "http://54.252.196.140:3002/download/";
    private String url_mongometa = "http://54.252.196.140:3001/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_list);

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create and set the adapter for empty mediaList
        mediaAdapter = new MediaAdapter(mongoMetaList, url_download);
        recyclerView.setAdapter(mediaAdapter);

        updateMedia();
    }

    private void updateRecyclerView() {
        // Create a new adapter with the updated media list
        mediaAdapter = new MediaAdapter(mongoMetaList, url_download);
        recyclerView.setAdapter(mediaAdapter);
    }

    private void updateMedia() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url_mongometa) 
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create a service interface for your API endpoints
        MediaService mediaService = retrofit.create(MediaService.class);

        // Make an API call to retrieve media files
        Call<List<MongoMediaEntry>> call = mediaService.getMetaDataList("image");  // , "image_1684667427711_388"
        call.enqueue(new Callback<List<MongoMediaEntry>>() {
            @Override
            public void onResponse(Call<List<MongoMediaEntry>> call, Response<List<MongoMediaEntry>> response) {
                if (response.isSuccessful()) {
                    List<MongoMediaEntry> mediaEntries = response.body();

                    // Handle the retrieved media entries
                    mongoMetaList.addAll(mediaEntries);
                    updateRecyclerView();

                } else {
                    ToastHelper.showLongToast(getApplicationContext(), response.message(), Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<List<MongoMediaEntry>> call, Throwable t) {
                // Handle network or other errors
                ToastHelper.showLongToast(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
            }
        });

    }


}
