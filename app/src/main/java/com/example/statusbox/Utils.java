package com.example.statusbox;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utils {

    public static void copyFile(File source, File dest) {
        try (FileInputStream in = new FileInputStream(source);
             FileOutputStream out = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) out.write(buffer, 0, length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void refreshGallery(Context context, File file) {
        MediaScannerConnection.scanFile(context,
                new String[]{file.getAbsolutePath()},
                null,
                (path, uri) -> { });
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
