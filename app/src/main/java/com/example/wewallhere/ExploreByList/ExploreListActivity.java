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
    private List<MediaEntry> mediaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_list);

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create a list of media entries
        mediaList = createMediaList();

        // Create and set the adapter
        mediaAdapter = new MediaAdapter(mediaList);
        recyclerView.setAdapter(mediaAdapter);
    }

    // Create a list of media entries (dummy data for demonstration)
    private List<MediaEntry> createMediaList() {
        List<MediaEntry> mediaList = new ArrayList<>();
        // Create a Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://54.252.196.140:3001/") // Replace with your server URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create a service interface for your API endpoints
        MediaService mediaService = retrofit.create(MediaService.class);

        // Make an API call to retrieve media files
        Call<List<MediaEntry>> call = mediaService.getMediaList("image");  // , "image_1684667427711_388"
        call.enqueue(new Callback<List<MediaEntry>>() {
            @Override
            public void onResponse(Call<List<MediaEntry>> call, Response<List<MediaEntry>> response) {
                if (response.isSuccessful()) {
                    List<MediaEntry> mediaEntries = response.body();
                    // Handle the retrieved media entries
                    mediaList.addAll(mediaEntries);
                } else {
                    ToastHelper.showLongToast(getApplicationContext(), response.message(), Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<List<MediaEntry>> call, Throwable t) {
                // Handle network or other errors
                ToastHelper.showLongToast(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
            }
        });

        return mediaList;
    }


}
