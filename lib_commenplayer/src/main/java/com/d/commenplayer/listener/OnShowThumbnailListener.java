package com.d.commenplayer.listener;

import android.widget.ImageView;

public interface OnShowThumbnailListener {
    /**
     * 回传封面的view，让用户自主设置
     */
    void onShowThumbnail(ImageView ivThumbnail);
}
