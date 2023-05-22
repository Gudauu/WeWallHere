package com.example.wewallhere.ExploreByList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.wewallhere.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaViewHolder> {
    private List<MediaEntry> mediaList;

    // Constructor
    public MediaAdapter(List<MediaEntry> mediaList) {
        this.mediaList = mediaList;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media_entry, parent, false);
        return new MediaViewHolder(view);
    }

//    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        MediaEntry mediaEntry = mediaList.get(position);
        // Set the data to the views
        if (mediaEntry.isVideo()) {
            holder.imageViewMedia.setVisibility(View.VISIBLE);
            holder.videoViewMedia.setVisibility(View.GONE);
            holder.imageViewMedia.setImageURI(mediaEntry.getMediaUri());

            // Set click listener to start video playback
            holder.imageViewMedia.setOnClickListener(v -> {
                holder.imageViewMedia.setVisibility(View.GONE);
                holder.videoViewMedia.setVisibility(View.VISIBLE);
                holder.videoViewMedia.setVideoURI(mediaEntry.getMediaUri());
                holder.videoViewMedia.start();
            });
        } else {
            holder.imageViewMedia.setVisibility(View.VISIBLE);
            holder.videoViewMedia.setVisibility(View.GONE);
            holder.imageViewMedia.setImageURI(mediaEntry.getMediaUri());

            // Remove click listener if it was previously set for video playback
            holder.imageViewMedia.setOnClickListener(null);
        }

        holder.imageViewProfilePic.setImageResource(mediaEntry.getProfilePicResId());
        holder.textViewTitle.setText(mediaEntry.getTitle());
        holder.textViewUploader.setText(mediaEntry.getUploaderName());
        holder.textViewDate.setText(mediaEntry.getUploadDate());
    }


    @Override
    public int getItemCount() {
        return mediaList.size();
    }
}
