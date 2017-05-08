package com.d.dmusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d.commen.base.BaseFragmentActivity;
import com.d.dmusic.application.SysApplication;
import com.d.dmusic.module.global.MusciCst;
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
    @Bind(R.id.dl_drawer)
    DrawerLayout drawerLayout;

    private Context context;
    public static FragmentManager fManger;
    private CurrentInfoReceiver currentInfoReceiver;

    @OnClick({R.id.iv_play, R.id.llyt_menu_exit})
    public void onClickListener(View v) {
        switch (v.getId()) {
            case R.id.iv_title_back:
                fManger.popBackStack();
                break;
            case R.id.iv_title_more:
                break;
            case R.id.iv_play:
                Intent intent = new Intent(this, PlayActivity.class);
                startActivity(intent);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        Util.setScreenSize(this);
        StatusBarCompat.compat(MainActivity.this, 0xffFD8D22);//沉浸式状态栏
        registerReceiver();//注册广播监听器
    }

    @Override
    protected void init() {
        //设置抽屉打开时，主要内容区被自定义阴影覆盖
        drawerLayout.addDrawerListener(this);
        fManger = getSupportFragmentManager();
        fManger.beginTransaction().replace(R.id.framement,
                new MainFragment()).addToBackStack(null).commitAllowingStateLoss();
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
        filter.addAction(MusciCst.MUSIC_CURRENT_INFO);
        registerReceiver(currentInfoReceiver, filter);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        View content = drawerLayout.getChildAt(0);
        View menu = drawerView;
        float scale = 1 - slideOffset;
        float rightScale = 0.8f + scale * 0.2f;
        float leftScale = 1 - 0.3f * scale;

        ViewHelper.setScaleX(menu, leftScale);
        ViewHelper.setScaleY(menu, leftScale);
        ViewHelper.setAlpha(menu, 0.6f + 0.4f * (1 - scale));

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
            String action = intent.getAction();
            if (action.equals(MusciCst.MUSIC_CURRENT_INFO)) {
                String songName = intent.getStringExtra("songName");
                String singer = intent.getStringExtra("singer");
                ULog.d("main:SongName:" + songName + "--Singer:" + singer);
                if (tvSongName != null && tvSinger != null) {
                    tvSongName.setText(songName);
                    tvSinger.setText(singer);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (currentInfoReceiver != null) {
            unregisterReceiver(currentInfoReceiver);
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (fManger.getBackStackEntryCount() <= 1) {
                finish();
            } else {
                fManger.popBackStack();
            }
            return true;
        }
        return false;
    }
}
