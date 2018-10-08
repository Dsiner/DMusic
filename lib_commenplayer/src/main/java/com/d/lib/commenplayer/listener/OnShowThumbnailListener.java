package com.d.lib.commenplayer.listener;

import android.widget.ImageView;

public interface OnShowThumbnailListener {

    /**
     * Return the cover view, let the user set it autonomously
     */
    void onShowThumbnail(ImageView ivThumbnail);
}
