package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnClickListener;

import java.util.HashMap;
import java.util.Map;

import static com.example.musicplayer.PermissionControl.*;
import static com.example.musicplayer.MusicLoader.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionControl permissionControl = new PermissionControl();
        checkAndRequestPermissions(MainActivity.this);

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
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL: {
                Map<String, Integer> permissionResults = new HashMap<>();

                for (int i = 0; i < permissions.length; ++i) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        permissionResults.put(permissions[i], grantResults[i]);
                    }
                }

                if (!permissionResults.isEmpty()) {
                    for (Map.Entry<String, Integer> entry : permissionResults.entrySet()) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this
                                , entry.getKey())) {
                            showMessageOkCancel(MainActivity.this
                                    , permissionRationale.get(entry.getKey())
                                    , new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ActivityCompat.requestPermissions(MainActivity.this
                                                    , new String[]{entry.getKey()}, requestCode);
                                        }
                                    });
                        } else {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setMessage(DENIED_PERMISSION_MESSAGE)
                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                    }
                } else {
                    // features that require file access
                    loadMusicFromFolder(this);
                }
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
