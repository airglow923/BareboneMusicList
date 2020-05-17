package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnClickListener;

import static com.example.musicplayer.PermissionControl.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        processMultiplePermission(this, PERMISSION_RATIONALE);

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

        // app-specific directory
        Log.i("getExternalFilesDir", this.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getPath());
        // /data
        Log.i("getDataDirectory", Environment.getDataDirectory().getPath());
        logDirectory();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_MEDIA_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {}
                break;
            case PERMISSION_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {}
                break;
            case PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {}
                break;
            case PERMISSION_ALL:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {}
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void logDirectory() {
        // /internal/file
        Uri uri = MediaStore.Files.getContentUri(MediaStore.VOLUME_INTERNAL);
        Log.i("MediaStore: ", uri.getPath());

//        try {
//            if (file.createNewFile()) {
//                Log.i("createNewFile(): ", "File created successfully");
//            }
//        } catch (IOException e) {
//        }
//        Log.i("Path of external dir:", file.getAbsolutePath());
//        try {
//            File[] files = file.getParentFile().listFiles();
//            if (files == null) {
//                throw new IOException();
//            }
//            for (File inFile : files) {
//                Log.i("File directory: ", inFile.getAbsolutePath());
//            }
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//        Log.i("Finished printing", "text placeholder");
    }
}
