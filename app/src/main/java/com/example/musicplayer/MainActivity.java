package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnClickListener;

import org.javatuples.Triplet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 2;
    private static final int PERMISSION_READ_EXTERNAL_STORAGE = 4;
    private static final int PERMISSION_ALL =
            PERMISSION_READ_EXTERNAL_STORAGE | PERMISSION_WRITE_EXTERNAL_STORAGE;

    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static List<Triplet<String, String, Integer>> permissionRationale = Arrays.asList(
            Triplet.with(
                    PERMISSIONS[0]
                    , "In order to read and play music, you need to allow access to file."
                    , PERMISSION_READ_EXTERNAL_STORAGE),
            Triplet.with(
                    PERMISSIONS[1]
                    , "In order to update and delete music, you need to allow access to file."
                    , PERMISSION_WRITE_EXTERNAL_STORAGE)
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!hasPermissions(this, PERMISSIONS)) {
            processPermission(permissionRationale);
        }

        TextView allMusic = findViewById(R.id.text_all_music);

        allMusic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent allMusicIntent = new Intent(MainActivity.this, AllMusicActivity.class);
                startActivity(allMusicIntent);
            }
        });

        TextView album = findViewById(R.id.text_album);

        album.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent albumIntent = new Intent(MainActivity.this, AlbumActivity.class);
                startActivity(albumIntent);
            }
        });

        TextView playlist = findViewById(R.id.text_playlist);

        playlist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent playlistIntent = new Intent(MainActivity.this, PlaylistActivity.class);
                startActivity(playlistIntent);
            }
        });

        logDirectory();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL:
                if (grantedPermissions(grantResults)) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean grantedPermissions(int... results) {
        if (results != null) {
            for (int result : results) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void processPermission(@NonNull List<Triplet<String, String, Integer>> permissionList) {
        for (Triplet<String, String, Integer> permission : permissionList) {
            final String permissionName = permission.getValue0();
            final String permissionRationale = permission.getValue1();
            final int permissionCode = permission.getValue2();
            if (!shouldShowRequestPermissionRationale(permissionName)) {
                showMessageOkCancel(permission.getValue1(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(new String[]{permissionRationale}, permissionCode);
                    }
                });
            }
        }
    }

    private void showMessageOkCancel(String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", listener)
                .create()
                .show();
    }

    private void logDirectory() {
        // Environment.getExternalStorageDirectory() returns /storage/emulated/0
        // which seems /sdcard/
        // note that internal storage has been altered when /sdcard has been altered
        File file = new File(Environment.getExternalStorageDirectory().getPath()
                + "/emulatordesu.txt");
        Log.i("Path of external dir:", file.getAbsolutePath());
        try {
            File[] files = file.getParentFile().listFiles();
            if (files == null) {
                throw new IOException();
            }
            for (File inFile : files) {
                Log.i("File directory: ", inFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        Log.i("Finished printing", "text placeholder");
    }

    private boolean androidCreateFile(File file) {
        boolean result = false;

        try {
            result = file.createNewFile();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }
}
