package com.d.dmusic.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.d.dmusic.module.greendao.util.MusicDBUtil;
import com.d.dmusic.module.repeatclick.ClickUtil;
import com.d.dmusic.module.service.MusicService;

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
        ClickUtil.setDelayTime(400);
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public void exit() {
        MusicService.getControl().onDestroy();// 停止音乐播放
        stopService(new Intent(getApplicationContext(), MusicService.class));// 停止服务
        System.exit(0);
    }
}
