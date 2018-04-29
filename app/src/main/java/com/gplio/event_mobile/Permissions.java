package com.gplio.event_mobile;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by goncalopalaio on 29/04/18.
 */

public class Permissions {
    public static int MY_PERMISSIONS_REQUEST = 333;

    public static void requestPermissions(Activity activity) {
        String[] applicationWidePermissions = {Manifest.permission.LOCATION_HARDWARE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        boolean granted = true;
        for (String applicationWidePermission : applicationWidePermissions) {
            granted &= ContextCompat.checkSelfPermission(activity, applicationWidePermission) == PackageManager.PERMISSION_GRANTED;
        }

        if (!granted) {
            ActivityCompat.requestPermissions(activity, applicationWidePermissions, MY_PERMISSIONS_REQUEST);
        }
    }

    public static boolean resultingPermissions(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != Permissions.MY_PERMISSIONS_REQUEST) {
            return false;
        }

        if (grantResults.length == 0) {
            return false;
        }

        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }
}
