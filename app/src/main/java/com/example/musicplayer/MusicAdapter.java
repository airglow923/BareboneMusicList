package com.example.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MusicAdapter extends ArrayAdapter<Music> {

    private MusicPlayerActivity musicPlayerActivity;

    public MusicAdapter(Context context, ArrayList<Music> musics) {
        super(context, 0, musics);
    }

    public MusicAdapter(MusicPlayerActivity musicPlayerActivity) {
        this.musicPlayerActivity = musicPlayerActivity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.music_list_item, parent, false);
        }

        Music music = getItem(position);
        musicPlayerActivity.setMusic(music);

        ImageView albumCover = listItemView.findViewById(R.id.image_album_cover);
        albumCover.setImageResource(music.getAlbumCover() == 0
                ? R.drawable.default_album_cover : music.getAlbumCover());
        TextView title = listItemView.findViewById(R.id.text_title);
        title.setText(music.getTitle());
        TextView artistAlbum = listItemView.findViewById(R.id.text_artist_album);
        artistAlbum.setText(music.getArtist() + " - " + music.getAlbum());

        return listItemView;
    }
}
