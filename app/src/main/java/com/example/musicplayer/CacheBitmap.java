package com.example.musicplayer;

import android.graphics.Bitmap;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.collection.LruCache;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.example.musicplayer.AndroidVersion.*;

public final class CacheBitmap {

    private Set<SoftReference<Bitmap>> reusableObjects = IS_ANDROID_HONEYCOMB
            ? Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>())
            : new HashSet<SoftReference<Bitmap>>();
    private LruCache<String, Bitmap> memoryCache;

    public CacheBitmap() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(@NonNull String key, @NonNull Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToCache(String key, Bitmap value) {
        if (getBitmapFromCache(key) == null) {
            memoryCache.put(key, value);
        }
    }

    public Bitmap getBitmapFromCache(String key) {
        return memoryCache.get(key);
    }

    public void loadBitmap(int placedholder) {}
}