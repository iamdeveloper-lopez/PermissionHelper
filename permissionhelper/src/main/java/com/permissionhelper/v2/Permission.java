package com.permissionhelper.v2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.permissionhelper.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Permission {

    private static final String TAG = Permission.class.getSimpleName();

    private static Permission instance = null;

    private static WeakReference<Activity> activityWeakReference;
    private static WeakReference<Fragment> fragmentWeakReference;

    private static PermisionHolder permisionHolder;
    private static String[] permissions;
    private static RequestPermissionRationaleListener requestPermissionRationaleListener;
    private static RequestPermissionGrantedListener requestPermissionGrantedListener;
    private static RequestPermissionDeniedListener requestPermissionDeniedListener;
    private static RequestPermissionListener requestPermissionListener;
    private static int requestCode;
    private static boolean retry = false;
    private static boolean permitting;

    private enum PermisionHolder {
        ACTIVITY,
        FRAGMENT
    }

    private Permission(Activity activity) {
        activityWeakReference = new WeakReference<>(activity);
        permisionHolder = PermisionHolder.ACTIVITY;
    }

    private Permission(Fragment fragment) {
        fragmentWeakReference = new WeakReference<>(fragment);
        permisionHolder = PermisionHolder.FRAGMENT;
    }

    public static Permission with(Activity activity) {
        if (instance == null) {
            instance = new Permission(activity);
        }
        return instance;
    }

    public static Permission with(Fragment fragment) {
        if (instance == null) {
            instance = new Permission(fragment);
        }
        return instance;
    }

    public static void exit() {
        instance = null;
    }

    public Permission retry() {
        if (instance == null)
            throw new IllegalStateException("Please call Permission.with(Context)");
        Permission.retry = true;
        return instance;
    }

    public Permission permissions(@NonNull String... permissions) {
        if (instance == null)
            throw new IllegalStateException("Please call Permission.with(Context)");
        Permission.permissions = permissions;
        return instance;
    }

    public Permission permissionListener(
            RequestPermissionRationaleListener requestPermissionRationaleListener) {
        if (instance == null)
            throw new IllegalStateException("Please call Permission.with(Context)");
        Permission.requestPermissionRationaleListener = requestPermissionRationaleListener;
        return instance;
    }

    public Permission permissionListener(
            RequestPermissionGrantedListener requestPermissionGrantedListener) {
        if (instance == null)
            throw new IllegalStateException("Please call Permission.with(Context)");
        Permission.requestPermissionGrantedListener = requestPermissionGrantedListener;
        return instance;
    }

    public Permission permissionListener(
            RequestPermissionDeniedListener requestPermissionDeniedListener) {
        if (instance == null)
            throw new IllegalStateException("Please call Permission.with(Context)");
        Permission.requestPermissionDeniedListener = requestPermissionDeniedListener;
        return instance;
    }

    public Permission permissionListener(
            RequestPermissionListener requestPermissionListener) {
        if (instance == null)
            throw new IllegalStateException("Please call Permission.with(Context)");
        Permission.requestPermissionListener = requestPermissionListener;
        return instance;
    }

    public Permission requestCode(int requestCode) {
        if (instance == null)
            throw new IllegalStateException("Please call Permission.with(Context)");
        Permission.requestCode = requestCode;
        return instance;
    }

    //Checker Permission

    private static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null
                && permissions != null) {
            for (String permission : permissions) {
                Log.d(TAG, "hasPermissions: " + permission);
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (instance == null)
            throw new IllegalStateException("Please call Permission.with(Context)");
        if (Permission.requestCode == requestCode) {
            permitting = false;
            List<String> permissionsGranted = new ArrayList<>();
            List<String> permissionsDenied = new ArrayList<>();
            int index = 0;
            for (String permission : permissions) {
                if (grantResults[index] == 0) {
                    permissionsGranted.add(permission);
                } else {
                    permissionsDenied.add(permission);
                }
                index++;
            }
            if (requestPermissionDeniedListener != null && permissionsDenied.size() > 0) {
                requestPermissionDeniedListener.hasDeniedPermission(permissionsDenied);
            }

            if (requestPermissionGrantedListener != null && permissionsGranted.size() > 0) {
                requestPermissionGrantedListener.hasPermission(permissionsGranted);
            }

            if (requestPermissionListener != null) {
                if (permissionsGranted.size() == permissions.length) {
                    requestPermissionListener.permissionsGranted();
                } else {
                    if (!retry)
                        requestPermissionListener.permissionsDenied();
                }
            }
            if (retry)
                permit();
        }
    }

    private static String getPermissionNames(List<String> permissions) {
        StringBuilder permissionNames = new StringBuilder();
        Context context = permisionHolder == PermisionHolder.ACTIVITY ? activityWeakReference.get() : fragmentWeakReference.get().getActivity();
        try {
            PackageManager packageManager = context.getPackageManager();
            for (String permission : permissions) {
                try {
                    PermissionInfo permissionInfo = packageManager.getPermissionInfo(permission, 0);
                    PermissionGroupInfo permissionGroupInfo = packageManager.getPermissionGroupInfo(permissionInfo.group, 0);
                    String permissionName = permissionGroupInfo.loadLabel(packageManager).toString();
                    if (permissionNames.length() > 0) {
                        if (!permissionNames.toString().contains(permissionName)) {
                            permissionNames.append("\n");
                            permissionNames.append("• ");
                            permissionNames.append(permissionName);
                        }
                    } else {
                        permissionNames.append("• ");
                        permissionNames.append(permissionGroupInfo.loadLabel(packageManager));
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "getPermissionNames()", ex);
        }
        return permissionNames.toString();
    }

    public void permit(RequestPermissionRationaleListener requestPermissionRationaleListener) {
        Permission.requestPermissionRationaleListener = requestPermissionRationaleListener;
        permit();
    }

    public void permit(RequestPermissionGrantedListener requestPermissionGrantedListener) {
        Permission.requestPermissionGrantedListener = requestPermissionGrantedListener;
        permit();
    }

    public void permit(RequestPermissionDeniedListener requestPermissionDeniedListener) {
        Permission.requestPermissionDeniedListener = requestPermissionDeniedListener;
        permit();
    }

    public void permit(RequestPermissionListener requestPermissionListener) {
        Permission.requestPermissionListener = requestPermissionListener;
        permit();
    }

    public static void permit() {
        if (instance == null)
            throw new IllegalStateException("Please call Permission.with(Context)");
        if (permitting) {
            Log.e(TAG, "Cannot call permit() currently running");
            return;
        }
        permitting = true;
        if (!hasPermissions(permisionHolder.equals(PermisionHolder.ACTIVITY) ? activityWeakReference.get() : fragmentWeakReference.get().getActivity(), permissions)) {
            List<String> permissionNeedRationale = new ArrayList<>();
            for (String permission : permissions) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (permisionHolder.equals(PermisionHolder.ACTIVITY)) {
                        if (activityWeakReference.get().shouldShowRequestPermissionRationale(permission)) {
                            permissionNeedRationale.add(permission);
                        }
                    } else {
                        if (fragmentWeakReference.get().shouldShowRequestPermissionRationale(permission)) {
                            permissionNeedRationale.add(permission);
                        }
                    }
                }
            }
            if (requestPermissionRationaleListener != null && permissionNeedRationale.size() > 0) {
                requestPermissionRationaleListener.shouldShowRequestPermissionRationale(getPermissionNames(permissionNeedRationale));
            } else {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (permisionHolder.equals(PermisionHolder.ACTIVITY)) {
                        activityWeakReference.get().requestPermissions(permissions, requestCode);
                    } else {
                        fragmentWeakReference.get().requestPermissions(permissions, requestCode);
                    }
                } else {
                    if (requestPermissionListener != null) {
                        requestPermissionListener.permissionsGranted();
                    }
                    if (requestPermissionGrantedListener != null) {
                        requestPermissionGrantedListener.hasPermission(Arrays.asList(permissions));
                    }
                }
            }
        } else {
            if (requestPermissionListener != null) {
                requestPermissionListener.permissionsGranted();
            }
            if (requestPermissionGrantedListener != null) {
                requestPermissionGrantedListener.hasPermission(Arrays.asList(permissions));
            }
        }
    }

    public static void showDeniedPermissionDialog(Activity context, String permissionName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("Permission Required")
                .setMessage(String.format("Allow %s to access to the following permissions: \n\n%s \n\nby tapping Settings > Permissions.", context.getResources().getString(R.string.app_name), permissionName))
                .setPositiveButton("SETTINGS", (dialogInterface, i) -> {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                })
                .create()
                .show();
    }

// Permission Listeners

    public interface RequestPermissionRationaleListener {

        void shouldShowRequestPermissionRationale(String permissionsNeedRationale);
    }

    public interface RequestPermissionGrantedListener {

        void hasPermission(List<String> permissionsGranted);
    }

    public interface RequestPermissionDeniedListener {

        void hasDeniedPermission(List<String> permissionsDenied);
    }

    public interface RequestPermissionListener {

        void permissionsGranted();

        void permissionsDenied();
    }

}
