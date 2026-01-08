package com.example.statusbox;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Status implements Serializable {

    private String name;
    private String path;
    private boolean isVideo;
    private long size;
    private long lastModified;

    public Status(String name, String path, boolean isVideo) {
        this.name = name;
        this.path = path;
        this.isVideo = isVideo;

        // Get file info
        File file = new File(path);
        if (file.exists()) {
            this.size = file.length();
            this.lastModified = file.lastModified();
        } else {
            this.size = 0;
            this.lastModified = System.currentTimeMillis();
        }
    }

    public String getName() { return name; }
    public String getPath() { return path; }
    public boolean isVideo() { return isVideo; }

    public long getSize() { return size; }
    public long getLastModified() { return lastModified; }

    // Convert size to KB / MB
    public String getSizeString() {
        if (size <= 0) return "0 KB";

        double kb = size / 1024.0;
        double mb = kb / 1024.0;

        if (mb >= 1) {
            return String.format(Locale.getDefault(), "%.2f MB", mb);
        } else {
            return String.format(Locale.getDefault(), "%.2f KB", kb);
        }
    }

    // Convert date to readable format
    public String getDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.getDefault());
        return sdf.format(new Date(lastModified));
    }
}
