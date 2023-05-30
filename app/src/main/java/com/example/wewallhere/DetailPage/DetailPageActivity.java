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
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Helper.ToastHelper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailPageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<MongoCommentEntry> mongoCommentList = new ArrayList<>();
    private String url_media_service = "http://54.252.196.140:3000/";
    private String url_download = "http://54.252.196.140:3000/download/";
    private ImageView topimageView;
    private ImageView topTumbnail;
    private VideoView topvideoView;
    private Toolbar toptitle;
    private TextView textTitle;
    private RelativeLayout loadingPanel;
    private ImageView reply;
    private String topID;
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
    private Uri comment_media_uri;
    private int TYPE_TEXT = 1;
    private int TYPE_IMAGE = 2;
    private int TYPE_VIDEO = 3;
    private int comment_type = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);

        MongoMediaEntry top_mongoEntry = (MongoMediaEntry) getIntent().getSerializableExtra("MongoMediaEntry");

        initTitleBar(top_mongoEntry.getTitle());
        iniTopMedia(top_mongoEntry);
        iniCommentDialog();

        recyclerView = findViewById(R.id.recyclerViewComments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Create and set the adapter for empty mediaList
        commentAdapter = new CommentAdapter(mongoCommentList, url_download, getApplicationContext());
        recyclerView.setAdapter(commentAdapter);
        updateComment();
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
        topID = mongoEntry.getID();
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
        comment_type = TYPE_TEXT;
        comment_media_uri = null;
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
                    buttonSubmit.setFocusable(false);
                    selectImagePermissionCheck();
                } else if (radioButtonVideo.isChecked()) {
                    buttonSubmit.setFocusable(false);
                    selectVideoPermissionCheck();
                } else {
                    ToastHelper.showLongToast(getApplicationContext(), "Choose media type.", Toast.LENGTH_SHORT);
                }
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = editTextComment.getText().toString();
                if(comment.length() == 0){
                    ToastHelper.showLongToast(getApplicationContext(), "Please enter comment.", Toast.LENGTH_SHORT);
                    return;
                }
                if(comment_type == TYPE_TEXT){
                    uploadTextCommentToServer(topID, "reply", comment);
                }else if(comment_type == TYPE_IMAGE){
                    uploadImageCommentToServer(topID, comment_media_uri, "reply", comment);
                }else if(comment_type == TYPE_VIDEO){
                    uploadVideoToServer(topID, comment_media_uri, "reply", comment);
                }
                // Handle the submission of the comment and selected media
