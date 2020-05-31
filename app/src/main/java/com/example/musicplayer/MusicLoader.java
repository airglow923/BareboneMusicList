package com.example.musicplayer;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.musicplayer.AndroidVersion.*;

public final class MusicLoader {

    private static final int ALBUM_ART_WIDTH = 500;
    private static final int ALBUM_ART_HEIGHT = 500;
    public static List<Music> musicList = new ArrayList<Music>();

    public static void loadMusicFromFolder(Context context) {
        String[] mediaProjection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.TRACK
        };

        String[] genresProjection = {
                MediaStore.Audio.Genres.NAME
        };

        /**
         * After dealing with a shit ton of documentations and StackOverflow questions, I finally
         * figured out what MediaStore really does. MediaStore categorises files inside "Download"
         * folder and its subfolders, and based on the query, it returns the results to the user.
         *
         * https://developer.android.com/training/data-storage/shared/media
         *
         * The link above is the tip from the official documentation for Android MediaStore. It
         * states that
         *
         *      Audio files, which are stored in the Alarms/, Audiobooks/, Music/, Notifications/,
         *      Podcasts/, and Ringtones/ directories, as well as audio playlists that are in the
         *      Music/ or Movies/ directories. The system adds these files to the MediaStore.Audio
         *      table.
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

        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mediaProjection
                , MediaStore.Audio.Media.IS_MUSIC + " = 1", null, sortOrder);

        // prevent NullPointerException
        if (cursor != null && cursor.getCount() > 0) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            int albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
            int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
            int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            int yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR);
            int trackColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK);

            while (cursor.moveToNext()) {
                int id = cursor.getInt(idColumn);
                String title = cursor.getString(titleColumn);
                int albumId = Integer.parseInt(cursor.getString(albumIdColumn));
                String album = cursor.getString(albumColumn);
                String artist = cursor.getString(artistColumn);
                String year = cursor.getString(yearColumn);
                String track = cursor.getString(trackColumn);

                Uri musicUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                Uri genresUri = MediaStore.Audio.Genres.getContentUriForAudioId("external", id);
                String genre = null;

                try (Cursor genresCursor = context.getContentResolver().query(genresUri
                        , genresProjection, null, null, null)) {
                    if (genresCursor != null && cursor.getCount() > 0) {
                        int genresColumn = genresCursor.getColumnIndexOrThrow(
                                MediaStore.Audio.Genres.NAME);
                        while (genresCursor.moveToNext()) {
                            genre = genresCursor.getString(genresColumn);
                        }
                        genresCursor.close();
                    }
                }

                Uri albumUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId);
                Bitmap albumCover = null;

                if (IS_ANDROID_Q) {
                    try {
                        albumCover = context.getContentResolver().loadThumbnail(albumUri
                                , new Size(ALBUM_ART_WIDTH, ALBUM_ART_HEIGHT), null);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }

                musicList.add(new Music(musicUri, title, album, artist, null, genre, year, track
                        , albumCover));
            }

            cursor.close();
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
