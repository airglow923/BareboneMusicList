package com.example.musicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
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

    static final String TAG = MediaPlayerService.class.getSimpleName();
    static final String LOG_TAG = MediaPlayerService.class.getSimpleName();

    public static final String ACTION_PLAY = "com.example.musicplayer.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.musicplayer.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.example.musicplayer.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.example.musicplayer.ACTION_NEXT";
    public static final String ACTION_STOP = "com.example.musicplayer.ACTION_STOP";

    private final IBinder iBinder = new LocalBinder();
    private MediaPlayer mediaPlayer;
    private int resumePosition;
    private AudioManager audioManager;

    // to detect change of audio output
    private BroadcastReceiver audioOutputReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pauseMedia();
            buildNotification(PlaybackState.STATE_PLAYING);
        }
    };

    // check phone call
    private boolean receivingCall = false;
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;

    private int audioIndex = -1;
    private Music activeMusic;
    private BroadcastReceiver playNewAudioReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            audioIndex = new StorageUtil(getApplicationContext()).loadAudioIndex();

            if (audioIndex != -1 && audioIndex < musicList.size()) {
                activeMusic = musicList.get(audioIndex);
            } else {
                stopSelf();
            }

            stopMedia();
            mediaPlayer.reset();
            initMediaPlayer();
            updateMetaData();
            buildNotification(PlaybackState.STATE_PLAYING);
        }
    };

    // MediaSession for notification
    private MediaSessionManager mediaSessionManager;
    private MediaSession mediaSession;
    private MediaController.TransportControls transportControls;

    private static final int NOTIFICATION_ID = 777;

    @Override
    public void onCreate() {
        super.onCreate();
        callStateListener();
        registerAudioOutputReceiver();
        registerPlayNewAudioReceiver();
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
            StorageUtil storage = new StorageUtil(getApplicationContext());
            musicList = storage.loadAudio();
            audioIndex = storage.loadAudioIndex();

            if (audioIndex != -1 && audioIndex < musicList.size()) {
                activeMusic = musicList.get(audioIndex);
            } else {
                stopSelf();
            }
        } catch (NullPointerException e) {
            stopSelf();
        }

        if (!requestAudioFocus()) {
            stopSelf();
        }

        if (mediaSessionManager == null) {
            try {
                initMediaSession();
                initMediaPlayer();
            } catch (RemoteException e) {
                e.printStackTrace();
                stopSelf();
            }
            buildNotification(PlaybackState.STATE_PLAYING);
        }

        handleIncomingAction(intent);
        return super.onStartCommand(intent, flags, startId);
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
        mediaPlayer = new MediaPlayer();
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
            mediaPlayer.setDataSource(getApplicationContext(), activeMusic.getUri());
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }

        mediaPlayer.prepareAsync();
    }

    private void initMediaSession() throws RemoteException {
        if (mediaSessionManager != null) {
            return;
        }

        mediaSessionManager =
                (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        mediaSession = new MediaSession(getApplicationContext(), LOG_TAG);
        transportControls = mediaSession.getController().getTransportControls();
        mediaSession.setActive(true);

        updateMetaData();

        mediaSession.setCallback(new MediaSession.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                resumeMedia();
                buildNotification(PlaybackState.STATE_PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();
                resumeMedia();
                buildNotification(PlaybackState.STATE_PAUSED);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                skipToNext();
                buildNotification(PlaybackState.STATE_PLAYING);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                skipToPrevious();
                buildNotification(PlaybackState.STATE_PLAYING);
            }

            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                stopSelf();
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
            }
        });
    }

    private void updateMetaData() {
        Bitmap albumCover = activeMusic.getAlbumCover();
        mediaSession.setMetadata(new MediaMetadata.Builder()
                .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, albumCover)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, activeMusic.getArtist())
                .putString(MediaMetadata.METADATA_KEY_ALBUM, activeMusic.getAlbum())
                .putString(MediaMetadata.METADATA_KEY_TITLE, activeMusic.getTitle())
                .build());
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

    private void skipToNext() {
        if (audioIndex == musicList.size() - 1) {
            audioIndex = 0;
            activeMusic = musicList.get(audioIndex);
        } else {
            activeMusic = musicList.get(++audioIndex);
        }

        new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);

        stopMedia();
        mediaPlayer.reset();
        initMediaPlayer();
    }

    private void skipToPrevious() {
        if (audioIndex == 0) {
            audioIndex = musicList.size() - 1;
            activeMusic = musicList.get(audioIndex);
        } else {
            activeMusic = musicList.get(--audioIndex);
        }

        new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);

        stopMedia();
        mediaPlayer.reset();
        initMediaPlayer();
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

    private void buildNotification(int playbackState) {
        int notificationAction = R.drawable.pause_button;
        PendingIntent playPauseAction = null;

        if (playbackState == PlaybackState.STATE_PLAYING) {
            notificationAction = R.drawable.pause_button;
            playPauseAction = playbackAction(0);
        }

        // Bitmap image here

        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setShowWhen(false)
                .setStyle(new Notification.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2))
                .setColor(getResources().getColor(R.color.colorPrimary
                        , getBaseContext().getTheme()))
//                .setLargeIcon()
//                .setSmallIcon()
                .setContentText(activeMusic.getArtist())
                .setContentTitle(activeMusic.getAlbum())
                .setSubText(activeMusic.getArtist())
                .addAction(new Notification.Action.Builder(
                        Icon.createWithResource(this, R.drawable.previous_button)
                        , "previous", playbackAction(3)).build())
                .addAction(new Notification.Action.Builder(
                        Icon.createWithResource(this, notificationAction)
                        , "pause", playPauseAction).build())
                .addAction(new Notification.Action.Builder(
                        Icon.createWithResource(this, R.drawable.next_button)
                        , "next", playbackAction(2)).build());

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(
                NOTIFICATION_ID, notificationBuilder.build());
    }

    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, MediaPlayerService.class);
        switch (actionNumber) {
            case 0: playbackAction.setAction(ACTION_PLAY);     break;
            case 1: playbackAction.setAction(ACTION_PAUSE);    break;
            case 2: playbackAction.setAction(ACTION_NEXT);     break;
            case 3: playbackAction.setAction(ACTION_PREVIOUS); break;
            default: return null;
        }
        return PendingIntent.getService(this, actionNumber, playbackAction, 0);
    }

    private void handleIncomingAction(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) {
            return;
        }

        String actionString = playbackAction.getAction();

        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
            transportControls.skipToNext();
        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
            transportControls.skipToNext();
        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
        }
    }

    private void removeNotification() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
