package com.example.wewallhere.ExploreByList;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.wewallhere.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Helper.ToastHelper;

public class MediaAdapter extends RecyclerView.Adapter<MediaViewHolder> {
    private List<MongoMediaEntry> mongometaEntries;
    private String serverIP;

    public MediaAdapter(List<MongoMediaEntry> mongometaEntries, String serverIP) {
        this.mongometaEntries = mongometaEntries;
        this.serverIP = serverIP;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media_entry, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        MongoMediaEntry mongoEntry = mongometaEntries.get(position);

        if (isVideoFilename(mongoEntry.getFilename())) {
            String videourl = serverIP + "video/" + mongoEntry.getFilename();
            holder.imageViewMedia.setVisibility(View.GONE);
            holder.videoViewMedia.setVisibility(View.VISIBLE);

            MediaController mediaController = new MediaController(holder.itemView.getContext());
            mediaController = new MediaController(holder.itemView.getContext());
            mediaController.setAnchorView(holder.videoViewMedia);


            // Set the video URI
            Uri videoUri = Uri.parse(videourl);
            holder.videoViewMedia.setVideoURI(videoUri);

            // using glide to render video thumbnail
            Glide.with(holder.itemView.getContext())
                    .asBitmap()
                    .load(videoUri)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            // Display the thumbnail in an ImageView
                            holder.imageViewThumbnail.setImageBitmap(resource);
                            holder.imageViewThumbnail.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            // Handle the case where loading the thumbnail fails
                            super.onLoadFailed(errorDrawable);
                        }
                    });



            MediaController finalMediaController = mediaController;
            holder.videoViewMedia.setMediaController(finalMediaController);

            holder.videoViewMedia.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    try{
                        holder.loadingPanel.setVisibility(View.GONE);
                        holder.imageViewThumbnail.setVisibility(View.GONE);
                    }catch (Exception e){
                        ToastHelper.showLongToast(holder.itemView.getContext(), e.getMessage(), Toast.LENGTH_LONG);
                    }

                }
            });
            // Set an OnClickListener for the VideoView to start the video
            holder.videoViewMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!holder.videoViewMedia.isPlaying()) {
                        // Hide the thumbnail
                        holder.imageViewThumbnail.setVisibility(View.GONE);
                        holder.videoViewMedia.requestFocus();
                        holder.videoViewMedia.start();
                        finalMediaController.show(10); // Show the MediaController

                    }
                }
            });

        } else {
            String imagedownloadUrl = serverIP + "image/" + mongoEntry.getFilename();

            holder.imageViewMedia.setVisibility(View.VISIBLE);
            holder.videoViewMedia.setVisibility(View.GONE);

            Glide.with(holder.itemView.getContext())
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
                            holder.loadingPanel.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.imageViewMedia);
        }

//        holder.imageViewProfilePic.setImageResource(mongoEntry.getProfilePicResId());
        //        holder.textViewUploader.setText(mongoEntry.getUploaderName());

        holder.textViewTitle.setText(mongoEntry.getTitle());
        holder.textViewDate.setText(mongoEntry.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return mongometaEntries.size();
    }

    private boolean isVideoFilename(String filename) {
        return filename.startsWith("video");
    }
}

