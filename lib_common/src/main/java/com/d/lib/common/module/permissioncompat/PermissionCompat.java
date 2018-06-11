package com.d.lib.common.module.permissioncompat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.d.lib.common.module.permissioncompat.callback.PermissionCallback;
import com.d.lib.common.module.permissioncompat.callback.PermissionSimpleCallback;
import com.d.lib.common.module.permissioncompat.callback.PublishCallback;
import com.d.lib.common.module.permissioncompat.support.ManufacturerSupport;
import com.d.lib.common.module.permissioncompat.support.PermissionSupport;
import com.d.lib.common.module.permissioncompat.support.lollipop.PermissionsChecker;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * PermissionCompat
 * For more detail, check this https://github.com/tbruyelle/RxPermissions
 * Created by D on 2018/4/13.
 */
public class PermissionCompat {

    public final static String TAG = "PermissionCompat";

    protected Context mContext;
    protected WeakReference<Activity> mRefActivity;
    protected String[] mPermissions;
    protected PermissionCallback<Permission> mCallback;
    protected PermissionsFragment mPermissionsFragment;
    protected PermissionSchedulers.Schedulers subscribeScheduler = PermissionSchedulers.Schedulers.DEFAULT_THREAD;
    protected PermissionSchedulers.Schedulers observeOnScheduler = PermissionSchedulers.Schedulers.DEFAULT_THREAD;

    PermissionCompat(@NonNull Activity activity) {
        mContext = activity.getApplicationContext();
        mRefActivity = new WeakReference<>(activity);
        if (ManufacturerSupport.isHoneycomb()) {
            mPermissionsFragment = getPermissionsFragment(activity);
        }
    }

    protected Activity getActivity() {
        return mRefActivity != null ? mRefActivity.get() : null;
    }

    protected PermissionCallback<Permission> getCallback() {
        return mCallback;
    }

