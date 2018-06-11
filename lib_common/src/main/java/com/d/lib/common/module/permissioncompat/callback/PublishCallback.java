package com.d.lib.common.module.permissioncompat.callback;

/**
 * PublishCallback
 * Created by D on 2017/10/24.
 */
public class PublishCallback<R> extends PermissionCallback<R> {
    private R permission;

    private PublishCallback() {
    }

    private PublishCallback(R permission) {
        this.permission = permission;
    }

    public static <T> PublishCallback<T> create() {
        return new PublishCallback<>();
    }

    public static <T> PublishCallback<T> create(T permission) {
        return new PublishCallback<>(permission);
    }

    @Override
    public void onNext(R permission) {

    }
}