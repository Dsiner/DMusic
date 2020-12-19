package com.d.lib.common.component.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * NetworkChangeReceiver
 * Created by D on 2017/5/28.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return;
        }
        NetworkCompat.getNetworkType(context);
    }
}
