package com.example.yousefebrahimzadeh.tv_fernbedienung;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

public class StoragePermission {

    private static final int REQUEST_ID_READ_PERMISSION = 100;
    private static final int REQUEST_ID_WRITE_PERMISSION = 200;
    private boolean writeOk = false;
    private boolean readOk = false;

    public void askPermissionAndWriteFile(MainActivity mainActivity) {
        boolean canWrite = askPermission(REQUEST_ID_WRITE_PERMISSION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, mainActivity);
        //
        if (canWrite) {
            writeOk = true;
        }
        else
        {
            writeOk = false;
        }
    }
    public void askPermissionAndReadFile(MainActivity mainActivity) {
        boolean canRead = askPermission(REQUEST_ID_READ_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE, mainActivity);
        //
        if (canRead) {
            readOk = true;
        }
        else
        {
            readOk = false;
        }
    }
    public boolean askPermission(int requestId, String permissionName, MainActivity mainActivity) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            // Check if we have permission
            int permission = ActivityCompat.checkSelfPermission(mainActivity, permissionName);


            if (permission != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                mainActivity.requestPermissions(
                        new String[]{permissionName},
                        requestId
                );
                return false;
            }
        }
        return true;
    }

    public boolean getWritePermission()
    {
        return writeOk;
    }
    public boolean getReadPermission()
    {
        return readOk;
    }
}