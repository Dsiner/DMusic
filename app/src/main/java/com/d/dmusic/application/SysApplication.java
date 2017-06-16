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
import com.d.dmusic.mvp.activity.PlayActivity;
import com.d.dmusic.mvp.activity.PlayerModeActivity;

/**
 * Application
 * Created by D on 2017/4/28.
 */
public class SysApplication extends Application {
    private static SysApplication instance;

    public static SysApplication getInstance() {
        if (instance == null) {
            synchronized (SysApplication.class) {
                if (instance == null) {
                    instance = new SysApplication();
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //初始化数据库
        MusicDBUtil.getInstance(getApplicationContext());
        ClickUtil.setDelayTime(350);
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    /**
     * 退出应用
     */
    public void exit() {
        //释放全局静态变量
        MusicCst.release();
        //保存当前播放位置
        Preferences.getInstance(getApplicationContext()).putLastPlayPosition(MusicService.getControl().getCurPos());
        //停止服务
        stopService(new Intent(getApplicationContext(), MusicService.class));
        //停止音乐播放
        MusicService.getControl().onDestroy();

        if (MusicCst.playerMode == MusicCst.PLAYER_MODE_NORMAL) {
            exit(getApplicationContext(), MainActivity.class);
        } else if (MusicCst.playerMode == MusicCst.PLAYER_MODE_MINIMALIST) {
            exit(getApplicationContext(), PlayActivity.class);
        } else {
            exit(getApplicationContext(), PlayerModeActivity.class);
        }
        instance = null;//release
        System.exit(0);

//        int pid = android.os.Process.myPid();
//        android.os.Process.killProcess(pid);
//
//        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
//        manager.killBackgroundProcesses(getPackageName());
    }

    public static void exit(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putBoolean(MusicCst.TAG_EXIT, true);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static boolean toFinish(Intent intent) {
        if (intent == null) {
            return false;
        }
        Bundle bundle = intent.getExtras();
        return bundle != null && bundle.getBoolean(MusicCst.TAG_EXIT, false);
    }
}
