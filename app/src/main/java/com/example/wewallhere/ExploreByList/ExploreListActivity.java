package com.example.wewallhere.ExploreByList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import com.example.wewallhere.R;

import java.util.ArrayList;
import java.util.List;


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

        // Add media entries to the list
        // You can populate the list with your actual data
        // Here, we are adding dummy data for demonstration

        // Media 1
        MediaEntry media1 = new MediaEntry();
//        media1.setMediaUri(Uri.parse("path_to_media_file_1"));
//        media1.setProfilePicResId(R.drawable.profile_pic_1);
        media1.setTitle("Media 1");
//        media1.setUploaderName("Uploader 1");
//        media1.setUploadDate("May 20, 2023");
        mediaList.add(media1);
//
//        // Media 2
        MediaEntry media2 = new MediaEntry();
//        media2.setMediaUri(Uri.parse("path_to_media_file_2"));
//        media2.setProfilePicResId(R.drawable.profile_pic_2);
        media2.setTitle("Media 2");
//        media2.setUploaderName("Uploader 2");
//        media2.setUploadDate("May 21, 2023");
        mediaList.add(media2);

        // ...

        return mediaList;
    }
}
