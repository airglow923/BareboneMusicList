package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnClickListener;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    private void logDirectory() {
        File file = new File("").getAbsoluteFile();
        try {
            file.createNewFile();
            File[] files = file.getParentFile().listFiles();
            for (File inFile : files) {
                Log.i("File directory: ", inFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        Log.i("Finished printing", "asdfasdfasd");
    }
}
