package com.ldeveloper.permissionhelper;

import android.Manifest;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.permissionhelper.v1.PermissionHelper;
import com.permissionhelper.v2.Permission;

public class MainActivity extends AppCompatActivity {

    private PermissionHelper permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        PermissionHelper.Builder builder = new PermissionHelper.Builder(this)
//                .permissions(Manifest.permission.ACCESS_COARSE_LOCATION,
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE)
//                .requestCode(1000)
//                .setPermissionListener((PermissionHelper.RequestPermissionRationaleListener) permissionsNeedRationale -> {
//                    //All permissions that need rationale
//                    //Do Something
//                })
//                .setPermissionListener(new PermissionHelper.RequestPermissionListener() {
//                    @Override
//                    public void permissionsGranted() {
//                        //All permissions granted
//                        //Do Something
//                    }
//
//                    @Override
//                    public void permissionsDenied() {
//                        permissionHelper.check();
//                    }
//                });
//        permissionHelper = builder.build();
//        permissionHelper.check();

        Permission.with(this)
                .requestCode(1000)
                .permissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .permissionListener((Permission.RequestPermissionRationaleListener) permissionsNeedRationale -> {
                    Permission.showDeniedPermissionDialog(this, permissionsNeedRationale);
                })
                .permissionListener(new Permission.RequestPermissionListener() {
                    @Override
                    public void permissionsGranted() {
                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void permissionsDenied() {
                        Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        Permission.permit();
                    }
                });
        Permission.permit();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
