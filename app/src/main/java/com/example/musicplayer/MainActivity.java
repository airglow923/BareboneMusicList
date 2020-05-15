package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnClickListener;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_ALL = 1111;

    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL:
                if (!grantedPermissions(grantResults)) {
                    hasPermissions(this, permissions);
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
        for (int result : results) {
            if(result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void logDirectory() {
        // Environment.getExternalStorageDirectory() returns /storage/emulated/0
        // which seems /sdcard/
        // note that internal storage has been altered when /sdcard has been altered
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/emulatordesu.txt");
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
