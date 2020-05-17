package com.example.musicplayer;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.List;

final class MusicLoader {

    public static List<String> musicDirectories = Arrays.asList(
            Environment.getDataDirectory().getPath()
    );

    public static void updateMusicLibrary(Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Audio.Media.MIME_TYPE, "audio/*");
        contentValues.put(MediaStore.Audio.Media.RELATIVE_PATH, "Music");
    }
}
