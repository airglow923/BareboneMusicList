package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import static com.example.musicplayer.MusicLoader.musicList;

public class AllMusicFragment extends Fragment {

    static final String TAG = AllMusicFragment.class.getSimpleName();
    static final String LOG_TAG = AllMusicFragment.class.getSimpleName();

    private OnClickListener onClickListener = v -> {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    };

    public AllMusicFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.all_music_main, container, false);

//        TextView textView = rootView.findViewById(R.id.text_all_music);
//        textView.setOnClickListener(onClickListener);

        RecyclerView recyclerView = rootView.findViewById(R.id.list_all_music);
        recyclerView.setAdapter(new MusicAdapter(getContext(), musicList));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return rootView;
    }
}
