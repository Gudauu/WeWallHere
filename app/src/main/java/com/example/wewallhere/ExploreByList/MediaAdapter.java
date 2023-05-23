package com.example.wewallhere.ExploreByList;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.wewallhere.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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
        String mediaUrl = serverIP + mongoEntry.getFilename();

        if (isVideoFilename(mongoEntry.getFilename())) {
            holder.imageViewMedia.setVisibility(View.GONE);
            holder.videoViewMedia.setVisibility(View.VISIBLE);
            holder.videoViewMedia.setVideoURI(Uri.parse(mediaUrl));
            holder.videoViewMedia.start();
        } else {
            holder.imageViewMedia.setVisibility(View.VISIBLE);
            holder.videoViewMedia.setVisibility(View.GONE);

            Glide.with(holder.itemView.getContext())
                    .load(mediaUrl)
                    .into(holder.imageViewMedia);
        }

//        holder.imageViewProfilePic.setImageResource(mongoEntry.getProfilePicResId());
//        holder.textViewTitle.setText(mongoEntry.getTitle());
//        holder.textViewUploader.setText(mongoEntry.getUploaderName());
//        holder.textViewDate.setText(mongoEntry.getUploadDate());
    }

    @Override
    public int getItemCount() {
        return mongometaEntries.size();
    }

    private boolean isVideoFilename(String filename) {
        return filename.startsWith("video");
    }
}

