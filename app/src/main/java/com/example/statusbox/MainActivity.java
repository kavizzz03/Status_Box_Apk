package com.example.statusbox;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 100;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private StatusPagerAdapter pagerAdapter;

    private List<Status> imageStatusList = new ArrayList<>();
    private List<Status> videoStatusList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        pagerAdapter = new StatusPagerAdapter(this, imageStatusList, videoStatusList);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) tab.setText("Images");
                    else tab.setText("Videos");
                }).attach();

        if (!hasStoragePermission()) requestStoragePermission();
        else loadStatuses();
    }

    private boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            return Environment.isExternalStorageManager();

        int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, PERMISSION_REQUEST);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, PERMISSION_REQUEST);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_REQUEST) {
            if (hasStoragePermission()) loadStatuses();
            else Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {
            if (hasStoragePermission()) loadStatuses();
            else Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadStatuses() {
        imageStatusList.clear();
        videoStatusList.clear();

        String oldPath = Environment.getExternalStorageDirectory() + "/WhatsApp/Media/.Statuses";
        String newPath = Environment.getExternalStorageDirectory() + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";

        addStatusesFromFiles(new File(oldPath).listFiles());
        addStatusesFromFiles(new File(newPath).listFiles());

        if (imageStatusList.isEmpty() && videoStatusList.isEmpty())
            Toast.makeText(this, "No statuses found", Toast.LENGTH_SHORT).show();
        else pagerAdapter.notifyDataSetChanged();
    }

    private void addStatusesFromFiles(File[] files) {
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String name = file.getName();
                    boolean isVideo = name.endsWith(".mp4");
                    boolean isImage = name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");

                    if (isVideo) videoStatusList.add(new Status(name, file.getAbsolutePath(), true));
                    else if (isImage) imageStatusList.add(new Status(name, file.getAbsolutePath(), false));
                }
            }
        }
    }
}
