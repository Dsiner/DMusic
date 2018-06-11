package com.d.lib.common.module.permissioncompat.callback;

@Deprecated
public abstract class PermissionSimpleCallback {
    /**
     * All permissions are granted !
     */
    public abstract void onGranted();

    /**
     * At least one denied permission, need to go to the settings
     */
    public void onDeny() {
    }
}
