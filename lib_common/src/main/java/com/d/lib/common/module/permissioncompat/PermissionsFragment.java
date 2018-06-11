package com.d.lib.common.module.permissioncompat;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.d.lib.common.module.permissioncompat.callback.PermissionCallback;
import com.d.lib.common.module.permissioncompat.callback.PublishCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PermissionsFragment extends Fragment {

    protected static final int PERMISSIONS_REQUEST_CODE = 42;

    protected Context mContext;
    // Contains all the current permission requests.
    // Once granted or denied, they are removed from it.
    protected Map<String, PublishCallback<Permission>> mSubjects = new HashMap<>();
    protected PermissionCallback<List<Permission>> callback;

    public PermissionsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mContext = getActivity().getApplicationContext();
    }

    @TargetApi(Build.VERSION_CODES.M)
    void requestPermissions(@NonNull String[] permissions, PermissionCallback<List<Permission>> callback) {
        this.callback = callback;
        requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != PERMISSIONS_REQUEST_CODE) {
            return;
        }

        boolean[] shouldShowRequestPermissionRationale = new boolean[permissions.length];

        for (int i = 0; i < permissions.length; i++) {
            shouldShowRequestPermissionRationale[i] = shouldShowRequestPermissionRationale(permissions[i]);
        }

        onRequestPermissionsResult(permissions, grantResults, shouldShowRequestPermissionRationale);
    }

    protected void onRequestPermissionsResult(String permissions[], int[] grantResults, boolean[] shouldShowRequestPermissionRationale) {
        List<Permission> list = new ArrayList<Permission>();
        for (int i = 0, size = permissions.length; i < size; i++) {
            Log.d(PermissionCompat.TAG, "onRequestPermissionsResult  " + permissions[i]);
            // Find the corresponding subject
            PublishCallback<Permission> subject = mSubjects.get(permissions[i]);
            if (subject == null) {
                // No subject found
                Log.e(PermissionCompat.TAG, "PermissionCompat.onRequestPermissionsResult invoked but didn't find the corresponding permission request.");
                if (callback != null) {
                    callback.onError(new Exception("PermissionCompat.onRequestPermissionsResult invoked but didn't find the corresponding permission request."));
                }
                return;
            }
            mSubjects.remove(permissions[i]);
            boolean granted = isGranted(permissions[i], grantResults[i], shouldShowRequestPermissionRationale[i]);
            list.add(new Permission(permissions[i], granted, shouldShowRequestPermissionRationale[i]));
        }
        if (callback != null) {
            callback.onNext(list);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected boolean isGranted(String permission, int grantResult, boolean shouldShowRequestPermissionRationale) {
        return grantResult == PackageManager.PERMISSION_GRANTED;
    }

    public PublishCallback<Permission> getSubjectByPermission(@NonNull String permission) {
        return mSubjects.get(permission);
    }

    public boolean containsByPermission(@NonNull String permission) {
        return mSubjects.containsKey(permission);
    }

    public PublishCallback<Permission> setSubjectForPermission(@NonNull String permission, @NonNull PublishCallback<Permission> subject) {
        return mSubjects.put(permission, subject);
    }

    protected boolean isFinish() {
        return getActivity() == null || getActivity().isFinishing();
    }
}
