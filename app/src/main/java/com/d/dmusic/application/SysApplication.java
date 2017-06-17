package com.d.dmusic.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.d.dmusic.MainActivity;
import com.d.dmusic.commen.Preferences;
import com.d.dmusic.module.global.MusicCst;
import com.d.dmusic.module.greendao.util.MusicDBUtil;
import com.d.dmusic.module.repeatclick.ClickUtil;
import com.d.dmusic.module.service.MusicService;
import com.d.dmusic.module.skin.SkinUtil;
import com.d.dmusic.mvp.activity.PlayActivity;
import com.d.dmusic.mvp.activity.PlayerModeActivity;

/**
 * Application
 * Created by D on 2017/4/28.
 */
public class SysApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化数据库
        MusicDBUtil.getInstance(getApplicationContext());
        ClickUtil.setDelayTime(350);
        SkinUtil.initSkin(getApplicationContext());
    }

    /**
     * 退出应用
     */
    public static void exit(Context context) {
        if (context == null) {
            return;
        }
        Context appContext = context.getApplicationContext();
        //释放全局静态变量
        MusicCst.release();
        MusicService.timing(appContext, false, 0);
        Preferences.getInstance(appContext).putSleepType(0);
        //停止音乐播放
        MusicService.getControl(appContext).onDestroy();
        //停止服务
        appContext.stopService(new Intent(appContext, MusicService.class));

        if (MusicCst.playerMode == MusicCst.PLAYER_MODE_NORMAL) {
            exit(appContext, MainActivity.class);
        } else if (MusicCst.playerMode == MusicCst.PLAYER_MODE_MINIMALIST) {
            exit(appContext, PlayActivity.class);
        } else {
            exit(appContext, PlayerModeActivity.class);
        }
//        System.exit(0);

//        int pid = android.os.Process.myPid();
//        android.os.Process.killProcess(pid);
//
//        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
//        manager.killBackgroundProcesses(getPackageName());
    }

    public static boolean toFinish(Intent intent) {
        if (intent == null) {
            return false;
        }
        Bundle bundle = intent.getExtras();
        return bundle != null && bundle.getBoolean(MusicCst.TAG_EXIT, false);
    }

    private static void exit(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putBoolean(MusicCst.TAG_EXIT, true);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
