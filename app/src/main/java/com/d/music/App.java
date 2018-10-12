package com.d.music;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.d.lib.common.component.cache.Cache;
import com.d.lib.common.component.netstate.NetCompat;
import com.d.lib.common.component.repeatclick.ClickFast;
import com.d.lib.common.utils.log.ULog;
import com.d.lib.permissioncompat.support.PermissionSupport;
import com.d.lib.taskscheduler.TaskScheduler;
import com.d.music.component.media.controler.MediaControler;
import com.d.music.component.service.MusicService;
import com.d.music.component.skin.SkinUtil;
import com.d.music.data.Constants;
import com.d.music.data.database.greendao.util.AppDBUtil;
import com.d.music.data.preferences.Preferences;
import com.d.music.play.activity.PlayActivity;
import com.d.music.setting.activity.ModeActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Application
 * Created by D on 2017/4/28.
 */
public class App extends Application {
    public static final String TAG_EXIT = "tag_exit";

    /**
     * The context of the single, global Application object
     */
    private static Application INSTANCE;

    /**
     * Return the context of the single, global Application object of the
     * current process.  This generally should only be used if you need a
     * Context whose lifecycle is separate from the current context, that is
     * tied to the lifetime of the process rather than the current component.
     */
    @NonNull
    public static Context getContext() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        // Debug switch
        ULog.setDebug(true);
        // Initialize the database
        AppDBUtil.getIns(getContext());
        // Anti-double-click interval setting
        ClickFast.setDelayTime(350);
        // Loading skin
        SkinUtil.initSkin(getContext());
        // Network monitoring
        NetCompat.init(getContext());
        // Runtime permission
        initPermission();
        // Cache
        initCache();
    }

    private void initPermission() {
        PermissionSupport.setPool(new com.d.lib.permissioncompat.support.threadpool.ThreadPool() {
            @Override
            public void executeMain(Runnable r) {
                TaskScheduler.executeMain(r);
            }

            @Override
            public void executeTask(Runnable r) {
                TaskScheduler.executeTask(r);
            }

            @Override
            public void executeNew(Runnable r) {
                TaskScheduler.executeNew(r);
            }
        });
    }

    private void initCache() {
        Cache.setThreadPool(new com.d.lib.common.component.cache.utils.threadpool.ThreadPool() {
            /**
             * Cache download queue limit
             */
            private final static int DOWNLOAD_LIMIT = 3;

            private ExecutorService downloadThreadPool = Executors.newFixedThreadPool(DOWNLOAD_LIMIT);

            @Override
            public void executeMain(Runnable r) {
                TaskScheduler.executeMain(r);
            }

            @Override
            public void executeTask(Runnable r) {
                TaskScheduler.executeTask(r);
            }

            @Override
            public void executeDownload(Runnable r) {
                downloadThreadPool.execute(r);
            }

            @Override
            public void executeNew(Runnable r) {
                TaskScheduler.executeNew(r);
            }
        });
    }

    /**
     * Exit the app
     */
    public static void exit(Context context) {
        if (context == null) {
            return;
        }
        Context appContext = context.getApplicationContext();
        MusicService.timing(appContext, false, 0);
        Preferences.getIns(appContext).putSleepType(0);
        // Save current playback position
        Preferences.getIns(appContext).putLastPlayPosition(MediaControler.getIns(appContext).getPosition());
        // Stop music playback
        MediaControler.getIns(appContext).onDestroy();
        // Stop the service
        appContext.stopService(new Intent(appContext, MusicService.class));

        if (Constants.PlayerMode.mode == Constants.PlayerMode.PLAYER_MODE_NORMAL) {
            exit(appContext, MainActivity.class);
        } else if (Constants.PlayerMode.mode == Constants.PlayerMode.PLAYER_MODE_MINIMALIST) {
            exit(appContext, PlayActivity.class);
        } else {
            exit(appContext, ModeActivity.class);
        }
    }

    public static boolean toFinish(Intent intent) {
        if (intent == null) {
            return false;
        }
        Bundle bundle = intent.getExtras();
        return bundle != null && bundle.getBoolean(TAG_EXIT, false);
    }

    private static void exit(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putBoolean(TAG_EXIT, true);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
