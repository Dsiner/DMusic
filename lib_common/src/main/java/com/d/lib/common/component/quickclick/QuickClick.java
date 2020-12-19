package com.d.lib.common.component.quickclick;

/**
 * Prevent quick clicks
 * Created by D on 2017/6/6.
 */
public class QuickClick {

    /**
     * Prevent quick clicks and default waiting time is 900ms.
     */
    private static long sSpanTime = 900;
    private static long sLastClickTime;

    public static boolean isQuickClick() {
        final long currentTime = System.currentTimeMillis();
        final long span = currentTime - sLastClickTime;
        if (Math.abs(span) < sSpanTime) {
            return true;
        }
        sLastClickTime = currentTime;
        return false;
    }

    /**
     * Set the default wait time to prevent quick clicks.
     *
     * @param delay millisecond
     */
    public static void setSpanTime(long delay) {
        sSpanTime = delay;
    }
}
