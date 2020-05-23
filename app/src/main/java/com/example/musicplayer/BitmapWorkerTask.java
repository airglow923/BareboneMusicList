package com.example.musicplayer;

import android.graphics.Bitmap;

import java.util.concurrent.Callable;

class BitmapWorkerTask implements Callable<Bitmap> {
    private final String input;

    BitmapWorkerTask(String input) {
        this.input = input;
    }

    @Override
    public Bitmap call() {
        Bitmap bitmap = null;
        return bitmap;
    }
}
