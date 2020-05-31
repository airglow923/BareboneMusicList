package com.example.musicplayer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;

import static com.example.musicplayer.MusicLoader.musicList;

/*
 * A Service is an application component that can perform long-running operation in the background.
 *
 * See: https://developer.android.com/guide/components/services
 *
 * Services run in the main thread of their hosting process, meaning that it would take up CPU
 * resources and block some of the operations.
 *
 * Types of services
 *
 *      - Foreground
 *          A foreground service performs some operation that is noticeable to the user.
 *          Foreground services must display a Notification. Foreground services continue running
 *          even when the user is not interacting with the app.
 *
 *      - Background
 *          A background service performs an operation that is not directly noticed by the user.
 *
 *      - Bound
 *          A service is bound when an application component binds to it by calling bindService().
 *          A bound service offers a client-server interface that allows components to interact
 *          with the service, send requests, receive results, and even do so across processes with
 *          inter-process communication (IPC).
 */

/*
 * Most of the implementation here followed the link below
 *
 * https://www.sitepoint.com/a-step-by-step-guide-to-building-an-android-audio-player-app/
 */

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener
        , MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener
        , MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener
        , MediaPlayer.OnBufferingUpdateListener, AudioManager.OnAudioFocusChangeListener {

    private final IBinder iBinder = new LocalBinder();
    private MediaPlayer mediaPlayer;
    private Uri musicUri;
    private int resumePosition;
    private AudioManager audioManager;

    // to detect change of audio output
    private BroadcastReceiver audioOutputReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pauseMedia();
            buildNotification(PlaybackState.STATE_PAUSED)
        }
    };

    // check phone call
    private boolean receivingCall = false;
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;

    private int musicIndex = -1;
    private Music activeMusic;
    private BroadcastReceiver playNewAudioReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            musicIndex = new StorageUtil(getApplicationContext()).loadAudioIndex();

            if (musicIndex != -1 && musicIndex < musicList.size()) {
                activeMusic = musicList.get(musicIndex);
            } else {
                stopSelf();
            }

            stopMedia();
            mediaPlayer.reset();
            initMediaPlayer();
            updateMetaData();
            buildNotification(PlaybackStatus.PLAYING);
        }
    };

    public MediaPlayerService(Uri uri) {
        musicUri = uri;
        mediaPlayer = MediaPlayer.create(this, uri);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        callStateListener();
        registerAudioOutputReceiver();
        registerPlayNewAudioReceiver();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopMedia();
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK "
                        + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED" + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playMedia();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (mediaPlayer == null) {
                    initMediaPlayer();
                } else if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.setVolume(0.1f, 0.1f);
                }
                break;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            musicUri = Uri.parse(intent.getExtras().getString("musicUri"));
        } catch (NullPointerException e) {
            stopSelf();
        }

        if (!requestAudioFocus()) {
            stopSelf();
        }

        if (musicUri != null) {
            initMediaPlayer();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }

        removeAudioFocus();

        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        removeNotification();

        unregisterReceiver(audioOutputReceiver);
        unregisterReceiver(playNewAudioReceiver);

        new StorageUtil(getApplicationContext()).clearCachedAudioPlaylist();
    }

    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    private void initMediaPlayer() {
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.reset();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());

        try {
            mediaPlayer.setDataSource(this, musicUri);
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }

        mediaPlayer.prepareAsync();
    }

    private void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopMedia() {
        if (mediaPlayer == null) {
            return;
        }

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    private void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC
                , AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true;
        }

        // AUDIOFOCUS_REQUEST_FAILED, AUDIOFOCUS_REQUEST_DELAYED
        return false;
    }

    private boolean removeAudioFocus() {
        return audioManager.abandonAudioFocus(this) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private void registerAudioOutputReceiver() {
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(audioOutputReceiver, intentFilter);
    }

    private void registerPlayNewAudioReceiver() {
        IntentFilter intentFilter = new IntentFilter(MusicPlayerActivity.Broadcast_PLAY_NEW_AUDIO);
        registerReceiver(playNewAudioReceiver, intentFilter);
    }

    private void callStateListener() {
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                            receivingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (mediaPlayer != null) {
                            if (receivingCall) {
                                receivingCall = false;
                                resumeMedia();
                            }
                        }
                        break;
                }
            }
        };

        // register listener to telephony manager
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }
}
