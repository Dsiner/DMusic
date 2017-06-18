package com.d.music;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;

import com.d.music.commen.Preferences;
import com.d.music.module.global.MusicCst;
import com.d.music.module.service.MusicService;
import com.d.music.mvp.activity.PlayActivity;
import com.d.music.utils.StatusBarCompat;

import java.lang.ref.WeakReference;

/**
 * SsssActivity
 * Created by D on 2017/4/28.
 */
public class SplashActivity extends Activity {
    private Context context;
    private WeakHandler handler = new WeakHandler(this);
    private Preferences p;
    private int delayTime = 2500;//splash时间
    private boolean isBackPressed;

    static class WeakHandler extends Handler {
        WeakReference<SplashActivity> weakReference;

        public WeakHandler(SplashActivity activity) {
            weakReference = new WeakReference<SplashActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SplashActivity theActivity = weakReference.get();
            if (theActivity != null && !theActivity.isFinishing() && !theActivity.isBackPressed) {
                switch (msg.what) {
                    case 1:
                        MusicService.startService(theActivity.getApplicationContext());
                        theActivity.startActivity(new Intent(theActivity, MainActivity.class));
                        theActivity.finish();
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        StatusBarCompat.compat(SplashActivity.this, Color.parseColor("#ececec"));//沉浸式状态栏
        p = Preferences.getInstance(getApplicationContext());
        if (p.getIsFirst()) {
            //首次安装启动
            MusicCst.playerMode = MusicCst.PLAYER_MODE_NORMAL;
            startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
            finish();
            return;
        }
        if (!MusicService.isRunning()) {
            //第一次启动
            MusicCst.playerMode = p.getPlayerMode();
        }
        switch (MusicCst.playerMode) {
            case MusicCst.PLAYER_MODE_MINIMALIST:
                MusicService.startService(getApplicationContext());
                PlayActivity.openActivity(SplashActivity.this);
                finish();
                break;
            case MusicCst.PLAYER_MODE_NOTIFICATION:
                MusicService.startService(getApplicationContext());
                finish();
                break;
            default:
                if (!MusicService.isRunning()) {
                    //第一次启动
                    initView();
                    handler.sendEmptyMessageDelayed(1, delayTime);
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                break;
        }
    }

    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        findViewById(R.id.iv_splash).setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        isBackPressed = true;
        super.onBackPressed();
    }
}
