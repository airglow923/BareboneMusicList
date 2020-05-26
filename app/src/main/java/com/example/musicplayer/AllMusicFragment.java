package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.example.musicplayer.MusicLoader.musicList;

public class AllMusicFragment extends Fragment {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.all_music_main);
//
//        RecyclerView recyclerView = findViewById(R.id.list_all_music);
//        recyclerView.setAdapter(new MusicAdapter(this, musicList));
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.all_music_main, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.list_all_music);
        recyclerView.setAdapter(new MusicAdapter(getContext(), musicList));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return rootView;
    }
}
