package com.example.musicplayer;

import android.app.Service;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

/*
 * https://developer.android.com/guide/components/services
 *
 * A Service is an application component that can perform long-running operation in the background.
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

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener
        , MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener
        , MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener
        , MediaPlayer.OnBufferingUpdateListener, AudioManager.OnAudioFocusChangeListener {

    private final IBinder iBinder = new LocalBinder();

    private class LocalBinder extends Binder {}
}
