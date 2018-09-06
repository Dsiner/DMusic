package com.d.music;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d.lib.common.component.mvp.base.BaseFragmentActivity;
import com.d.lib.common.component.repeatclick.ClickFast;
import com.d.music.common.preferences.Preferences;
import com.d.music.local.fragment.MainFragment;
import com.d.music.component.events.MusicInfoEvent;
import com.d.music.component.media.controler.MediaControler;
import com.d.music.play.activity.PlayActivity;
import com.d.music.setting.activity.SettingActivity;
import com.d.music.setting.activity.SkinActivity;
import com.d.music.setting.activity.SleepActivity;
import com.d.music.transfer.activity.TransferActivity;
import com.d.music.utils.StatusBarCompat;
import com.nineoldandroids.view.ViewHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import cn.feng.skin.manager.loader.SkinManager;

/**
 * MainActivity
 * Created by D on 2017/4/28.
 */
public class MainActivity extends BaseFragmentActivity {
    @SuppressLint("StaticFieldLeak")
    private static FManger fManger;

    @BindView(R.id.tv_song_name)
    TextView tvSongName;
    @BindView(R.id.tv_singer)
    TextView tvSinger;
    @BindView(R.id.tv_stroke)
    TextView tvStroke;
    @BindView(R.id.llyt_menu_exit)
    LinearLayout llytExit;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.flyt_menu)
    FrameLayout flytMenu;
    @BindView(R.id.dl_drawer)
    DrawerLayout dlDrawer;

    @OnClick({R.id.iv_play, R.id.flyt_menu, R.id.llyt_menu_transfer, R.id.llyt_menu_sleep,
            R.id.llyt_menu_skin, R.id.llyt_menu_setting, R.id.llyt_menu_exit})
    public void onClickListener(View v) {
        if (ClickFast.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_play:
                PlayActivity.openActivity(MainActivity.this);
                break;
            case R.id.flyt_menu:
                dlDrawer.openDrawer(Gravity.END);
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
                App.exit(getApplicationContext());
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
    protected void init() {
        if (App.toFinish(getIntent())) {
            finish();
            return;
        }
        StatusBarCompat.compat(MainActivity.this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
        EventBus.getDefault().register(this);
        fManger = new FManger(dlDrawer, getSupportFragmentManager());
        fManger.replace(new MainFragment());
        dlDrawer.setScrimColor(getResources().getColor(R.color.lib_pub_color_trans));
        dlDrawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View content = dlDrawer.getChildAt(0);
                float scale = 1 - slideOffset;
                float rightScale = 0.8f + scale * 0.2f;
                float leftScale = 1 - 0.3f * scale;

                ViewHelper.setScaleX(drawerView, leftScale);
                ViewHelper.setScaleY(drawerView, leftScale);

                ViewHelper.setTranslationX(content, -drawerView.getMeasuredWidth() * slideOffset);
                ViewHelper.setPivotX(content, content.getMeasuredWidth());
                ViewHelper.setPivotY(content, content.getMeasuredHeight() / 2);
                content.invalidate();
                ViewHelper.setScaleX(content, rightScale);
                ViewHelper.setScaleY(content, rightScale);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvSongName.setText(MediaControler.getIns(mContext).getSongName());
        tvSinger.setText(MediaControler.getIns(mContext).getArtistName());
        Preferences p = Preferences.getIns(getApplicationContext());
        flytMenu.setVisibility(p.getIsShowMenuIcon() ? View.VISIBLE : View.GONE);
        tvStroke.setText(p.getSignature());
    }

    @Override
    public void onThemeUpdate() {
        super.onThemeUpdate();
        // 沉浸式状态栏
        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onEventMainThread(MusicInfoEvent event) {
        if (event != null && tvSongName != null && tvSinger != null) {
            tvSongName.setText(event.songName);
            tvSinger.setText(event.artistName);
        }
    }

    @Override
    public void onBackPressed() {
        if (fManger.getBackStackEntryCount() <= 1) {
            finish();
        } else {
            fManger.popBackStack();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        releaseResource();
        super.onDestroy();
    }

    private void releaseResource() {
        fManger = null;
    }

    /**
     * GetManger
     */
    public static FManger getManger() {
        if (fManger == null) {
            return new FManger(null, null);
        }
        return fManger;
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
