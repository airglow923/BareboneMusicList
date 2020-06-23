package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.media.MediaPlayer;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static com.example.musicplayer.MusicLoader.musicList;

public class MusicPlayerActivity extends AppCompatActivity {

    public static final String Broadcast_PLAY_NEW_AUDIO = "com.example.musicplayer.PlayNewAudio";

    static final String TAG = MusicPlayerActivity.class.getSimpleName();
    static final String LOG_TAG = MusicPlayerActivity.class.getSimpleName();

    private MediaPlayerService mediaPlayerService;
    private boolean serviceBound = false;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            mediaPlayerService = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player_main);

        startService(new Intent(this, MediaPlayerService.class));
        playAudio(0);
        Music music = getIntent().getParcelableExtra("music");
        Uri musicUri = music.getUri();
        updateMusicPlayer(music);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serviceBound) {
            unbindService(serviceConnection);
            mediaPlayerService.stopSelf();
        }
    }

    public void playOrPause(View view) {
        ImageView imageView = findViewById(R.id.image_music_player_play_pause);

        if (mediaPlayerService.isPlaying()) {
            imageView.setImageResource(R.drawable.play_button);
        } else {
            imageView.setImageResource(R.drawable.pause_button);
        }
    }

    public void goToPrevious(View view) {
//        ((ImageView) findViewById(R.id.image_music_player_play_pause))
//                .setImageResource(R.drawable.play_button);
//        index = getPreviousIndex(index, musicList);
//        Uri musicUri = index == -1 ? null : musicList.get(index).getUri();
//
//        if (musicUri != null) {
//            mediaPlayer.release();
//            mediaPlayer = MediaPlayer.create(this, musicUri);
//            updateMusicPlayer(musicList.get(index));
//
//            int nextIndex = getNextIndex(index, musicList);
//            Uri nextUri = index == -1 ? null : musicList.get(index).getUri();
//
//            if (nextUri != null) {
//                mediaPlayer.setNextMediaPlayer(
//                        MediaPlayer.create(this, nextUri));
//            }
//        }
    }

    public void goToNext(View view) {
//        ((ImageView) findViewById(R.id.image_music_player_play_pause))
//                .setImageResource(R.drawable.play_button);
//        index = getNextIndex(index, musicList);
//        Uri musicUri = index == -1 ? null : musicList.get(index).getUri();
//
//        if (musicUri != null) {
//            mediaPlayer.release();
//            mediaPlayer = MediaPlayer.create(this, musicUri);
//            updateMusicPlayer(musicList.get(index));
//
//            int nextIndex = getNextIndex(index, musicList);
//            Uri nextUri = index == -1 ? null : musicList.get(index).getUri();
//
//            if (nextUri != null) {
//                mediaPlayer.setNextMediaPlayer(
//                        MediaPlayer.create(this, nextUri));
//            }
//        }
    }

    private void updateMusicPlayer(Music music) {
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

    private void playAudio(int index) {
        if (!serviceBound) {
            StorageUtil storageUtil = new StorageUtil(getApplicationContext());
            storageUtil.storeAudio(musicList);
            storageUtil.storeAudioIndex(index);

            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            playerIntent.putExtra("music", musicList.get(index));
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            StorageUtil storageUtil = new StorageUtil(getApplicationContext());
            storageUtil.storeAudioIndex(index);

            Intent intent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(intent);
        }
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
