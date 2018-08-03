package com.ldeveloper.permissionhelper;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.permissionhelper.PermissionHelper;

public class MainActivity extends AppCompatActivity {

    private PermissionHelper permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionHelper.Builder builder = new PermissionHelper.Builder(this)
                .addPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .requestCode(1000)
                .setPermissionListener((PermissionHelper.RequestPermissionRationaleListener) permissionsNeedRationale -> {
                    //All permissions that need rationale
                    //Do Something
                })
                .setPermissionListener(new PermissionHelper.RequestPermissionListener() {
                    @Override
                    public void permissionsGranted() {
                        //All permissions granted
                        //Do Something
                    }

                    @Override
                    public void permissionsDenied() {
                        permissionHelper.check();
                    }
                });
        permissionHelper = builder.build();
        permissionHelper.check();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
