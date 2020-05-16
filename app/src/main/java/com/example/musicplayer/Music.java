package com.example.musicplayer;

import android.media.MediaMetadataRetriever;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

public class Music implements Comparable<Music>, Parcelable {

    // referenced id3v2 format
    private String title;
    private String album;
    private String artist;
    private String albumArtist;
    private String genre;
    private String year;
    private String track;
    private byte[] albumCover = new byte[0];

    public Music() {}

    public Music(Music other) {
        title = other.title;
        album = other.album;
        artist = other.artist;
        albumArtist = other.albumArtist;
        genre = other.genre;
        year = other.year;
        track = other.track;
        albumCover = other.albumCover;
    }

    public Music(String title, String album, String artist) {
        this.title = title;
        this.album = album;
        this.artist = artist;
    }

    public Music(String title, String album, String artist, String albumArtist,  String genre
            , String year, String track, byte[] albumCover, String albumCoverDir) {
        this(title, album, artist);
        this.albumArtist = albumArtist;
        this.genre = genre;
        this.year = year;
        this.track = track;
        this.albumCover = albumCover;
    }

    public Music(File file) {
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

        title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        albumArtist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
        genre = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
        year = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
        track = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
        albumCover = mmr.getEmbeddedPicture();
    }

    public Music(String path) {
        this(new File(path));
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
        out.writeString(genre);
        out.writeString(year);
        out.writeString(track);
        out.writeInt(albumCover.length);
        out.writeByteArray(albumCover);
    }

    private Music(Parcel in) {
        title = in.readString();
        album = in.readString();
        artist = in.readString();
        albumArtist = in.readString();
        genre = in.readString();
        year = in.readString();
        track = in.readString();
        albumCover = new byte[in.readInt()];
        in.readByteArray(albumCover);
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
