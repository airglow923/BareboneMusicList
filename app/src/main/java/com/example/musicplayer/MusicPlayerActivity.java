package com.example.musicplayer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MusicPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player_main);

        String title = getIntent().getStringExtra("title");
        String artist = getIntent().getStringExtra("artist");
        String album = getIntent().getStringExtra("album");
        String artistAlbum = artist + " - " + album;
        int albumCover = getIntent().getIntExtra("albumCover", 0);

        TextView titleView = findViewById(R.id.text_music_player_title);
        TextView artistAlbumView = findViewById(R.id.text_music_player_artist_album);
        ImageView albumCoverView = findViewById(R.id.image_music_player_album_cover);

        titleView.setText(title);
        artistAlbumView.setText(artistAlbum);
        albumCoverView.setImageResource(albumCover);
    }

    public void playPause(View view) {
        // TODO
    }

    public void backward(View view) {
        // TODO
    }

    public void forward(View view) {
        // TODO
    }
}
