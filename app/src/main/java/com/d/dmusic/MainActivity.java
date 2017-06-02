package com.d.dmusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d.commen.base.BaseFragmentActivity;
import com.d.dmusic.application.SysApplication;
import com.d.dmusic.module.global.MusicCst;
import com.d.dmusic.module.repeatclick.ClickUtil;
import com.d.dmusic.module.service.MusicService;
import com.d.dmusic.mvp.activity.PlayActivity;
import com.d.dmusic.mvp.fragment.MainFragment;
import com.d.dmusic.utils.StatusBarCompat;
import com.d.dmusic.utils.Util;
import com.d.dmusic.utils.log.ULog;
import com.nineoldandroids.view.ViewHelper;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * MainActivity
 * Created by D on 2017/4/28.
 */
public class MainActivity extends BaseFragmentActivity implements DrawerListener {
    @Bind(R.id.tv_song_name)
    TextView tvSongName;
    @Bind(R.id.tv_singer)
    TextView tvSinger;
    @Bind(R.id.llyt_menu_exit)
    LinearLayout llytExit;
    @Bind(R.id.iv_play)
    ImageView ivPlay;

    private Context context;
    private static DrawerLayout drawer;
    public static FragmentManager fManger;
    private CurrentInfoReceiver currentInfoReceiver;

    @OnClick({R.id.iv_play, R.id.llyt_menu_exit})
    public void onClickListener(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_title_left:
                popBackStack();
                break;
            case R.id.iv_play:
                PlayActivity.openActivity(MainActivity.this);
                break;
            case R.id.llyt_menu_exit:
                SysApplication.getInstance().exit();// 退出
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        StatusBarCompat.compat(MainActivity.this, getResources().getColor(R.color.color_main));//沉浸式状态栏
        context = this;
        Util.setScreenSize(this);
        registerReceiver();//注册广播监听器

        drawer = (DrawerLayout) findViewById(R.id.dl_drawer);
        //设置抽屉打开时，主要内容区被自定义阴影覆盖
        drawer.addDrawerListener(this);
        fManger = getSupportFragmentManager();
        replace(new MainFragment());
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvSongName.setText(MusicService.getControl().getCurSongName());
        tvSinger.setText(MusicService.getControl().getCurSinger());
    }

    /**
     * 定义和注册广播接收器
     */
    private void registerReceiver() {
        currentInfoReceiver = new CurrentInfoReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicCst.MUSIC_CURRENT_INFO);
        registerReceiver(currentInfoReceiver, filter);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        View content = drawer.getChildAt(0);
        View menu = drawerView;
        float scale = 1 - slideOffset;
        float rightScale = 0.8f + scale * 0.2f;
        float leftScale = 1 - 0.3f * scale;

        ViewHelper.setScaleX(menu, leftScale);
        ViewHelper.setScaleY(menu, leftScale);
//        ViewHelper.setAlpha(menu, 0.6f + 0.4f * (1 - scale));

        ViewHelper.setTranslationX(content, -menu.getMeasuredWidth() * slideOffset);
        ViewHelper.setPivotX(content, content.getMeasuredWidth());
        ViewHelper.setPivotY(content, content.getMeasuredHeight() / 2);
        content.invalidate();
        ViewHelper.setScaleX(content, rightScale);
        ViewHelper.setScaleY(content, rightScale);
    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    /**
     * 用来接收从service传回来的广播的内部类
     */
    public class CurrentInfoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), MusicCst.MUSIC_CURRENT_INFO)
                    && tvSongName != null && tvSinger != null) {
                String songName = intent.getStringExtra("songName");
                String singer = intent.getStringExtra("singer");
                ULog.d("main:SongName:" + songName + "--Singer:" + singer);
                tvSongName.setText(songName);
                tvSinger.setText(singer);
            }
        }
    }

    @Override
    protected void onDestroy() {
        releaseResource();
        if (currentInfoReceiver != null) {
            unregisterReceiver(currentInfoReceiver);
        }
        super.onDestroy();
    }

    private void releaseResource() {
        drawer = null;
        fManger = null;
    }

    @Override
    public void onBackPressed() {
        if (fManger.getBackStackEntryCount() <= 1) {
            finish();
        } else {
            popBackStack();
        }
    }

    public static void setDrawerLockMode(int lockMode) {
        if (drawer != null) {
            drawer.setDrawerLockMode(lockMode);
        }
    }

    public static void replace(Fragment fragment) {
        if (fManger != null) {
            fManger.beginTransaction().replace(R.id.framement, fragment)
                    .addToBackStack(null).commitAllowingStateLoss();
        }
    }

    public static void popBackStack() {
        if (fManger != null) {
            fManger.popBackStack();
        }
    }
}
