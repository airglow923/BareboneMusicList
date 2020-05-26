package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.media.MediaPlayer;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static com.example.musicplayer.MusicLoader.musicList;

public class MusicPlayerFragment extends Fragment {

    private MediaPlayer mediaPlayer;
    private MediaPlayer.OnCompletionListener onCompletionListener = MediaPlayer::release;
    private int index;

    public MusicPlayerFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_player_main, container, false);

        Music music = getActivity().getIntent().getParcelableExtra("music");
        Uri musicUri = music.getUri();

        if (musicUri != null) {
            mediaPlayer = MediaPlayer.create(getContext(), music.getUri());
            mediaPlayer.setOnCompletionListener(onCompletionListener);
        } else {
            mediaPlayer = null;
        }

        index = getIndexFromArray(musicUri, musicList);
        updateMusicPlayer(music);

        int nextInt = getNextIndex(index, musicList);
        if (nextInt != -1) {
            mediaPlayer.setNextMediaPlayer(
                    MediaPlayer.create(getContext(), musicList.get(nextInt).getUri()));
        }

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    public void playOrPause(View view) {
        View rootView = getView().getRootView();

        if (mediaPlayer != null) {
            ImageView imageView = rootView.findViewById(R.id.image_music_player_play_pause);

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
        View rootView = getView().getRootView();

        ((ImageView) rootView.findViewById(R.id.image_music_player_play_pause))
                .setImageResource(R.drawable.play_button);
        index = getPreviousIndex(index, musicList);
        Uri musicUri = index == -1 ? null : musicList.get(index).getUri();

        if (musicUri != null) {
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getContext(), musicUri);
            updateMusicPlayer(musicList.get(index));

            int nextIndex = getNextIndex(index, musicList);
            Uri nextUri = index == -1 ? null : musicList.get(index).getUri();

            if (nextUri != null) {
                mediaPlayer.setNextMediaPlayer(
                        MediaPlayer.create(getContext(), nextUri));
            }
        }
    }

    public void goToNext(View view) {
        View rootView = getView().getRootView();

        ((ImageView) rootView.findViewById(R.id.image_music_player_play_pause))
                .setImageResource(R.drawable.play_button);
        index = getNextIndex(index, musicList);
        Uri musicUri = index == -1 ? null : musicList.get(index).getUri();

        if (musicUri != null) {
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getContext(), musicUri);
            updateMusicPlayer(musicList.get(index));

            int nextIndex = getNextIndex(index, musicList);
            Uri nextUri = index == -1 ? null : musicList.get(index).getUri();

            if (nextUri != null) {
                mediaPlayer.setNextMediaPlayer(
                        MediaPlayer.create(getContext(), nextUri));
            }
        }
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void updateMusicPlayer(Music music) {
        View rootView = getView().getRootView();
        String artistAlbum = music.getArtist() + " - " + music.getAlbum();

        TextView titleView = rootView.findViewById(R.id.text_music_player_title);
        TextView artistAlbumView = rootView.findViewById(R.id.text_music_player_artist_album);
        ImageView albumCoverView = rootView.findViewById(R.id.image_music_player_album_cover);

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
