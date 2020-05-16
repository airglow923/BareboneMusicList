package com.example.musicplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v1Genres;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

public class Music implements Comparable<Music>, Parcelable {

    // referenced id3v2 format
    private String title;
    private String album;
    private String artist;
    private String albumArtist;
    private String comment;
    private String genre;
    private String year;
    private String track;
    private byte[] albumCover = new byte[0];
    private String albumCoverDir;

    public Music() {}

    public Music(Music other) {
        title = other.title;
        album = other.album;
        artist = other.artist;
        albumArtist = other.albumArtist;
        comment = other.comment;
        genre = other.genre;
        year = other.year;
        track = other.track;
        albumCover = other.albumCover;
        albumCoverDir = other.albumCoverDir;
    }

    public Music(String title, String album, String artist) {
        this.title = title;
        this.album = album;
        this.artist = artist;
    }

    public Music(String title, String album, String artist, String albumArtist, String comment,
        String genre, String year, String track, byte[] albumCover, String albumCoverDir) {
        this(title, album, artist);
        this.albumArtist = albumArtist;
        this.comment = comment;
        this.genre = genre;
        this.year = year;
        this.track = track;
        this.albumCover = albumCover;
        this.albumCoverDir = albumCoverDir;
    }

    public Music(File file, Integer placeholder) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(file.getPath());

        try {
            String hasAudio = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO);
            if (hasAudio == null) {
                throw new NotAudioException("Metadata does not contain audio information.");
            }
        } catch (NotAudioException e) {
            System.out.println(e.getMessage());
        }
    }

    public Music(File dir) {
        Mp3File mp3File = null;

        try {
            mp3File = new Mp3File(dir);
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            System.out.println(e.getMessage());
        }

        if (mp3File.hasId3v1Tag()) {
            ID3v1 id3v1 = mp3File.getId3v1Tag();

            title = id3v1.getTitle();
            album = id3v1.getAlbum();
            artist = id3v1.getArtist();
            comment = id3v1.getComment();
            genre = ID3v1Genres.GENRES[id3v1.getGenre()];
            year = id3v1.getYear();
            track = id3v1.getTrack();
        } else if (mp3File.hasId3v2Tag()) {
            ID3v2 id3v2 = mp3File.getId3v2Tag();

            title = id3v2.getTitle();
            album = id3v2.getAlbum();
            artist = id3v2.getArtist();
            albumArtist = id3v2.getAlbumArtist();
            comment = id3v2.getComment();
            genre = ID3v1Genres.GENRES[id3v2.getGenre()];
            year = id3v2.getYear();
            track = id3v2.getTrack();
            albumCover = id3v2.getAlbumImage();
            albumCoverDir = saveImage(albumCover, DRAWABLE_DIR);
        }
    }

    public Music(String filename) {
        this(new File(filename));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public byte[] getAlbumCover() {
        return albumCover;
    }

    public void setAlbumCover(byte[] albumCover) {
        this.albumCover = albumCover;
    }

    public String getAlbumCoverDir() {
        return albumCoverDir;
    }

    public void setAlbumCoverDir(String albumCoverDir) {
        this.albumCoverDir = albumCoverDir;
    }

    private static byte[] bmpToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private static Bitmap byteArrayToBmp(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    private static String saveImage(byte[] data, File dir) {
//        File file = null;
//        FileOutputStream fos = null;
//
//        if (data.length != 0 || !file.exists()) {
//            file = new File(dir, data.hashCode() + ".png");
//            try {
//                fos = new FileOutputStream(file);
//                byteArrayToBmp(data).compress(Bitmap.CompressFormat.PNG, 100, fos);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        } else {
//            file = new File("");
//        }
//
//        return file.getPath();
        File file = new File("");
        return file.getPath();
    }

    @Override
    public int compareTo(Music other) {
        return this.title.compareTo(other.title);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(album);
        out.writeString(artist);
        out.writeString(albumArtist);
        out.writeString(comment);
        out.writeString(genre);
        out.writeString(year);
        out.writeString(track);
        out.writeInt(albumCover.length);
        out.writeByteArray(albumCover);
        out.writeString(albumCoverDir);
    }

    private Music(Parcel in) {
        title = in.readString();
        album = in.readString();
        artist = in.readString();
        albumArtist = in.readString();
        comment = in.readString();
        genre = in.readString();
        year = in.readString();
        track = in.readString();
        albumCover = new byte[in.readInt()];
        in.readByteArray(albumCover);
        albumCoverDir = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Music> CREATOR
            = new Parcelable.Creator<Music>() {

        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };
}
