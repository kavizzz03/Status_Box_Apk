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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 2296;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private StatusPagerAdapter pagerAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<Status> imageStatusList = new ArrayList<>();
    private List<Status> videoStatusList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- Initialize Views ---
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);

        // --- Set Adapter ---
        pagerAdapter = new StatusPagerAdapter(this, imageStatusList, videoStatusList);
        viewPager.setAdapter(pagerAdapter);

        // --- Tabs setup ---
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "Images" : "Videos"))
                .attach();

        // --- Swipe to refresh listener ---
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadStatuses();
            swipeRefreshLayout.setRefreshing(false);
        });

        // --- Check Permissions ---
        if (!hasPermission()) {
            requestPermission();
        } else {
            loadStatuses();
        }
    }

    // ----------- ALL FILE ACCESS CHECK -----------
    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }

        int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return read == PackageManager.PERMISSION_GRANTED;
    }

    // ----------- REQUEST ALL FILE ACCESS -----------
    private void requestPermission() {
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
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST);
        }
    }

    // ----------- CALLBACK FOR PERMISSION -----------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_REQUEST) {
            if (hasPermission()) {
                loadStatuses();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {
            if (hasPermission()) {
                loadStatuses();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ----------- LOAD WHATSAPP STATUS FILES -----------
    private void loadStatuses() {
        imageStatusList.clear();
        videoStatusList.clear();

        // Old WhatsApp path
        String pathOld = Environment.getExternalStorageDirectory() + "/WhatsApp/Media/.Statuses";

        // New Android/media WhatsApp path (Android 11+)
        String pathNew = Environment.getExternalStorageDirectory() + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";

        addStatusFiles(new File(pathOld));
        addStatusFiles(new File(pathNew));

        if (imageStatusList.isEmpty() && videoStatusList.isEmpty()) {
            Toast.makeText(this, "No statuses found", Toast.LENGTH_SHORT).show();
        }

        pagerAdapter.notifyDataSetChanged();
    }

    // ----------- ADD STATUS FILES -----------
    private void addStatusFiles(File folder) {
        if (folder == null || !folder.exists()) return;

        File[] files = folder.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (f.isFile()) {
                String name = f.getName();
                String path = f.getAbsolutePath();

                if (name.endsWith(".mp4")) {
                    videoStatusList.add(new Status(name, path, true));
                } else if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")) {
                    imageStatusList.add(new Status(name, path, false));
                }
            }
        }
    }
}
