package com.d.music;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;

import androidx.annotation.Nullable;

import com.d.lib.common.component.statusbarcompat.StatusBarCompat;
import com.d.music.component.service.NotificationService;
import com.d.music.data.Constants;
import com.d.music.data.preferences.Preferences;
import com.d.music.play.activity.PlayActivity;

import java.lang.ref.WeakReference;

/**
 * SplashActivity
 * Created by D on 2017/4/28.
 */
public class SplashActivity extends Activity {
    // Splash时间
    private final int mDelayTime = 2500;
    private boolean mIsBackPressed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 沉浸式状态栏
        StatusBarCompat.setStatusBarColor(SplashActivity.this, Color.parseColor("#ececec"));
        Preferences preferences = Preferences.getInstance(getApplicationContext());
        WeakHandler handler = new WeakHandler(this);
        if (preferences.getIsFirst()) {
            // 首次安装启动
            Constants.PlayerMode.sPlayerMode = Constants.PlayerMode.PLAYER_MODE_NORMAL;
            startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
            finish();
            return;
        }
        if (!NotificationService.isRunning()) {
            // 第一次启动
            Constants.PlayerMode.sPlayerMode = preferences.getPlayerMode();
        }
        switch (Constants.PlayerMode.sPlayerMode) {
            case Constants.PlayerMode.PLAYER_MODE_MINIMALIST:
                NotificationService.startService(getApplicationContext());
                PlayActivity.openActivity(SplashActivity.this);
                finish();
                break;

            case Constants.PlayerMode.PLAYER_MODE_NOTIFICATION:
                NotificationService.startService(getApplicationContext());
                finish();
                break;

            default:
                if (!NotificationService.isRunning()) {
                    // 第一次启动
                    initView();
                    handler.sendEmptyMessageDelayed(1, mDelayTime);
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                break;
        }
    }

    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.module_common_activity_splash);
        findViewById(R.id.iv_splash).setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        mIsBackPressed = true;
        super.onBackPressed();
    }

    static class WeakHandler extends Handler {
        WeakReference<SplashActivity> ref;

        WeakHandler(SplashActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SplashActivity theActivity = ref.get();
            if (theActivity != null && !theActivity.isFinishing() && !theActivity.mIsBackPressed) {
                switch (msg.what) {
                    case 1:
                        NotificationService.startService(theActivity.getApplicationContext());
                        theActivity.startActivity(new Intent(theActivity, MainActivity.class));
                        theActivity.finish();
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }
}
