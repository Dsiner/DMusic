package com.d.lib.commenplayer.listener;

import android.content.res.Configuration;
import android.widget.MediaController;

/**
 * IMediaPlayerControl
 * Created by D on 2017/5/27.
 */
public interface IMediaPlayerControl extends MediaController.MediaPlayerControl {

    boolean isLive();

    /**
     * Live mode
     */
    void setLive(boolean live);

    /**
     * Control
     */
    void play(String url);

    String getUrl();

    /**
     * Ignore traffic alerts when mobile data
     */
    void ignoreMobileNet();

    /**
     * Display setting
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
     * Life cycle
     */
    void onResume();

    void onPause();

    boolean onBackPress();

    void onDestroy();
}
