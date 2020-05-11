package com.example.musicplayer;

public class Music implements Comparable<Music>{
    // referenced id3v2 format
    private String title;
    private String album;
    private String artist;
    private String albumArtist;
    private String comment;
    private String genre;
    private int year;
    private int track;
    private int discNumber;
    private int albumCover;

    public Music(String title, String album, String artist) {
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.albumCover = R.drawable.default_album_cover;
    }

    public Music(String title, String album, String artist, String albumArtist, String comment,
        String genre, int year, int track, int discNumber, int albumCover) {
        this(title, album, artist);
        this.albumArtist = albumArtist;
        this.comment = comment;
        this.genre = genre;
        this.year = year;
        this.track = track;
        this.discNumber = discNumber;
        this.albumCover = albumCover;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getTrack() {
        return track;
    }

    public void setTrack(int track) {
        this.track = track;
    }

    public int getDiscNumber() {
        return discNumber;
    }

    public void setDiscNumber(int discNumber) {
        this.discNumber = discNumber;
    }

    public int getAlbumCover() {
        return albumCover;
    }

    public void setAlbumCover(int albumCover) {
        this.albumCover = albumCover;
    }

    @Override
    public int compareTo(Music other) {
        return this.title.compareTo(other.title);
    }
}
