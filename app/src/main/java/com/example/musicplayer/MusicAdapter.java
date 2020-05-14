package com.example.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private Context context;
    private List<Music> musics;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Music> getMusics() {
        return musics;
    }

    public void setMusics(List<Music> musics) {
        this.musics = musics;
    }

    public MusicAdapter(Context context, List<Music> musics) {
        this.context = context;
        this.musics = musics;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.music_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Music music = musics.get(position);

//        if (!music.getAlbumCoverDir().isEmpty()) {
//            holder.albumCoverImageView.setImageResource(context.getResources().getIdentifier(
//                    stem(music.getAlbumCoverDir()), "drawable", context.getPackageName()));
//        } else {
//            holder.albumCoverImageView.setImageResource(R.drawable.default_album_cover);
//        }

        holder.albumCoverImageView.setImageResource(R.drawable.default_album_cover);
        holder.titleTextView.setText(music.getTitle());
        holder.artistAlbumTextView.setText(music.getArtist() + " - " + music.getAlbum());
        holder.parentView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent musicIntent = new Intent(context, MusicPlayerActivity.class);
                musicIntent.putExtra("music", music);
                context.startActivity(musicIntent);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return musics.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView albumCoverImageView;
        private TextView titleTextView;
        private TextView artistAlbumTextView;
        private View parentView;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.albumCoverImageView = view.findViewById(R.id.image_album_cover);
            this.titleTextView = view.findViewById(R.id.text_title);
            this.artistAlbumTextView = view.findViewById(R.id.text_artist_album);
            this.parentView = view;
        }
    }

    private static String stem(String filename) {
        return filename.substring(0, filename.lastIndexOf('.'));
    }
}
