package com.example.wewallhere.DetailPage;

import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wewallhere.R;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageViewMedia;
    public ImageView imageViewThumbnail;
    public VideoView videoViewMedia;
    public ImageView imageViewProfilePic;
    public TextView textViewTitle;
    public TextView textViewUploader;
    public TextView textViewDate;
    public RelativeLayout loadingPanel;
    public MediaController mediaController;


    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        imageViewMedia = itemView.findViewById(R.id.imageViewMedia);
        imageViewProfilePic = itemView.findViewById(R.id.imageViewProfilePic);
        textViewTitle = itemView.findViewById(R.id.textViewTitle);
        textViewUploader = itemView.findViewById(R.id.textViewUploader);
        textViewDate = itemView.findViewById(R.id.textViewDate);
        videoViewMedia = itemView.findViewById(R.id.videoViewMedia);
        imageViewThumbnail = itemView.findViewById(R.id.imageViewThumbnail);
        loadingPanel = itemView.findViewById(R.id.loadingPanel);
        mediaController = new MediaController(itemView.getContext());

    }


}

