package com.d.commenplayer.listener;

import android.content.res.Configuration;
import android.widget.MediaController;

/**
 * IMediaPlayerControl
 * Created by D on 2017/5/27.
 */
public interface IMediaPlayerControl extends MediaController.MediaPlayerControl {
    /**
     * live mode
     */
    void setLive(boolean live);

    boolean isLive();

    /**
     * control
     */
    void play(String url);

    String getUrl();

    /**
     * 忽略移动网络时流量提醒
     */
    void ignoreMobileNet();

    /**
     * display setting
     */
    void setPlayerVisibility(int visibility);

    void toggleStick();

    void toggleSystemUI(boolean show);

    void lockProgress(boolean lock);

    void progressTo(int position, int bufferPercentage);

    void setScaleType(int scaleType);

    int toggleAspectRatio();

    void onConfigurationChanged(Configuration newConfig);

    void onAnimationUpdate(float factor);

    void toggleOrientation();

    /**
     * liftcycle
     */
    void onResume();

    void onPause();

    boolean onBackPress();

    void onDestroy();
}
