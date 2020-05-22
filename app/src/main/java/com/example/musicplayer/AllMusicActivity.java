package com.example.musicplayer;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.musicplayer.MusicLoader.musicList;

public class AllMusicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_music_main);

        RecyclerView recyclerView = findViewById(R.id.list_all_music);
        recyclerView.setAdapter(new MusicAdapter(this, musicList));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
