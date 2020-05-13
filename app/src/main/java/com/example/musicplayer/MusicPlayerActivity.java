package com.example.musicplayer;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MusicPlayerActivity extends AppCompatActivity {

    private boolean isPlayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player_main);

        Music music = getIntent().getParcelableExtra("music");
        String artistAlbum = music.getArtist() + " - " + music.getAlbum();

        TextView titleView = findViewById(R.id.text_music_player_title);
        TextView artistAlbumView = findViewById(R.id.text_music_player_artist_album);
        ImageView albumCoverView = findViewById(R.id.image_music_player_album_cover);

        titleView.setText(music.getTitle());
        artistAlbumView.setText(artistAlbum);
        albumCoverView.setImageResource(music.getAlbumCover());
    }

    public void playPause(View view) {
        ImageView imageView = findViewById(R.id.image_music_player_play_pause);

        if (isPlayed) {
            imageView.setImageResource(R.drawable.pause_button);
        } else {
            imageView.setImageResource(R.drawable.play_button);
        }

        isPlayed = !isPlayed;
    }

    public void backward(View view) {
        // TODO
    }

    public void forward(View view) {
        // TODO
    }
}
