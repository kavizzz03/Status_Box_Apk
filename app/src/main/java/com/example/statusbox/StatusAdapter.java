package com.example.statusbox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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

        // CardView container with rounded corners
        CardView cardView = new CardView(context);
        cardView.setRadius(24);
        cardView.setCardElevation(8);
        cardView.setUseCompatPadding(true);
        cardView.setContentPadding(0, 0, 0, 0);
        cardView.setLayoutParams(new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        cardView.setCardBackgroundColor(Color.WHITE);

        // FrameLayout to stack items
        FrameLayout frameLayout = new FrameLayout(context);

        // Status Image
        ImageView statusImage = new ImageView(context);
        statusImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 600
        );
        frameLayout.addView(statusImage, imageParams);

        // Bottom gradient overlay
        View gradientOverlay = new View(context);
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{0xAA000000, 0x00000000} // black fade to transparent
        );
        gradientOverlay.setBackground(gradientDrawable);
        FrameLayout.LayoutParams gradientParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 200
        );
        gradientParams.gravity = Gravity.BOTTOM;
        frameLayout.addView(gradientOverlay, gradientParams);

        // Video icon overlay (centered)
        ImageView videoIcon = new ImageView(context);
        videoIcon.setImageResource(android.R.drawable.ic_media_play);
        videoIcon.setColorFilter(Color.WHITE);
        FrameLayout.LayoutParams iconParams = new FrameLayout.LayoutParams(120, 120);
        iconParams.gravity = Gravity.CENTER;
        videoIcon.setLayoutParams(iconParams);
        frameLayout.addView(videoIcon);

        // Download button as circular icon (bottom right)
        ImageButton downloadButton = new ImageButton(context);
        downloadButton.setImageResource(android.R.drawable.stat_sys_download_done);
        downloadButton.setBackgroundResource(R.drawable.circular_ripple); // ripple drawable
        downloadButton.setColorFilter(Color.WHITE);
        FrameLayout.LayoutParams btnParams = new FrameLayout.LayoutParams(100, 100);
        btnParams.gravity = Gravity.BOTTOM | Gravity.END;
        btnParams.setMargins(0,0,24,24);
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
            Utils.showToast(context, "Saved: " + destFile.getName());
        });
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    static class StatusViewHolder extends RecyclerView.ViewHolder {
        ImageView statusImage, videoIcon;
        ImageButton downloadButton;

        public StatusViewHolder(@NonNull View itemView, ImageView statusImage, ImageView videoIcon, ImageButton downloadButton) {
            super(itemView);
            this.statusImage = statusImage;
            this.videoIcon = videoIcon;
            this.downloadButton = downloadButton;
        }
    }
}
