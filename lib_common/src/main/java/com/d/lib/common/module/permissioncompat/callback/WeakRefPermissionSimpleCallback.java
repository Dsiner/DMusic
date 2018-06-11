package com.d.lib.common.module.permissioncompat.callback;

import android.app.Activity;
import android.app.Fragment;

import com.d.lib.common.module.permissioncompat.support.ManufacturerSupport;

import java.lang.ref.WeakReference;

@Deprecated
public abstract class WeakRefPermissionSimpleCallback<T> extends PermissionSimpleCallback {
    private WeakReference<T> view;

    public WeakRefPermissionSimpleCallback(T view) {
        this.view = new WeakReference<T>(view);
    }

    protected T getView() {
        return view != null ? view.get() : null;
    }

    protected boolean isFinish() {
        if (getView() == null) {
            return true;
        }
        if (getView() instanceof Activity) {
            return ((Activity) getView()).isFinishing();
        } else if (ManufacturerSupport.isHoneycomb() && getView() instanceof Fragment) {
            Activity activity = ((Fragment) getView()).getActivity();
            return activity == null || activity.isFinishing();
        } else if (getView() instanceof android.support.v4.app.Fragment) {
            Activity activity = ((android.support.v4.app.Fragment) getView()).getActivity();
            return activity == null || activity.isFinishing();
        }
        return false;
    }
}
