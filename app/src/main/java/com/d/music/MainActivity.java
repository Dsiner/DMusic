package com.d.music;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.d.lib.common.component.mvp.app.v4.BaseFragmentActivity;
import com.d.lib.common.component.quickclick.QuickClick;
import com.d.lib.common.component.statusbarcompat.StatusBarCompat;
import com.d.lib.common.util.ViewHelper;
import com.d.lib.common.util.log.ULog;
import com.d.lib.common.widget.BadgeView;
import com.d.music.component.media.controler.MediaControl;
import com.d.music.data.preferences.Preferences;
import com.d.music.event.eventbus.MusicInfoEvent;
import com.d.music.local.fragment.MainFragment;
import com.d.music.play.activity.PlayActivity;
import com.d.music.setting.activity.SettingActivity;
import com.d.music.setting.activity.SkinActivity;
import com.d.music.setting.activity.SleepActivity;
import com.d.music.transfer.activity.TransferActivity;
import com.d.music.transfer.manager.TransferDataObservable;
import com.d.music.transfer.manager.TransferManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.feng.skin.manager.loader.SkinManager;

/**
 * MainActivity
 * Created by D on 2017/4/28.
 */
public class MainActivity extends BaseFragmentActivity implements View.OnClickListener {
    @SuppressLint("StaticFieldLeak")
    private static FManger sFManger;

    TextView tv_song_name;
    TextView tv_singer;
    TextView tv_stroke;
    LinearLayout llyt_menu_exit;
    ImageView iv_play;
    FrameLayout flyt_menu;
    DrawerLayout dl_drawer;
    BadgeView bv_badge;

    private TransferDataObservable mTransferDataObservable;

    /**
     * GetManger
     */
    public static FManger getManger() {
        if (sFManger == null) {
            return new FManger(null, null);
        }
        return sFManger;
    }

    @Override
    public void onClick(View v) {
        if (QuickClick.isQuickClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_play:
                PlayActivity.openActivity(MainActivity.this);
                break;

            case R.id.flyt_menu:
                dl_drawer.openDrawer(GravityCompat.END);
                break;

            case R.id.llyt_menu_transfer:
                startActivity(new Intent(MainActivity.this, TransferActivity.class));
                break;

            case R.id.llyt_menu_sleep:
                startActivity(new Intent(MainActivity.this, SleepActivity.class));
                break;

            case R.id.llyt_menu_skin:
                startActivity(new Intent(MainActivity.this, SkinActivity.class));
                break;

            case R.id.llyt_menu_setting:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;

            case R.id.llyt_menu_exit:
                App.Companion.exit();
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.module_common_activity_main;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (App.toFinish(intent)) {
            finish();
        }
    }

    @Override
    protected void bindView() {
        tv_song_name = findViewById(R.id.tv_song_name);
        tv_singer = findViewById(R.id.tv_singer);
        tv_stroke = findViewById(R.id.tv_stroke);
        llyt_menu_exit = findViewById(R.id.llyt_menu_exit);
        iv_play = findViewById(R.id.iv_play);
        flyt_menu = findViewById(R.id.flyt_menu);
        dl_drawer = findViewById(R.id.dl_drawer);
        bv_badge = findViewById(R.id.bv_badge);

        ViewHelper.setOnClickListener(this, this,
                R.id.iv_play, R.id.flyt_menu,
                R.id.llyt_menu_transfer, R.id.llyt_menu_sleep,
                R.id.llyt_menu_skin, R.id.llyt_menu_setting,
                R.id.llyt_menu_exit);
    }

    @Override
    protected void init() {
        if (App.toFinish(getIntent())) {
            finish();
            return;
        }
        StatusBarCompat.setStatusBarColor(MainActivity.this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
        EventBus.getDefault().register(this);
        initMenu();
        initTransfer();
    }

    private void initMenu() {
        sFManger = new FManger(dl_drawer, getSupportFragmentManager());
        sFManger.replace(new MainFragment());
        dl_drawer.setScrimColor(getResources().getColor(R.color.lib_pub_color_trans));
        dl_drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View content = dl_drawer.getChildAt(0);
                float scale = 1 - slideOffset;
                float rightScale = 0.8f + scale * 0.2f;
                float leftScale = 1 - 0.3f * scale;

                drawerView.setScaleX(leftScale);
                drawerView.setScaleY(leftScale);

                content.setTranslationX(-drawerView.getMeasuredWidth() * slideOffset);
                content.setPivotX(content.getMeasuredWidth());
                content.setPivotY(content.getMeasuredHeight() / 2);
                content.invalidate();
                content.setScaleX(rightScale);
                content.setScaleY(rightScale);
            }
        });
    }

    private void initTransfer() {
        bv_badge.setVisibility(TransferManager.getInstance().getCount() > 0 ? View.VISIBLE : View.GONE);
        mTransferDataObservable = new TransferDataObservable() {
            @Override
            public void notifyDataSetChanged(int count) {
                ULog.d("dsiner --> TransferDataObservable: " + count);
                bv_badge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
            }
        };
        TransferManager.getInstance().register(mTransferDataObservable);
    }

//    @Override
//    public void onThemeUpdate() {
//        super.onThemeUpdate();
//        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
//    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_song_name.setText(MediaControl.getInstance(mContext).getSongName());
        tv_singer.setText(MediaControl.getInstance(mContext).getArtistName());
        Preferences p = Preferences.getInstance(getApplicationContext());
        flyt_menu.setVisibility(p.getIsShowMenuIcon() ? View.VISIBLE : View.GONE);
        tv_stroke.setText(p.getSignature());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onEventMainThread(MusicInfoEvent event) {
        if (event != null && tv_song_name != null && tv_singer != null) {
            tv_song_name.setText(event.songName);
            tv_singer.setText(event.artistName);
        }
    }

    @Override
    public void onBackPressed() {
        if (sFManger.getBackStackEntryCount() <= 1) {
            finish();
        } else {
            sFManger.popBackStack();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        TransferManager.getInstance().unregister(mTransferDataObservable);
        releaseResource();
        super.onDestroy();
    }

    private void releaseResource() {
        sFManger = null;
    }

    public static class FManger {
        private DrawerLayout drawer;
        private FragmentManager fragmentManager;

        FManger(DrawerLayout drawer, FragmentManager fragmentManager) {
            this.drawer = drawer;
            this.fragmentManager = fragmentManager;
        }

        public void setDrawerLockMode(int lockMode) {
            if (drawer == null) {
                return;
            }
            drawer.setDrawerLockMode(lockMode);
        }

        public void replace(Fragment fragment) {
            if (fragmentManager == null) {
                return;
            }
            fragmentManager.beginTransaction().replace(R.id.framement, fragment)
                    .addToBackStack(null).commitAllowingStateLoss();
        }

        public void popBackStack() {
            if (fragmentManager == null) {
                return;
            }
            fragmentManager.popBackStack();
        }

        int getBackStackEntryCount() {
            if (fragmentManager == null) {
                return 0;
            }
            return fragmentManager.getBackStackEntryCount();
        }
    }
}
