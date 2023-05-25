package com.example.wewallhere.ExploreByList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.wewallhere.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Helper.ToastHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ExploreListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MediaAdapter mediaAdapter;
    private TabLayout tabLayout;
    private Spinner dropdownMenu;
    private List<MongoMediaEntry> mongoMetaList = new ArrayList<>();
    private String url_media_service = "http://54.252.196.140:3000/";
    private String url_download = "http://54.252.196.140:3000/download/";
    private Toolbar topbar;
    private String media_type = "image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_list);

        initTopBar();

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create and set the adapter for empty mediaList
        mediaAdapter = new MediaAdapter(mongoMetaList, url_download);
        recyclerView.setAdapter(mediaAdapter);
        updateMedia();
    }
    private void initTopBar(){
        // remove the top left app title
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        // Initialize toolbar view
        topbar = findViewById(R.id.toolbar);
        // Set the toolbar as the action bar
        setSupportActionBar(topbar);
        tabLayout = findViewById(R.id.tabLayout);
        // Set up tabs for ExploreListActivity
        tabLayout.addTab(tabLayout.newTab().setText("List View"));
        tabLayout.addTab(tabLayout.newTab().setText("Map View"));

        // Set up dropdown menu options for image/video selection
        dropdownMenu = findViewById(R.id.dropdownMenu);
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"image", "video"});
        dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownMenu.setAdapter(dropdownAdapter);

        // Set up dropdown menu item selection listener
        dropdownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String new_media_type = parent.getItemAtPosition(position).toString();
                if(!new_media_type.equals(media_type)){
                    media_type = new_media_type;
                    updateMedia();
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        dropdownMenu.setSelection(0); // Set the initial selection to the first item ("Images")

    }




    private void updateRecyclerView() {
        // Create a new adapter with the updated media list
        mediaAdapter = new MediaAdapter(mongoMetaList, url_download);
        recyclerView.setAdapter(mediaAdapter);
    }

    private void updateMedia() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url_media_service)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create a service interface for your API endpoints
        MongoMetaService mongoMetaService = retrofit.create(MongoMetaService.class);

        // Make an API call to retrieve media files
        Call<List<MongoMediaEntry>> call = mongoMetaService.getMetaDataList(media_type);  // , "image_1684667427711_388"
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
