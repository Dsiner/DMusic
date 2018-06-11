package com.d.lib.common.module.permissioncompat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v4.app.AppOpsManagerCompat;
import android.support.v4.content.PermissionChecker;

import com.d.lib.common.module.permissioncompat.support.ManufacturerSupport;
import com.d.lib.common.module.permissioncompat.support.lollipop.PermissionsChecker;
import com.d.lib.common.module.permissioncompat.support.xiaomi.PermissionsFragmentXiaomi;

import java.util.ArrayList;
import java.util.List;

/**
 * PermissionXiaomi
 * Created by D on 2018/4/26.
 */
public class PermissionXiaomi extends PermissionCompat {

    PermissionXiaomi(@NonNull Activity activity) {
        super(activity);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected PermissionsFragment getPermissionsFragment(Activity activity) {
        PermissionsFragment permissionsFragment = findPermissionsFragment(activity);
        boolean isNewInstance = permissionsFragment == null;
        if (isNewInstance) {
            permissionsFragment = new PermissionsFragmentXiaomi();
            FragmentManager fragmentManager = activity.getFragmentManager();
            fragmentManager.beginTransaction()
                    .add(permissionsFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return permissionsFragment;
    }

    @Deprecated
    protected Permission combinePermissionXiaomi(List<Permission> permissions) {
        if (ManufacturerSupport.isXiaomiSpecial()) {
            List<Permission> list = new ArrayList<>();
            for (Permission p : permissions) {
                if (!isFinish() && p.granted && !PermissionsChecker.requestPermissions(mContext, p.name)) {
                    list.add(p);
                }
            }
            if (list.size() > 0) {
                permissions.removeAll(list);
                for (Permission p : list) {
                    permissions.add(new Permission(p.name, false, false));
                }
            }
        }
        return new Permission(permissions);
    }

    @Override
    public boolean isGranted(String permission) {
        if (ManufacturerSupport.isXiaomiSpecial()) {
            return isFinish() || hasSelfPermissionForXiaomiOS(mContext, permission)
                    && PermissionsChecker.requestPermissions(mContext, permission);
        }
        return hasSelfPermissionForXiaomiOS(mContext, permission);
    }

    public static boolean hasSelfPermissionForXiaomi(Context context, String permission) {
        if (ManufacturerSupport.isXiaomiSpecial()) {
            return PermissionsChecker.isPermissionGranted(permission);
        }
        return hasSelfPermissionForXiaomiOS(context, permission);
    }

    public static boolean hasSelfPermissionForXiaomiOS(Context context, String permission) {
        context = context.getApplicationContext();
        String permissionToOp = AppOpsManagerCompat.permissionToOp(permission);
        if (permissionToOp == null) {
            // in case of normal permissions(e.g. INTERNET)
            return true;
        }
        int noteOp = AppOpsManagerCompat.noteOp(context, permissionToOp, Process.myUid(), context.getPackageName());
        try {
            return noteOp == AppOpsManagerCompat.MODE_ALLOWED
                    && PermissionChecker.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        } catch (RuntimeException t) {
            return false;
        }
    }
}
