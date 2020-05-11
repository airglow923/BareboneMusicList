package com.example.musicplayer;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MusicPlayerActivity extends AppCompatActivity {

    private Music music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player_main);

        String title = getIntent().getStringExtra("title");
        String artist = getIntent().getStringExtra("artist");
        String album = getIntent().getStringExtra("album");
        int albumCover = getIntent().getIntExtra("albumCover", 0);

        TextView titleView = findViewById(R.id.text_music_player_title);
        TextView artistAlbumView = findViewById(R.id.text_music_player_artist_album);
        ImageView albumCoverView = findViewById(R.id.image_music_player_album_cover);

        titleView.setText(title);
        artistAlbumView.setText(artist + " - " + album);
        albumCoverView.setImageResource(albumCover);
    }
}
