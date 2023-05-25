package com.example.wewallhere.ExploreByList;

import android.media.MediaPlayer;
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

        if (isVideoFilename(mongoEntry.getFilename())) {
            String videourl = serverIP + "video/" + mongoEntry.getFilename();
            holder.imageViewMedia.setVisibility(View.GONE);
            holder.videoViewMedia.setVisibility(View.VISIBLE);
//            holder.videoViewMedia.setVideoURI(Uri.parse("http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4"));

            holder.videoViewMedia.setVideoURI(Uri.parse(videourl));
            holder.videoViewMedia.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    holder.videoViewMedia.start();
                }
            });
        } else {
            String imagedownloadUrl = serverIP + "image/" + mongoEntry.getFilename();

            holder.imageViewMedia.setVisibility(View.VISIBLE);
            holder.videoViewMedia.setVisibility(View.GONE);

            Glide.with(holder.itemView.getContext())
                    .load(imagedownloadUrl)
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

