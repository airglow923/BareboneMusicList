package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.media.MediaPlayer;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static com.example.musicplayer.MusicLoader.musicList;

public class MusicPlayerActivity extends AppCompatActivity {

    static final String TAG = MusicPlayerActivity.class.getSimpleName();
    static final String LOG_TAG = MusicPlayerActivity.class.getSimpleName();

    private MediaPlayer mediaPlayer;
    private MediaPlayer.OnCompletionListener onCompletionListener = mp -> mp.release();
    private AudioManager audioManager;
    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                            || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        mediaPlayer.pause();
                        mediaPlayer.seekTo(0);
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        mediaPlayer.start();
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        releaseMedia();
                    }
                }
            };
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player_main);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Music music = getIntent().getParcelableExtra("music");
        Uri musicUri = music.getUri();

        if (musicUri != null) {
            mediaPlayer = MediaPlayer.create(this, music.getUri());
            mediaPlayer.setOnCompletionListener(onCompletionListener);
        } else {
            mediaPlayer = null;
        }

        index = getIndexFromArray(musicUri, musicList);
        updateMusicPlayer(music);

        releaseMedia();

        int result = audioManager.requestAudioFocus(onAudioFocusChangeListener
                , AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//            audioManager.registerMediaButtonEventReceiver();
        }

        int nextInt = getNextIndex(index, musicList);
        if (nextInt != -1) {
            mediaPlayer.setNextMediaPlayer(
                    MediaPlayer.create(this, musicList.get(nextInt).getUri()));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.release();
    }

    public void playOrPause(View view) {
        if (mediaPlayer != null) {
            ImageView imageView = findViewById(R.id.image_music_player_play_pause);

            if (mediaPlayer.isPlaying()) {
                imageView.setImageResource(R.drawable.play_button);
                mediaPlayer.pause();
            } else {
                imageView.setImageResource(R.drawable.pause_button);
                mediaPlayer.start();
            }
        }
    }

    public void goToPrevious(View view) {
        ((ImageView) findViewById(R.id.image_music_player_play_pause))
                .setImageResource(R.drawable.play_button);
        index = getPreviousIndex(index, musicList);
        Uri musicUri = index == -1 ? null : musicList.get(index).getUri();

        if (musicUri != null) {
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(this, musicUri);
            updateMusicPlayer(musicList.get(index));

            int nextIndex = getNextIndex(index, musicList);
            Uri nextUri = index == -1 ? null : musicList.get(index).getUri();

            if (nextUri != null) {
                mediaPlayer.setNextMediaPlayer(
                        MediaPlayer.create(this, nextUri));
            }
        }
    }

    public void goToNext(View view) {
        ((ImageView) findViewById(R.id.image_music_player_play_pause))
                .setImageResource(R.drawable.play_button);
        index = getNextIndex(index, musicList);
        Uri musicUri = index == -1 ? null : musicList.get(index).getUri();

        if (musicUri != null) {
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(this, musicUri);
            updateMusicPlayer(musicList.get(index));

            int nextIndex = getNextIndex(index, musicList);
            Uri nextUri = index == -1 ? null : musicList.get(index).getUri();

            if (nextUri != null) {
                mediaPlayer.setNextMediaPlayer(
                        MediaPlayer.create(this, nextUri));
            }
        }
    }

    private void releaseMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        }
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

    private static int getPreviousIndex(int index, List<Music> musics) {
        return index < 0 ? -1 : --index;
    }

    private static int getNextIndex(int index, List<Music> musics) {
        return index == -1 || index + 1 == musics.size() ? -1 : ++index;
    }

    private static int getIndexFromArray(Uri uri, List<Music> musics) {
        if (uri == null || musics == null)
            return -1;

        for (int i = 0; i < musics.size(); ++i) {
            if (musics.get(i).getUri().equals(uri)) {
                return i;
            }
        }
        return -1;
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
