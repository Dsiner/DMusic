package com.d.lib.common.component.repeatclick;

import android.view.View;

/**
 * OnClickFastListener
 * Created by D on 2017/6/6.
 */
public abstract class OnClickFastListener extends ClickFast implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        // Determine whether the current click event and the previous click event interval
        // are less than the threshold value
        if (isFastDoubleClick()) {
            return;
        }
        onFastClick(v);
    }

    /**
     * Quick click event callback method
     */
    public abstract void onFastClick(View v);
}
