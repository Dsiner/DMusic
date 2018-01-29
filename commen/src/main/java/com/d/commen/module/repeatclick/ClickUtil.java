package com.d.commen.module.repeatclick;

/**
 * OnClickFastListener
 * Created by D on 2017/6/6.
 */
public class ClickUtil {
    private static long DELAY_TIME = 900;//防止快速点击默认等待时长为900ms
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < DELAY_TIME) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 设置防止快速点击默认等待时长
     *
     * @param delay:毫秒
     */
    public static void setDelayTime(long delay) {
        DELAY_TIME = delay;
    }
}
