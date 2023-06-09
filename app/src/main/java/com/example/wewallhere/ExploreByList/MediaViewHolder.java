package com.example.wewallhere.ExploreByList;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;
import com.example.wewallhere.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MediaViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageViewMedia;
    public ImageView imageViewThumbnail;
    public VideoView videoViewMedia;
    public ImageView imageViewProfilePic;
    public TextView textViewTitle;
    public TextView textViewUploader;
    public TextView textViewDate;
    public RelativeLayout loadingPanel;
    public LinearLayout details;
//    public MediaController mediaController;
//    public MediaController mediaController;


    public MediaViewHolder(@NonNull View itemView) {
        super(itemView);
        imageViewMedia = itemView.findViewById(R.id.imageViewMedia);
        imageViewProfilePic = itemView.findViewById(R.id.imageViewProfilePic);
        textViewTitle = itemView.findViewById(R.id.textViewTitle);
        textViewUploader = itemView.findViewById(R.id.textViewUploader);
        textViewDate = itemView.findViewById(R.id.textViewDate);
        videoViewMedia = itemView.findViewById(R.id.videoViewMedia);
        imageViewThumbnail = itemView.findViewById(R.id.imageViewThumbnail);
        details = itemView.findViewById(R.id.details);
        loadingPanel = itemView.findViewById(R.id.loadingPanel);
//        mediaController = new MediaController(itemView.getContext());




    }
}

