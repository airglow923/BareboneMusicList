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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.musicplayer.AndroidVersion.*;

final class PermissionControl {

    static final int PERMISSION_ALL = 1234;
    static final String DENIED_PERMISSION_MESSAGE =
            "You disallowed permission for this feature.\n"
                    + "To allows it, go to [Setting] > [Apps & notifications] > [App permissions]";
    static Map<String, String> permissionRationale = new HashMap<String, String>();
    private static List<String> permissionsNeeded = new ArrayList<>();

    PermissionControl() {
        init();
    }

    static void checkAndRequestPermissions(Activity activity) {
        List<String> permissionsNeeded = checkPermissions(activity.getApplicationContext());

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionsNeeded.toArray(new String[0])
                    , PERMISSION_ALL);
        }
    }

    static @NonNull List<String> checkPermissions(Context context) {
        List<String> notGranted = new ArrayList<>();

        for (String permission : permissionsNeeded) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                notGranted.add(permission);
            }
        }

        return notGranted;
    }

    static void showMessageOkCancel(@NonNull final Activity activity, final String message
            , final DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", listener)
                .create()
                .show();
    }

    private void init() {
        permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        permissionRationale.put(permissionsNeeded.get(0)
                , "To update and delete music, allow access to external storage.");
        permissionRationale.put(permissionsNeeded.get(1)
                , "To read and play music, allow access to external storage.");

        if (IS_ANDROID_Q) {
            permissionsNeeded.add(Manifest.permission.ACCESS_MEDIA_LOCATION);
            permissionRationale.put(
                    permissionsNeeded.get(2)
                    , "To read or edit media metadata, allow access to media inside internal storage.");
        }
    }
}
