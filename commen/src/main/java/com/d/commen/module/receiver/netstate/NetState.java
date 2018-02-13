package com.d.commen.module.receiver.netstate;

/**
 * 网络状态
 * Created by D on 2018/2/5.
 */
public class NetState {
    /**
     * 当前网络状态
     */
    static int NET_STATUS = 0;

    public final static int NO_AVAILABLE = 0;
    public final static int UN_CONNECTED = 1;
    public final static int CONNECTED_MOBILE = 2;
    public final static int CONNECTED_WIFI = 3;

    /**
     * 获取当前网络状态
     */
    public static int getStatus() {
        int state = NET_STATUS;
        return state;
    }
}
