package com.kenjin.shareloc;

import android.support.v4.app.ActivityCompat;

import permissions.dispatcher.PermissionUtils;

/**
 * Created by kenjin on 23/01/18.
 */


final class PermisionDispacther {
    private static final int REQUEST_GETMYLOCATION = 0;

    private static final String[] PERMISSION_GETMYLOCATION = new String[] {"android.permission.ACCESS_FINE_LOCATION","android.permission.ACCESS_COARSE_LOCATION"};

    private static final int REQUEST_STARTLOCATIONUPDATES = 1;

    private static final String[] PERMISSION_STARTLOCATIONUPDATES = new String[] {"android.permission.ACCESS_FINE_LOCATION","android.permission.ACCESS_COARSE_LOCATION"};

    private PermisionDispacther() {
    }

    static void getMyLocationWithPermissionCheck(MainActivity target) {
        if (PermissionUtils.hasSelfPermissions(target, PERMISSION_GETMYLOCATION)) {
            target.getMyLocation();
        } else {
            ActivityCompat.requestPermissions(target, PERMISSION_GETMYLOCATION, REQUEST_GETMYLOCATION);
        }
    }

    static void startLocationUpdatesWithPermissionCheck(MainActivity target) {
        if (PermissionUtils.hasSelfPermissions(target, PERMISSION_STARTLOCATIONUPDATES)) {
            target.startLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(target, PERMISSION_STARTLOCATIONUPDATES, REQUEST_STARTLOCATIONUPDATES);
        }
    }

    static void onRequestPermissionsResult(MainActivity target, int requestCode,
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_GETMYLOCATION:
                if (PermissionUtils.verifyPermissions(grantResults)) {
                    target.getMyLocation();
                }
                break;
            case REQUEST_STARTLOCATIONUPDATES:
                if (PermissionUtils.verifyPermissions(grantResults)) {
                    target.startLocationUpdates();
                }
                break;
            default:
                break;
        }
    }
}

