package com.example.musicplayer;

import androidx.collection.LruCache;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class Cache {

    Set<SoftReference<Object>> reusableObjects =
            Collections.synchronizedSet(new HashSet<>());
    private LruCache<String, Object> memoryCache;
}