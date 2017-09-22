package com.lynn.library.permission;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class PermissionsActivity extends AppCompatActivity implements OnRequestPermissionsResultCallback {
    private final ArrayList<String> list = new ArrayList();
    private final ArrayList<String> deniedList = new ArrayList();
    private int permissionType;
    private final int REQUEST_CODE_REQUEST_PERMISSION = 0x9901;

    protected final void askPermission(@NonNull String... permissions) {
        this.askPermission(0, permissions);
    }

    protected final void askPermission(int type, @NonNull String... permissions) {
        this.permissionType = type;
        this.filterPermission(permissions);
        if (this.list != null && !this.list.isEmpty()) {
            String[] array = list.toArray(new String[list.size()]);
            ActivityCompat.requestPermissions(this, array, this.REQUEST_CODE_REQUEST_PERMISSION);
        } else {
            this.onPermissionGranted(this.permissionType);
        }
    }

    private final void filterPermission(String... permissions) {
        this.list.clear();
        this.deniedList.clear();
        for (int i = 0; i < permissions.length; i++) {
            String p = permissions[i];
            if (!hasPermissions(p)) {
                this.list.add(p);
            }
        }
    }

    protected void onPermissionGranted(int type) {
    }

    protected void onPermissionDenied(int type, ArrayList<String> permissions) {
    }

    protected boolean dealSelf() {
        return false;
    }

    protected final boolean hasPermissions(@NonNull String... permissions) {
        boolean hasPermission = true;
        for (int i = 0; i < permissions.length; i++) {
            hasPermission = ContextCompat.checkSelfPermission(this, permissions[i]) == PackageManager.PERMISSION_GRANTED;
            if (!hasPermission) {
                return hasPermission;
            }
        }
        return hasPermission;
    }

    private final void startSettings() {
        Intent i;
        if (this.deniedList.contains(Manifest.permission.BIND_ACCESSIBILITY_SERVICE)) {
            i = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            this.startActivity(i);
            if (deniedList.size() == 1) {
                return;
            }
        }
        i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.setData(Uri.fromParts("package", this.getPackageName(), null));
        this.startActivity(i);
    }

    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_REQUEST_PERMISSION) {
            if (permissions.length != grantResults.length) return;
            for (int i = 0; i < permissions.length; i++) {
                String p = permissions[i];
                int grants = grantResults[i];
                list.remove(p);
                if (grants == PackageManager.PERMISSION_DENIED) {
                    deniedList.add(p);
                }
            }
            if (null == list || list.size() == 0) {
                if (deniedList == null || deniedList.size() == 0) {
                    onPermissionGranted(permissionType);
                } else {
                    if (dealSelf()) {
                        onPermissionDenied(permissionType, deniedList);
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("权限缺失")
                                .setMessage("部分权限未开启，请开启权限后再试")
                                .setNegativeButton("取消", new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveButton("设置", new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        startSettings();
                                    }
                                })
                                .create().show();
                    }
                }
            }
        }
    }
}
