package com.d.lib.common.component.quickclick;

import android.view.View;

/**
 * OnAvailableClickListener
 * Created by D on 2017/6/6.
 */
public abstract class OnAvailableClickListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        // Determine whether the current click event and the previous click event interval
        // are less than the threshold value
        if (QuickClick.isQuickClick()) {
            return;
        }
        onAvailableClick(v);
    }

    /**
     * Quick click event callback method
     */
    public abstract void onAvailableClick(View v);
}
