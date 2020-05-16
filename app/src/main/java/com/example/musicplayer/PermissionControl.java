package com.example.musicplayer;

import android.Manifest;

import org.javatuples.Triplet;

import java.util.Arrays;
import java.util.List;

public class PermissionControl {
    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 2;
    public static final int PERMISSION_READ_EXTERNAL_STORAGE = 4;
    public static final int PERMISSION_ALL =
            PERMISSION_READ_EXTERNAL_STORAGE | PERMISSION_WRITE_EXTERNAL_STORAGE;

    public static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final List<Triplet<String, String, Integer>> PERMISSION_RATIONALE =
            Arrays.asList(
                    Triplet.with(
                            PERMISSIONS[0]
                            , "In order to read and play music, you need to allow access to file."
                            , PERMISSION_READ_EXTERNAL_STORAGE),
                    Triplet.with(
                            PERMISSIONS[1]
                            , "In order to update and delete music, you need to allow access to file."
                            , PERMISSION_WRITE_EXTERNAL_STORAGE)
            );
}
