package com.example.statusbox;

import java.io.Serializable;

public class Status implements Serializable {
    private String name;
    private String path;
    private boolean isVideo;

    public Status(String name, String path, boolean isVideo) {
        this.name = name;
        this.path = path;
        this.isVideo = isVideo;
    }

    public String getName() { return name; }
    public String getPath() { return path; }
    public boolean isVideo() { return isVideo; }
}
