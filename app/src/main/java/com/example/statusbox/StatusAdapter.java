package com.example.statusbox;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {

    private final Context context;
    private final List<Status> statusList;

    public StatusAdapter(Context context, List<Status> statusList) {
        this.context = context;
        this.statusList = statusList;
    }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // CardView container
        CardView cardView = new CardView(context);
        cardView.setRadius(24);
        cardView.setCardElevation(12);
        cardView.setUseCompatPadding(true);
        cardView.setContentPadding(8,8,8,8);

        // FrameLayout for stacking
        FrameLayout frameLayout = new FrameLayout(context);

        // Status Image
        ImageView statusImage = new ImageView(context);
        statusImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        frameLayout.addView(statusImage, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                500
        ));

        // Video icon overlay
        ImageView videoIcon = new ImageView(context);
        videoIcon.setImageResource(android.R.drawable.ic_media_play);
        FrameLayout.LayoutParams iconParams = new FrameLayout.LayoutParams(80, 80);
        iconParams.gravity = Gravity.TOP | Gravity.END;
        videoIcon.setLayoutParams(iconParams);
        frameLayout.addView(videoIcon);

        // Download button at bottom
        Button downloadButton = new Button(context);
        downloadButton.setText("Save");
        downloadButton.setBackgroundColor(0xFF6200EE); // Material purple
        downloadButton.setTextColor(0xFFFFFFFF);
        FrameLayout.LayoutParams btnParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                120
        );
        btnParams.gravity = Gravity.BOTTOM;
        downloadButton.setLayoutParams(btnParams);
        frameLayout.addView(downloadButton);

        cardView.addView(frameLayout);

        return new StatusViewHolder(cardView, statusImage, videoIcon, downloadButton);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {
        Status status = statusList.get(position);

        if (status.isVideo()) {
            Glide.with(context).load(status.getPath()).thumbnail(0.1f).into(holder.statusImage);
            holder.videoIcon.setVisibility(View.VISIBLE);
        } else {
            Glide.with(context).load(status.getPath()).into(holder.statusImage);
            holder.videoIcon.setVisibility(View.GONE);
        }

        holder.statusImage.setOnClickListener(v -> {
            Intent intent = new Intent(context, FullScreenActivity.class);
            intent.putExtra("status", status);
            context.startActivity(intent);
        });

        holder.downloadButton.setOnClickListener(v -> {
            File srcFile = new File(status.getPath());

            // Public folder path
            File destFolder;
            if (status.isVideo()) {
                destFolder = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MOVIES), "StatusBox");
            } else {
                destFolder = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "StatusBox");
            }

            if (!destFolder.exists()) destFolder.mkdirs();

            // Handle duplicate filenames
            String filename = srcFile.getName();
            File destFile = new File(destFolder, filename);
            int count = 1;
            while(destFile.exists()) {
                String name = filename.substring(0, filename.lastIndexOf('.'));
                String ext = filename.substring(filename.lastIndexOf('.'));
                destFile = new File(destFolder, name + "_" + count + ext);
                count++;
            }

            Utils.copyFile(srcFile, destFile);
            Utils.refreshGallery(context, destFile);
            Utils.showToast(context, "Saved to Gallery: " + destFile.getName());
        });
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    static class StatusViewHolder extends RecyclerView.ViewHolder {
        ImageView statusImage, videoIcon;
        Button downloadButton;

        public StatusViewHolder(@NonNull View itemView, ImageView statusImage, ImageView videoIcon, Button downloadButton) {
            super(itemView);
            this.statusImage = statusImage;
            this.videoIcon = videoIcon;
            this.downloadButton = downloadButton;
        }
    }
}
