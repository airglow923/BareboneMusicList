package com.example.musicplayer;

import android.graphics.Bitmap;

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

    public Set<SoftReference<Bitmap>> getReusableObjects() {
        return reusableObjects;
    }

    public void setReusableObjects(Set<SoftReference<Bitmap>> reusableObjects) {
        this.reusableObjects = reusableObjects;
    }

    public LruCache<String, Bitmap> getMemoryCache() {
        return memoryCache;
    }

    public void setMemoryCache(LruCache<String, Bitmap> memoryCache) {
        this.memoryCache = memoryCache;
    }

    public CacheBitmap(int maxSize) {
        memoryCache = new LruCache<String, Bitmap>(maxSize) {
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
}