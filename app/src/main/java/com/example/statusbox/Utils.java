package com.example.statusbox;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utils {

    // -------------------------------
    // COPY FILE
    // -------------------------------
    public static void copyFile(File source, File dest) {
        try (FileInputStream in = new FileInputStream(source);
             FileOutputStream out = new FileOutputStream(dest)) {

            byte[] buffer = new byte[4096];
            int length;

            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -------------------------------
    // REFRESH GALLERY VIEW
    // -------------------------------
    public static void refreshGallery(Context context, File file) {
        MediaScannerConnection.scanFile(
                context,
                new String[]{file.getAbsolutePath()},
                null,
                (path, uri) -> {}
        );
    }

    // -------------------------------
    // SHOW TOAST
    // -------------------------------
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // -------------------------------
    // GET MIME TYPE
    // -------------------------------
    public static String getMimeType(String filePath) {
        String ext = MimeTypeMap.getFileExtensionFromUrl(filePath);

        if (ext == null || ext.isEmpty()) {
            return "*/*";
        }

        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);

        return mime != null ? mime : "*/*";
    }

    // -------------------------------
    // SHARE FILE
    // -------------------------------
    public static void shareFile(Context context, String filePath) {
        try {
            File file = new File(filePath);

            if (!file.exists()) {
                showToast(context, "File not found");
                return;
            }

            Uri fileUri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".provider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType(getMimeType(filePath));
            intent.putExtra(Intent.EXTRA_STREAM, fileUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(Intent.createChooser(intent, "Share using"));

        } catch (Exception e) {
            e.printStackTrace();
            showToast(context, "Unable to share file");
        }
    }

    // -------------------------------
    // OPEN FILE (PREVIEW)
    // -------------------------------
    public static void openFile(Context context, String filePath) {
        try {
            File file = new File(filePath);

            if (!file.exists()) {
                showToast(context, "File not found");
                return;
            }

            Uri fileUri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".provider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, getMimeType(filePath));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            showToast(context, "No app available to open this file");
        }
    }
}
