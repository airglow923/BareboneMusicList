package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;

public class AllMusicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_music_main);

        ListView listView = findViewById(R.id.list_all_music);

        // example music
        final ArrayList<Music> musics = new ArrayList<>();

        musics.add(new Music("新世紀のラブソング", "マジックディスク", "ASIAN KUNG-FU GENERATION"));
        musics.add(new Music("マジックディスク", "マジックディスク", "ASIAN KUNG-FU GENERATION"));
        musics.add(new Music("双子葉", "マジックディスク", "ASIAN KUNG-FU GENERATION"));
        musics.add(new Music("さよならロストジェネレイション", "マジックディスク", "ASIAN KUNG-FU GENERATION"));
        musics.add(new Music("迷子犬と雨のビート", "マジックディスク", "ASIAN KUNG-FU GENERATION"));
        musics.add(new Music("青空と黒い猫", "マジックディスク", "ASIAN KUNG-FU GENERATION"));
        musics.add(new Music("架空生物のブルース", "マジックディスク", "ASIAN KUNG-FU GENERATION"));
        musics.add(new Music("ラストダンスは悲しみを乗せて", "マジックディスク", "ASIAN KUNG-FU GENERATION"));
        musics.add(new Music("マイクロフォン", "マジックディスク", "ASIAN KUNG-FU GENERATION"));
        musics.add(new Music("ライジングサン", "マジックディスク", "ASIAN KUNG-FU GENERATION"));
        musics.add(new Music("イエス", "マジックディスク", "ASIAN KUNG-FU GENERATION"));
        musics.add(new Music("橙", "マジックディスク", "ASIAN KUNG-FU GENERATION"));
        musics.add(new Music("ソラニン", "マジックディスク", "ASIAN KUNG-FU GENERATION"));

        musics.add(new Music("FUNKASISTA", "Power Of Life", "BRADIO"));
        musics.add(new Music("Flyers", "Power Of Life", "BRADIO"));
        musics.add(new Music("真っ赤なカーチェイス", "Power Of Life", "BRADIO"));
        musics.add(new Music("Sunday (feat. Micro)", "Power Of Life", "BRADIO"));
        musics.add(new Music("Chocolate Flavor", "Power Of Life", "BRADIO"));
        musics.add(new Music("Flash Light Baby", "Power Of Life", "BRADIO"));
        musics.add(new Music("オトナHIT PARADE", "Power Of Life", "BRADIO"));
        musics.add(new Music("シークレットコード", "Power Of Life", "BRADIO"));
        musics.add(new Music("スパイシーマドンナ", "Power Of Life", "BRADIO"));
        musics.add(new Music("腰振る夜は君のせい", "Power Of Life", "BRADIO"));
        musics.add(new Music("You Make Me Feel Brand New", "Power Of Life", "BRADIO"));
        musics.add(new Music("Ride On Time (feat. 谷川正憲)", "Power Of Life", "BRADIO"));

        MusicAdapter itemsAdapter = new MusicAdapter(this, musics);
        listView.setAdapter(itemsAdapter);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent musicIntent = new Intent(AllMusicActivity.this, MusicPlayerActivity.class);
                musicIntent.putExtra("music", new Music(musics.get(position)));
                startActivity(musicIntent);
            }
        });
    }
}
