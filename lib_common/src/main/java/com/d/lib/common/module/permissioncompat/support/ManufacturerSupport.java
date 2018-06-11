package com.d.lib.common.module.permissioncompat.support;

import android.os.Build;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ManufacturerSupport {

    /**
     * Build.MANUFACTURER
     */
    static final String MANUFACTURER_XIAOMI = "Xiaomi";
    static final String MANUFACTURER_MEIZU = "meizu";
    static final String MANUFACTURER_OPPO = "OPPO";

    static final String MANUFACTURER_HUAWEI = "HUAWEI";
    static final String MANUFACTURER_VIVO = "vivo";

    /**
     * Special Xiaomi that need to request permission like LOLLIPOP
     */
    private final static String[] forceXiaomiManufacturers = new String[]{"Redmi Note 3"};
    private final static String[] forceManufacturers = {MANUFACTURER_XIAOMI, MANUFACTURER_MEIZU};
    private final static String[] underMHasPermissionsRequestManufacturer = {MANUFACTURER_XIAOMI,
            MANUFACTURER_MEIZU, MANUFACTURER_OPPO};

    private final static Set<String> forceSet = new HashSet<>(Arrays.asList(forceManufacturers));
    private final static Set<String> underMSet = new HashSet<>(Arrays.asList
            (underMHasPermissionsRequestManufacturer));

    public static boolean isXiaomi() {
        return MANUFACTURER_XIAOMI.equalsIgnoreCase(Build.MANUFACTURER);
    }

    /**
     * Whether the device is special Xiaomi that need to request permission like LOLLIPOP
     */
    public static boolean isXiaomiSpecial() {
        return false;
//        for (String manufacturer : forceXiaomiManufacturers) {
//            if (manufacturer.equalsIgnoreCase(Build.MODEL)) {
//                return true;
//            }
//        }
//        return false;
    }

    public static boolean isOppo() {
        return MANUFACTURER_OPPO.equalsIgnoreCase(Build.MANUFACTURER);
    }

    public static boolean isMeizu() {
        return MANUFACTURER_MEIZU.equalsIgnoreCase(Build.MANUFACTURER);
    }

    /**
     * Build version code is 3.0
     */
    public static boolean isHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * Build version code is 6.0
     */
    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 1.is under {@link Build.VERSION_CODES#M}, above
     * {@link Build.VERSION_CODES#LOLLIPOP}
     * 2.has permissions check
     * 3.open under check
     * <p>
     * now, we know {@link ManufacturerSupport#isXiaomi()}, {@link ManufacturerSupport#isMeizu()}
     */
    public static boolean isUnderMNeedChecked() {
        return isLollipop() && isUnderMHasPermissionManufacturer();
    }

    /**
     * Build version code is under 6.0 but above 5.0
     */
    private static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES
                .LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }

    /**
     * those manufacturer that need request permissions under {@link Build.VERSION_CODES#M},
     * above {@link Build.VERSION_CODES#LOLLIPOP}
     */
    private static boolean isUnderMHasPermissionManufacturer() {
        for (String manufacturer : underMSet) {
            if (manufacturer.equalsIgnoreCase(Build.MANUFACTURER)) {
                return true;
            }
        }
        return false;
    }

    /**
     * those manufacturer that need request by some special measures, above
     * {@link Build.VERSION_CODES#M}
     */
    public static boolean isForceManufacturer() {
        for (String manufacturer : forceSet) {
            if (manufacturer.equalsIgnoreCase(Build.MANUFACTURER)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isLocationMustNeedGpsManufacturer() {
        return MANUFACTURER_OPPO.equalsIgnoreCase(Build.MANUFACTURER);
    }
}
