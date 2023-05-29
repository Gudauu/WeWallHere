package com.example.wewallhere.DetailPage;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.example.wewallhere.Upload.ComposeActivity;
import com.example.wewallhere.Upload.UploadActivity;
import com.example.wewallhere.gmaps.SingleLocation;

import java.util.ArrayList;
import java.util.List;

import Helper.ToastHelper;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailPageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<MongoMediaEntry> mongoMetaList = new ArrayList<>();
    private String url_media_service = "http://54.252.196.140:3000/";
    private String url_download = "http://54.252.196.140:3000/download/";
    private ImageView topimageView;
    private ImageView topTumbnail;
    private VideoView topvideoView;
    private Toolbar toptitle;
    private TextView textTitle;
    private RelativeLayout loadingPanel;
    private ImageView reply;
    // Declare the dialog and its views
    private Dialog commentDialog;
    private EditText editTextComment;
    private RadioButton radioButtonImage;
    private RadioButton radioButtonVideo;
    private Button buttonUploadMedia;
    private Button buttonSubmit;
    private Button buttonCancel;
    private int REQUEST_VIDEO_PICK = 6723;
    private int REQUEST_IMAGE_PICK = 6724;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);

        MongoMediaEntry top_mongoEntry = (MongoMediaEntry) getIntent().getSerializableExtra("MongoMediaEntry");
        mongoMetaList.clear();
        mongoMetaList.add(top_mongoEntry);

        initTitleBar(top_mongoEntry.getTitle());
        iniTopMedia(top_mongoEntry);
        iniCommentDialog();

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
        reply = findViewById(R.id.reply);
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

    private void iniCommentDialog(){
        commentDialog = new Dialog(this);
        commentDialog.setContentView(R.layout.dialog_comment);

        editTextComment = commentDialog.findViewById(R.id.editTextComment);
        radioButtonImage = commentDialog.findViewById(R.id.radioButtonImage);
        radioButtonVideo = commentDialog.findViewById(R.id.radioButtonVideo);
        buttonUploadMedia = commentDialog.findViewById(R.id.buttonUploadMedia);
        buttonSubmit = commentDialog.findViewById(R.id.buttonSubmit);
        buttonCancel = commentDialog.findViewById(R.id.buttonCancel);

        

        buttonUploadMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioButtonImage.isChecked()) {
                    // Handle image upload
                } else if (radioButtonVideo.isChecked()) {
                    // Handle video upload
                } else {
                    // Show an error message if neither radio button is checked
                }
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = editTextComment.getText().toString();
                // Handle the submission of the comment and selected media
                commentDialog.dismiss();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the dialog without any action
                commentDialog.dismiss();
            }
        });

        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentDialog.show();
            }
        });


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
        commentAdapter = new CommentAdapter(mongoMetaList, url_download);
        recyclerView.setAdapter(commentAdapter);
    }
    private void initTitleBar(String title){
        toptitle = findViewById(R.id.titlebar);
        setSupportActionBar(toptitle);

        textTitle = findViewById(R.id.textViewHeadTitle);
        textTitle.setText(title);
        // Set as the action bar
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

        } else if (requestCode == REQUEST_VIDEO_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedVideoUri = data.getData();

        }
    }

    // media: check permission Select image & video from gallery
    private void selectImagePermissionCheck() {
        if (checkImagePermission()) {
            startImagePickerIntent();
        } else {
            // Request the necessary permissions
            String[] permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
            ActivityCompat.requestPermissions(this, permissions, REQUEST_IMAGE_PICK);
        }
    }
    private void selectVideoPermissionCheck() {
        if (checkVideoPermission()) {
            // Permissions are already granted, open the video selection intent
            startVideoPickerIntent();
        } else {
            // Request the necessary permissions
            String[] permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            };
            ActivityCompat.requestPermissions(this, permissions, REQUEST_VIDEO_PICK);
        }
    }
    // media: invoke picker
    private void startImagePickerIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }
    private void startVideoPickerIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");
        startActivityForResult(intent, REQUEST_VIDEO_PICK);
    }
    // media: permission
    private boolean checkVideoPermission() {
        // Check if the required permissions are granted
        int readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        return readStoragePermission == PackageManager.PERMISSION_GRANTED &&
                writeStoragePermission == PackageManager.PERMISSION_GRANTED &&
                cameraPermission == PackageManager.PERMISSION_GRANTED;
    }
    private boolean checkImagePermission() {
        // Check if the required permissions are granted
        int readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        return readStoragePermission == PackageManager.PERMISSION_GRANTED;
    }
    private boolean isVideoFilename(String filename) {
        return filename.startsWith("video");
    }
}
