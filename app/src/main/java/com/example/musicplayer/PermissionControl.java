package com.example.musicplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void processMultiplePermission(@NonNull final Activity activity
            , @NonNull final List<Triplet<String, String, Integer>> permissions) {
        for (Triplet<String, String, Integer> permission : permissions) {
            final String name = permission.getValue0();
            final String rationale = permission.getValue1();
            final int requestCode = permission.getValue2();

            if (!hasPermissions(activity, name)) {
                processPermission(activity, name, rationale, requestCode);
            }
        }
    }

    public static void processPermission(@NonNull final Activity activity
            , final String permissionName, final String permissionRationale
            , final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionName)) {
            ActivityCompat.requestPermissions(activity, new String[]{permissionRationale}
                    , requestCode);
        } else {
            showMessageOkCancel(activity, permissionRationale
                    , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(activity, new String[]{permissionName}
                            , requestCode);
                }
            });
        }
    }

    private static void showMessageOkCancel(@NonNull final Activity activity, final String message
            , final DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", listener)
                .create()
                .show();
    }
}
