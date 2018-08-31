package com.d.lib.xrv.view;

/**
 * ListState
 * Created by D on 2017/4/25.
 */
public class ListState {

    /**
     * Header
     */
    public final static int STATE_NORMAL = 0x00;
    public final static int STATE_RELEASE_TO_REFRESH = 0x01;
    public final static int STATE_REFRESHING = 0x02;
    public final static int STATE_DONE = 0x03;

    /**
     * Footer
     */
    public final static int STATE_LOADING = 0x10;
    public final static int STATE_COMPLETE = 0x11;
    public final static int STATE_NOMORE = 0x12;
    public final static int STATE_LOADMORE_ERROR = 0x13;
}
