package com.example.musicplayer;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class MusicLoader {

    private static Cursor cursor;

    static List<String> musicFolders = Arrays.asList(
            "Music"
    );

    static List<Music> musicList = new ArrayList<Music>();

    public static void loadMusicFromFolder(Context context) {
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.TRACK
        };

//        String selection = MediaStore.Audio.Media.DATA + " like ? ";
        String selection = null;
//        String[] selectionArgs = listToSelectionArguments(musicFolders);
        String[] selectionArgs = null;
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        cursor = context.getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI
                , projection, selection, selectionArgs, sortOrder);

        // prevent NullPointerException
        if (cursor != null && cursor.getCount() > 0) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
            int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            int yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR);
            int trackColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                String title = cursor.getString(titleColumn);
                String album = cursor.getString(albumColumn);
                String artist = cursor.getString(artistColumn);
                String year = cursor.getString(yearColumn);
                String track = cursor.getString(trackColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.INTERNAL_CONTENT_URI, id);

                Log.i("MediaStore: ", artist + " - " + title);
            }
        }
    }

    private static String[] listToSelectionArguments(List<String> list) {
        for (String item : list) {
            item = "%" + item + "%";
        }

        return (String[]) list.toArray();
    }
}
