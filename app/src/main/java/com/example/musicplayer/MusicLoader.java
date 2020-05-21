package com.example.musicplayer;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

final class MusicLoader {

    private static Cursor cursor;

    static List<Music> musicList = new ArrayList<Music>();

//    private String title;
//    private String album;
//    private String artist;
//    private String albumArtist;
//    private String genre;
//    private String year;
//    private String track;
//    private byte[] albumCover = new byte[0];

    public static void loadMusicFromFolder(Context context) {
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.TRACK
        };

        /**
         * After dealing with a shit ton of documentations and StackOverflow questions, I finally
         * figured out what MediaStore really does. MediaStore categorises files inside "Download"
         * folder and its subfolders, and based on the query, it returns the results to the user.
         *
         * One thing to note is that EXTERNAL_CONTENT_API refers to the root directory of the
         * primary external storage, which is not SD card. Because, removable SD card is considered
         * as another type of external storage (I also don't know why Android dev team named it in
         * this way).
         *
         * On the other hand, INTERNAL_CONTENT_API is a app-specific directory that can only be
         * accessed by the app itself.
         *
         * MediaStore looks for a scoped storage, which is a shared folder among all the apps.
         */

        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                , projection, MediaStore.Audio.Media.IS_MUSIC + " = 1", selectionArgs, sortOrder);

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

                Log.i("MediaStore: ", artist + " - " + title);
            }
        } else {
            Log.i("MediaStore", "cursor is null or empty");
        }
    }

    private static String[] listToSelectionArguments(List<String> list) {
        for (String item : list) {
            item = "%" + item + "%";
        }

        return (String[]) list.toArray();
    }
}
