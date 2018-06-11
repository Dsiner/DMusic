package com.d.lib.common.module.permissioncompat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.d.lib.common.module.permissioncompat.callback.PublishCallback;
import com.d.lib.common.module.permissioncompat.support.lollipop.PermissionsChecker;

import java.util.ArrayList;
import java.util.List;

/**
 * PermissionLollipop
 * Created by D on 2018/4/26.
 */
public class PermissionLollipop extends PermissionCompat {

    PermissionLollipop(@NonNull Activity activity) {
        super(activity);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void requestImplementation(final String... permissions) {
        final List<Permission> ps = new ArrayList<>();
        final List<PublishCallback<Permission>> publishs = new ArrayList<>(permissions.length);
        final List<String> unrequestedPermissions = new ArrayList<>();

        // In case of multiple permissions, we create an Observable for each of them.
        // At the end, the observables are combined to have a unique response.
        for (String permission : permissions) {
            Log.d(PermissionCompat.TAG, "Requesting permission " + permission);
            if (isGranted(permission)) {
                Permission p = new Permission(permission, true, false);
                ps.add(p);
                publishs.add(PublishCallback.create(p));
                continue;
            }
            ps.add(new Permission(permission, false, true));
            unrequestedPermissions.add(permission);
            PublishCallback<Permission> subject = PublishCallback.create();
            publishs.add(subject);
        }
        if (!unrequestedPermissions.isEmpty()) {
            String[] unrequestedPermissionsArray = unrequestedPermissions.toArray(new String[unrequestedPermissions.size()]);
            Log.d(PermissionCompat.TAG, "deny permissions " + TextUtils.join(", ", unrequestedPermissionsArray));
        }
        final Permission result = combinePermission(ps);
        PermissionSchedulers.switchThread(observeOnScheduler, new Runnable() {
            @Override
            public void run() {
                if (isFinish()) {
                    return;
                }
                getCallback().onNext(result);
                getCallback().onComplete();
            }
        });
    }

    @Override
    public boolean isGranted(String permission) {
        return isFinish() || PermissionsChecker.requestPermissions(mContext, permission);
    }
}
