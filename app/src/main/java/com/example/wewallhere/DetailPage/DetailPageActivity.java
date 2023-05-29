package com.example.wewallhere.DetailPage;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.wewallhere.ExploreByList.MongoMediaEntry;
import com.example.wewallhere.R;

import java.util.ArrayList;
import java.util.List;

import Helper.ToastHelper;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailPageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MediaAdapterTop mediaAdapterTop;
    private List<MongoMediaEntry> mongoMetaList = new ArrayList<>();
    private String url_media_service = "http://54.252.196.140:3000/";
    private String url_download = "http://54.252.196.140:3000/download/";
    private ImageView topimageView;
    private ImageView topTumbnail;
    private VideoView topvideoView;
    private Toolbar toptitle;
    private TextView textTitle;
    private RelativeLayout loadingPanel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);

        MongoMediaEntry top_mongoEntry = (MongoMediaEntry) getIntent().getSerializableExtra("MongoMediaEntry");
        mongoMetaList.clear();
        mongoMetaList.add(top_mongoEntry);

        initTitleBar(top_mongoEntry.getTitle());
        iniTopMedia(top_mongoEntry);

//        // Initialize the RecyclerView
//        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        // Create and set the adapter for empty mediaList
//        mediaAdapter = new MediaAdapter(mongoMetaList, url_download, getApplicationContext());
//        recyclerView.setAdapter(mediaAdapter);
//        updateMedia();
    }

    private void iniTopMedia(MongoMediaEntry mongoEntry){
        TextView content = findViewById(R.id.textViewContentDetail);
        content.setText(mongoEntry.getContent());
//        TextView author = findViewById(R.id.textViewUploaderDetail);
//        author.setText(uploader);
        TextView time = findViewById(R.id.textViewDateDetail);
        time.setText(mongoEntry.getTimestamp());
        // load media
        topimageView = findViewById(R.id.imageViewDetail);
        topTumbnail = findViewById(R.id.Thumbnail);
        topvideoView = findViewById(R.id.videoViewDetail);
        loadingPanel = findViewById(R.id.detailloadingPanel);
        if (isVideoFilename(mongoEntry.getFilename())) {
            String videourl = url_download + "video/" + mongoEntry.getFilename();
            topimageView.setVisibility(View.GONE);
            topvideoView.setVisibility(View.VISIBLE);

            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(topvideoView);
            topvideoView.setMediaController(mediaController);

            // Set the video URI
            Uri videoUri = Uri.parse(videourl);
            topvideoView.setVideoURI(videoUri);

            // using glide to render video thumbnail
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(videoUri)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            // Display the thumbnail in an ImageView
                            topTumbnail.setImageBitmap(resource);
                            topTumbnail.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            // Handle the case where loading the thumbnail fails
                            super.onLoadFailed(errorDrawable);
                        }
                    });


//            MediaController finalMediaController = mediaController;
//            topvideoView.setMediaController(null); // Remove existing MediaController
//            topvideoView.setMediaController(finalMediaController);

            final boolean[] ready = {false};
            topvideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    try{
                        loadingPanel.setVisibility(View.GONE);
                        topTumbnail.setVisibility(View.GONE);
                        ready[0] = true;
                    }catch (Exception e){
                        ToastHelper.showLongToast(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                    }

                }
            });
            // Set an OnClickListener for the VideoView to start the video
            topvideoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        if (ready[0] && !topvideoView.isPlaying()) {
                            topvideoView.requestFocus();
                            topvideoView.start();
//                            finalMediaController.show(10); // Show the MediaController

                        }
                    }catch (Exception e){
                        ToastHelper.showLongToast(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                    }

                }
            });

        } else {
            String imagedownloadUrl = url_download + "image/" + mongoEntry.getFilename();

            topimageView.setVisibility(View.VISIBLE);
            topvideoView.setVisibility(View.GONE);

            Glide.with(getApplicationContext())
                    .load(imagedownloadUrl)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            // Handle the image loading failure if needed
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            // Image loading is successful, hide the loading panel here
                            loadingPanel.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(topimageView);
        }

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
        mediaAdapterTop = new MediaAdapterTop(mongoMetaList, url_download);
        recyclerView.setAdapter(mediaAdapterTop);
    }
    private void initTitleBar(String title){
        toptitle = findViewById(R.id.titlebar);
        setSupportActionBar(toptitle);

        textTitle = findViewById(R.id.textViewHeadTitle);
        textTitle.setText(title);
        // Set as the action bar
    }
    private boolean isVideoFilename(String filename) {
        return filename.startsWith("video");
    }
}
