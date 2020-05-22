package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.media.MediaPlayer;

import java.io.ByteArrayOutputStream;

public class MusicPlayerActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

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

        if (music.getAlbumCover() == null) {
            albumCoverView.setImageResource(R.drawable.default_album_cover);
        } else {
            albumCoverView.setImageBitmap(music.getAlbumCover());
        }
    }

    public void playPause(View view) {
        ImageView imageView = findViewById(R.id.image_music_player_play_pause);

        if (mediaPlayer.isPlaying()) {
            imageView.setImageResource(R.drawable.pause_button);
            mediaPlayer.pause();
        } else {
            imageView.setImageResource(R.drawable.play_button);
            mediaPlayer.start();
        }
    }

    public void backward(View view) {
        // TODO
    }

    public void forward(View view) {
        // TODO
    }

    private static String stem(String filename) {
        return filename.substring(0, filename.lastIndexOf('.'));
    }

    private static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
