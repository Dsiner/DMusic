package com.d.lib.commenplayer.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.d.lib.commenplayer.media.MediaManager;

public class MediaPlayerService extends Service {
    private volatile static MediaManager MEDIA_MANAGER;

    public static void intentToStart(Context context) {
        context.startService(newIntent(context));
    }

    public static void intentToStop(Context context) {
        context.stopService(newIntent(context));
    }

    private static Intent newIntent(Context context) {
        return new Intent(context, MediaPlayerService.class);
    }

    public static MediaManager getMediaManager(Context context) {
        if (MEDIA_MANAGER == null) {
            synchronized (MediaPlayerService.class) {
                MEDIA_MANAGER = MediaManager.instance(context);
            }
        }
        return MEDIA_MANAGER;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        MEDIA_MANAGER.release(getApplicationContext(), true);
        MEDIA_MANAGER = null;
        super.onDestroy();
    }
}
