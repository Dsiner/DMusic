package com.d.music;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.d.lib.common.component.cache.Cache;
import com.d.lib.common.component.cache.utils.threadpool.ThreadPool;
import com.d.lib.common.component.netstate.NetCompat;
import com.d.lib.common.component.repeatclick.ClickFast;
import com.d.lib.taskscheduler.TaskScheduler;
import com.d.music.common.Constants;
import com.d.music.common.preferences.Preferences;
import com.d.music.component.greendao.util.AppDBUtil;
import com.d.music.component.media.controler.MediaControler;
import com.d.music.component.service.MusicService;
import com.d.music.component.skin.SkinUtil;
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

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化数据库
        AppDBUtil.getIns(getApplicationContext());
        // 防双击间隔设置
        ClickFast.setDelayTime(350);
        // 加载皮肤
        SkinUtil.initSkin(getApplicationContext());
        // 网络监听
        NetCompat.init(getApplicationContext());
        // Cache
        Cache.setThreadPool(new ThreadPool() {
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
     * 退出应用
     */
    public static void exit(Context context) {
        if (context == null) {
            return;
        }
        Context appContext = context.getApplicationContext();
        MusicService.timing(appContext, false, 0);
        Preferences.getIns(appContext).putSleepType(0);
        // 保存当前播放位置
        Preferences.getIns(appContext).putLastPlayPosition(MediaControler.getIns(appContext).getPosition());
        // 停止音乐播放
        MediaControler.getIns(appContext).onDestroy();
        // 停止服务
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
