package com.example.musicplayer;

public class NotAudioException extends Exception {

    private static final long serialVersionUID = 1111L;

    public NotAudioException() {
        super();
    }

    public NotAudioException(String message) {
        super(message);
    }

    public NotAudioException(String message, Throwable cause) {
        super(message, cause);
    }
}
