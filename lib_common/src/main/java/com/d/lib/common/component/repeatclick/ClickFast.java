package com.d.lib.common.component.repeatclick;

/**
 * Prevent quick clicks
 * Created by D on 2017/6/6.
 */
public class ClickFast {

    /**
     * Prevent quick clicks and default waiting time is 900ms.
     */
    private static long DELAY_TIME = 900;
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
     * Set the default wait time to prevent quick clicks.
     *
     * @param delay millisecond
     */
    public static void setDelayTime(long delay) {
        DELAY_TIME = delay;
    }
}
