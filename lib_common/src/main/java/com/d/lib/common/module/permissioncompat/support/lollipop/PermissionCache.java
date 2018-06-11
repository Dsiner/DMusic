package com.d.lib.common.module.permissioncompat.support.lollipop;

import java.util.HashMap;
import java.util.Map;

/**
 * PermissionCache
 * Created by D on 2018/4/28.
 */
public class PermissionCache {
    private static Map<String, Boolean> cache = new HashMap<>();

    public static void put(String permission, boolean granted) {
        cache.put(permission, granted);
    }

    public static boolean get(String permission) {
        Boolean granted = cache.get(permission);
        return granted != null ? granted : false;
    }
}
