package com.example.statusbox;

import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class FullScreenActivity extends AppCompatActivity {

    private ImageView imageView, closeButton;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Root FrameLayout
        FrameLayout root = new FrameLayout(this);

        // ImageView for image
        imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        root.addView(imageView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        // VideoView for video
        videoView = new VideoView(this);
        root.addView(videoView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        // Close button
        closeButton = new ImageView(this);
        closeButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        FrameLayout.LayoutParams btnParams = new FrameLayout.LayoutParams(120, 120);
        btnParams.gravity = Gravity.TOP | Gravity.END;
        btnParams.setMargins(16,16,16,16);
        closeButton.setLayoutParams(btnParams);
        root.addView(closeButton);

        setContentView(root);

        Status status = (Status) getIntent().getSerializableExtra("status");
        if(status == null) return;

        closeButton.setOnClickListener(v -> finish());

        if(status.isVideo()) showVideo(status.getPath());
        else showImage(status.getPath());
    }

    private void showImage(String path) {
        imageView.setVisibility(FrameLayout.VISIBLE);
        videoView.setVisibility(FrameLayout.GONE);
        Glide.with(this).load(path).into(imageView);
    }

    private void showVideo(String path) {
        imageView.setVisibility(FrameLayout.GONE);
        videoView.setVisibility(FrameLayout.VISIBLE);

        videoView.setVideoURI(Uri.parse(path));
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.start();
    }
}
