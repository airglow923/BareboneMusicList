package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.Map;

import static com.example.musicplayer.PermissionControl.*;
import static com.example.musicplayer.MusicLoader.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionControl permissionControl = new PermissionControl();
        checkAndRequestPermissions(MainActivity.this);

        if (checkPermissions(this).isEmpty()) {
            loadMusicFromFolder(this);
        }

        ViewPager viewPager = findViewById(R.id.viewpager);
        PagerAdapter pagerAdapter = new MusicPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL: {
                Map<String, Integer> permissionResults = new HashMap<>();

                for (int i = 0; i < permissions.length; ++i) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        permissionResults.put(permissions[i], grantResults[i]);
                    }
                }

                if (!permissionResults.isEmpty()) {
                    for (Map.Entry<String, Integer> entry : permissionResults.entrySet()) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this
                                , entry.getKey())) {
                            showMessageOkCancel(MainActivity.this
                                    , permissionRationale.get(entry.getKey())
                                    , new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ActivityCompat.requestPermissions(MainActivity.this
                                                    , new String[]{entry.getKey()}, requestCode);
                                        }
                                    });
                        } else {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setMessage(DENIED_PERMISSION_MESSAGE)
                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                    }
                } else {
                }
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