    protected boolean isFinish() {
        return getActivity() == null || getActivity().isFinishing();
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    protected PermissionsFragment getPermissionsFragment(Activity activity) {
        PermissionsFragment permissionsFragment = findPermissionsFragment(activity);
        boolean isNewInstance = permissionsFragment == null;
        if (isNewInstance) {
            permissionsFragment = new PermissionsFragment();
            FragmentManager fragmentManager = activity.getFragmentManager();
            fragmentManager.beginTransaction()
                    .add(permissionsFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return permissionsFragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    protected PermissionsFragment findPermissionsFragment(Activity activity) {
        return (PermissionsFragment) activity.getFragmentManager().findFragmentByTag(TAG);
    }

    public static PermissionCompat with(Activity activity) {
        int type = PermissionSupport.getType();
        if (type == PermissionSupport.TYPE_LOLLIPOP) {
            return new PermissionLollipop(activity);
        } else if (type == PermissionSupport.TYPE_MARSHMALLOW_XIAOMI) {
            return new PermissionXiaomi(activity);
        }
        return new PermissionCompat(activity);
    }

    /**
     * Map emitted items from the source observable into one combined {@link Permission} object. Only if all permissions are granted,
     * permission also will be granted. If any permission has {@code shouldShowRationale} checked, than result also has it checked.
     * <p>
     * If one or several permissions have never been requested, invoke the related framework method
     * to ask the user if he allows the permissions.
     */
    public PermissionCompat requestEachCombined(final String... permissions) {
        this.mPermissions = permissions;
        return this;
    }

    public PermissionCompat subscribeOn(PermissionSchedulers.Schedulers scheduler) {
        this.subscribeScheduler = scheduler;
        return this;
    }

    public PermissionCompat observeOn(PermissionSchedulers.Schedulers scheduler) {
        this.observeOnScheduler = scheduler;
        return this;
    }

    public void requestPermissions(PermissionCallback<Permission> callback) {
        if (mPermissions == null || mPermissions.length == 0) {
            throw new IllegalArgumentException("PermissionCompat.request/requestEach requires at least one input permission");
        }
        this.mCallback = callback;
        PermissionSchedulers.switchThread(subscribeScheduler, new Runnable() {
            @Override
            public void run() {
                if (isFinish()) {
                    return;
                }
                requestImplementation(mPermissions);
            }
        });
    }

    protected void requestImplementation(final String... permissions) {
        final List<Permission> ps = new ArrayList<>();
        final List<PublishCallback<Permission>> publishs = new ArrayList<>(permissions.length);
        final List<String> unrequestedPermissions = new ArrayList<>();

        // In case of multiple permissions, we create an Observable for each of them.
        // At the end, the observables are combined to have a unique response.
        for (String permission : permissions) {
            Log.d(PermissionCompat.TAG, "Requesting permission " + permission);
            if (isGranted(permission)) {
                // Already granted, or not Android M
                // Return a granted Permission object.
                Permission p = new Permission(permission, true, false);
                ps.add(p);
                publishs.add(PublishCallback.create(p));
                continue;
            }

            if (isRevoked(permission)) {
                // Revoked by a policy, return a denied Permission object.
                Permission p = new Permission(permission, false, false);
                ps.add(p);
                publishs.add(PublishCallback.create(p));
                continue;
            }

            PublishCallback<Permission> subject = mPermissionsFragment.getSubjectByPermission(permission);
            // Create a new subject if not exists
            if (subject == null) {
                unrequestedPermissions.add(permission);
                subject = PublishCallback.create();
                mPermissionsFragment.setSubjectForPermission(permission, subject);
            }

            publishs.add(subject);
        }

        if (!unrequestedPermissions.isEmpty()) {
            String[] unrequestedPermissionsArray = unrequestedPermissions.toArray(new String[unrequestedPermissions.size()]);
            Log.d(PermissionCompat.TAG, "requestPermissionsFromFragment " + TextUtils.join(", ", unrequestedPermissionsArray));
            if (isFinish()) {
                return;
            }
            mPermissionsFragment.requestPermissions(unrequestedPermissionsArray, new PermissionCallback<List<Permission>>() {
                @Override
                public void onNext(List<Permission> permission) {
                    if (isFinish()) {
                        return;
                    }
                    List<Permission> list = new ArrayList<>();
                    list.addAll(ps);
                    list.addAll(permission);
                    final Permission result = combinePermission(list);
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
                public void onError(Throwable e) {
                    if (isFinish()) {
                        return;
                    }
                    getCallback().onError(e);
                    getCallback().onComplete();
                }
            });
        } else {
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
    }

    protected Permission combinePermission(List<Permission> permissions) {
        return new Permission(permissions);
    }

    /**
     * Invokes Activity.shouldShowRequestPermissionRationale and wraps
     * the returned value in an observable.
     * <p>
     * In case of multiple permissions, only emits true if
     * Activity.shouldShowRequestPermissionRationale returned true for
     * all revoked permissions.
     * <p>
     * You shouldn't call this method if all permissions have been granted.
     * <p>
     * For SDK &lt; 23, the observable will always emit false.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean shouldShowRequestPermissionRationale(final Activity activity, final String... permissions) {
        if (!ManufacturerSupport.isMarshmallow()) {
            return false;
        }
        return shouldShowRequestPermissionRationaleImplementation(activity, permissions);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean shouldShowRequestPermissionRationaleImplementation(final Activity activity, final String... permissions) {
        for (String p : permissions) {
            if (!isGranted(p) && !activity.shouldShowRequestPermissionRationale(p)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the permission is already granted.
     * <p>
     * Always true if SDK &lt; 23.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isGranted(String permission) {
        return !ManufacturerSupport.isMarshmallow()
                || mContext.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Returns true if the permission has been revoked by a policy.
     * <p>
     * Always false if SDK &lt; 23.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isRevoked(String permission) {
        return ManufacturerSupport.isMarshmallow()
                && mContext.getPackageManager().isPermissionRevokedByPolicy(permission, mContext.getPackageName());
    }

    /**
     * Checks all given permissions have been granted.
     *
     * @param grantResults results
     * @return returns true if all permissions have been granted.
     */
    public static boolean verifyPermissions(int... grantResults) {
        if (grantResults.length == 0) {
            return false;
        }
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the permissions are all already granted.
     */
    public static boolean hasSelfPermissions(@NonNull Context context, String... permissions) {
        context = context.getApplicationContext();
        if (permissions == null || permissions.length <= 0) {
            throw new IllegalArgumentException("permissions is null or empty");
        }
        for (String permission : permissions) {
            if (!hasSelfPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determine context has access to the given permission.
     * <p>
     * This is a workaround for RuntimeException of Parcel#readException.
     * For more detail, check this issue https://github.com/hotchemi/PermissionsDispatcher/issues/107
     *
     * @param context    context
     * @param permission permission
     * @return returns true if context has access to the given permission, false otherwise.
     * @see #hasSelfPermissions(Context, String...)
     */
    private static boolean hasSelfPermission(Context context, String permission) {
        context = context.getApplicationContext();
        int type = PermissionSupport.getType();
        if (type == PermissionSupport.TYPE_LOLLIPOP) {
            return PermissionsChecker.isPermissionGranted(permission);
        } else if (type == PermissionSupport.TYPE_MARSHMALLOW_XIAOMI) {
            return PermissionXiaomi.hasSelfPermissionForXiaomi(context, permission);
        } else if (type == PermissionSupport.TYPE_MARSHMALLOW) {
            try {
                return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
            } catch (RuntimeException t) {
                return false;
            }
        }
        return true;
    }

    @Deprecated
    public static void checkSelfPermissions(Activity activity, final PermissionSimpleCallback callback, final String... permissions) {
        if (activity == null) {
            return;
        }
        if (!PermissionSupport.isL() && hasSelfPermissions(activity, permissions)) {
            PermissionSchedulers.switchThread(PermissionSchedulers.Schedulers.MAIN_THREAD, new Runnable() {
                @Override
                public void run() {
                    if (callback != null) {
                        callback.onGranted();
                    }
                }
            });
            return;
        }

        PermissionCompat.with(activity).requestEachCombined(permissions)
                .subscribeOn(PermissionSchedulers.io())
                .observeOn(PermissionSchedulers.mainThread())
                .requestPermissions(new PermissionCallback<Permission>() {
                    @Override
                    public void onNext(Permission permission) {
                        if (permission.granted) {
                            // All permissions are granted !
                            if (callback != null) {
                                callback.onGranted();
                            }
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // At least one denied permission without ask never again
                            if (callback != null) {
                                callback.onDeny();
                            }
                        } else {
                            // At least one denied permission with ask never again
                            // Need to go to the settings
                            if (callback != null) {
                                callback.onDeny();
                            }
                        }
                    }
                });
    }
}