//                commentDialog.dismiss();
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



    private void updateComment() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url_media_service)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // Create a service interface for your API endpoints
        MongoCommentService mongoCommentService = retrofit.create(MongoCommentService.class);
        // Make an API call to retrieve media files
        Call<List<MongoCommentEntry>> call = mongoCommentService.getCommentMetaList(topID);  // , "image_1684667427711_388"
        call.enqueue(new Callback<List<MongoCommentEntry>>() {
            @Override
            public void onResponse(Call<List<MongoCommentEntry>> call, Response<List<MongoCommentEntry>> response) {
                if (response.isSuccessful()) {
                    List<MongoCommentEntry> commentEntries = response.body();

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
            comment_type = TYPE_IMAGE;
            comment_media_uri = selectedImageUri;
            ToastHelper.showLongToast(getApplicationContext(), "Image selected.", Toast.LENGTH_SHORT);
            buttonSubmit.setFocusable(true);

        } else if (requestCode == REQUEST_VIDEO_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedVideoUri = data.getData();
            comment_type = TYPE_VIDEO;
            comment_media_uri = selectedVideoUri;
            ToastHelper.showLongToast(getApplicationContext(), "Video selected.", Toast.LENGTH_SHORT);
            buttonSubmit.setFocusable(true);
        }
    }

    private void uploadImageCommentToServer(String replyID, Uri imageUri, String title, String content) {
        try {
            // Create a Retrofit instance
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url_media_service) // Replace with your server's IP address
                    .build();


            // Create the request body for image, latitude and longitude
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), getBytesFromInputStream(inputStream));
            String fileName = "image_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("file", fileName, requestBody);

            RequestBody ID = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(geneUniqueID()));
            RequestBody ID_reply = RequestBody.create(MediaType.parse("text/plain"), replyID);
            RequestBody contentTitle = RequestBody.create(MediaType.parse("text/plain"), title);
            RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"), content);


            // Create an instance of the API service interface
            UploadCommentService UploadCommentService = retrofit.create(UploadCommentService.class);

            // Send the image file to the server
            Call<ResponseBody> call = UploadCommentService.uploadImage(ID, ID_reply, imagePart, contentTitle, contentBody);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        // Image uploaded successfully
                        Toast.makeText(DetailPageActivity.this, "Comment posted.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle error response
                        Toast.makeText(DetailPageActivity.this, "Comment post failed:", Toast.LENGTH_SHORT).show();
                        ToastHelper.showLongToast(getApplicationContext(), response.message(),Toast.LENGTH_LONG);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Handle network failure
                    ToastHelper.showLongToast(getApplicationContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG);
                }
            });
        } catch (Exception e) {
            ToastHelper.showLongToast(DetailPageActivity.this, "Comment post failed:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }
    private void uploadTextCommentToServer(String replyID, String title, String content) {
        try {
            // Create a Retrofit instance
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url_media_service) // Replace with your server's IP address
                    .build();
            // Create an instance of the API service interface
            UploadCommentService UploadCommentService = retrofit.create(UploadCommentService.class);

            // Create a JsonObject and add your data
            JsonObject data = new JsonObject();
            data.addProperty("ID", geneUniqueID());
            data.addProperty("ID_reply", replyID);
            data.addProperty("title", "reply");
            data.addProperty("content", content);

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), data.toString());

            // Send the image file to the server
            Call<Void> call = UploadCommentService.uploadText(requestBody);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        // Image uploaded successfully
                        Toast.makeText(DetailPageActivity.this, "Comment posted.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle error response
                        Toast.makeText(DetailPageActivity.this, "Comment post failed:", Toast.LENGTH_SHORT).show();
                        ToastHelper.showLongToast(getApplicationContext(), response.message(),Toast.LENGTH_LONG);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Handle network failure
                    ToastHelper.showLongToast(getApplicationContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG);
                }
            });
        } catch (Exception e) {
            ToastHelper.showLongToast(DetailPageActivity.this, "comment post failed: " + e.getMessage(), Toast.LENGTH_LONG);
        }
    }
    // helper function in video upload
    private byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[4 * 1024]; // Adjust the buffer size as needed
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, bytesRead);
        }
        return byteBuffer.toByteArray();
    }
    private void uploadVideoToServer(String replyID, Uri videoUri, String title, String content){
        try{
            // Create the Retrofit instance
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url_media_service)
                    .build();

            RequestBody ID = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(geneUniqueID()));
            RequestBody ID_reply = RequestBody.create(MediaType.parse("text/plain"), replyID);

            // Create the request body for video, latitude and longitude
            InputStream inputStream = getContentResolver().openInputStream(videoUri);
            RequestBody requestBody = RequestBody.create(MediaType.parse("video/*"), getBytesFromInputStream(inputStream));
            String fileName = "video_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000);
            MultipartBody.Part videoPart = MultipartBody.Part.createFormData("file", fileName, requestBody);


            RequestBody contentTitle = RequestBody.create(MediaType.parse("text/plain"), title);
            RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"), content);


            // Create the API service interface
            UploadCommentService UploadCommentService = retrofit.create(UploadCommentService.class);

            // Create the API call to upload the video
            Call<ResponseBody> call = UploadCommentService.uploadVideo(ID, ID_reply, videoPart,contentTitle, contentBody);

            // Enqueue the API call
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        // Video uploaded successfully
                        Toast.makeText(DetailPageActivity.this, "Comment posted.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Video upload failed
                        Toast.makeText(DetailPageActivity.this, "Comment post failed:", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Handle the upload failure
                    ToastHelper.showLongToast(DetailPageActivity.this, "Comment post failed: " + t.getMessage(), Toast.LENGTH_LONG);
                }
            });
        } catch (Exception e){
            ToastHelper.showLongToast(DetailPageActivity.this, "Comment post failed: " + e, Toast.LENGTH_LONG);

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

    private String geneUniqueID(){
        String str_ID = System.currentTimeMillis() + "_" + new Random().nextInt(1000) + "_" + new Random().nextInt(1000);
        return str_ID;
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
