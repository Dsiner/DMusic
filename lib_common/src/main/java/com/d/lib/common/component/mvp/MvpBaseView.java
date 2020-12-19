package com.d.lib.common.component.mvp;

/**
 * MvpBaseView
 * Created by D on 2017/8/22.
 */
public interface MvpBaseView extends MvpView {

    /**
     * Set the default state.
     * When the network request error occurs, you can get the display status by getState(Throwable e).
     *
     * @param state: DSLayout.STATE_LOADING、DSLayout.STATE_EMPTY、
     *               DSLayout.STATE_NET_ERROR、DSLayout.GONE
     */
    void setState(int state);

    /**
     * Show loading dialog
     */
    void showLoadingDialog();

    /**
     * Dismiss loading dialog
     */
    void dismissLoadingDialog();
}
