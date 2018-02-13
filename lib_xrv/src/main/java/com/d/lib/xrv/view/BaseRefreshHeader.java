package com.d.lib.xrv.view;

/**
 * Created by jianghejie on 15/11/22.
 */
public interface BaseRefreshHeader {

    void onMove(float delta);

    boolean releaseAction();

    void refreshComplete();
}