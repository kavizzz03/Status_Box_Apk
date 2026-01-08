package com.example.statusbox;

import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
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
        setContentView(R.layout.activity_full_screen);

        imageView = findViewById(R.id.fullscreen_image);
        videoView = findViewById(R.id.fullscreen_video);
        closeButton = findViewById(R.id.closeButton);

        Status status = (Status) getIntent().getSerializableExtra("status");
        if (status == null) finish();

        closeButton.setOnClickListener(v -> finish());

        if (status.isVideo()) showVideo(status.getPath());
        else showImage(status.getPath());

        // Swipe down to close (like WhatsApp)
        GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD && diffY > 0) {
                    finish();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.activity_full_screen).setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    private void showImage(String path) {
        imageView.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);

        Glide.with(this)
                .load(path)
                .fitCenter()
                .into(imageView);

        imageView.setAlpha(0f);
        imageView.animate().alpha(1f).setDuration(250).start(); // fade-in animation
    }

    private void showVideo(String path) {
        imageView.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);

        videoView.setVideoURI(Uri.parse(path));
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true); // optional: loop video like WhatsApp
            videoView.start();
            videoView.setAlpha(0f);
            videoView.animate().alpha(1f).setDuration(300).start(); // fade-in animation
        });
    }
}
