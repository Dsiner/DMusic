package com.d.music.module.repeatclick;

import android.view.View;

/**
 * OnClickFastListener
 * Created by D on 2017/6/6.
 */
public abstract class OnClickFastListener extends ClickUtil implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        //判断当前点击事件与前一次点击事件时间间隔是否小于阙值
        if (isFastDoubleClick()) {
            return;
        }
        onFastClick(v);
    }

    /**
     * 快速点击事件回调方法
     */
    public abstract void onFastClick(View v);
}
