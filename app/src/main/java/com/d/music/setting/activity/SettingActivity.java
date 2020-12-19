package com.d.music.setting.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.component.mvp.app.BaseActivity;
import com.d.lib.common.component.quickclick.QuickClick;
import com.d.lib.common.component.statusbarcompat.StatusBarCompat;
import com.d.lib.common.util.ViewHelper;
import com.d.lib.common.widget.RowLayout;
import com.d.lib.common.widget.TitleLayout;
import com.d.music.R;
import com.d.music.data.preferences.Preferences;

import cn.feng.skin.manager.loader.SkinManager;

/**
 * SettingActivity
 * Created by D on 2017/6/13.
 */
public class SettingActivity extends BaseActivity<MvpBasePresenter>
        implements MvpView, View.OnClickListener, RowLayout.OnToggleListener {
    TitleLayout tl_title;
    RowLayout rl_mode_auto_play;
    RowLayout rl_mode_sub;
    RowLayout rl_mode_add;
    RowLayout rl_mode_rotate;
    RowLayout rl_mode_shake;
    RowLayout rl_mode_menu;

    private Preferences mPreferences;

    @Override
    public void onClick(View v) {
        if (QuickClick.isQuickClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_title_left:
                finish();
                break;

            case R.id.rl_koan:
                startActivity(new Intent(SettingActivity.this, KoanActivity.class));
                break;

            case R.id.rl_skin:
                startActivity(new Intent(SettingActivity.this, SkinActivity.class));
                break;

            case R.id.rl_sleep:
                startActivity(new Intent(SettingActivity.this, SleepActivity.class));
                break;

            case R.id.rl_player_mode:
                startActivity(new Intent(SettingActivity.this, ModeActivity.class));
                break;

            case R.id.rl_about:
                startActivity(new Intent(SettingActivity.this, AboutActivity.class));
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.module_setting_activity_setting;
    }

    @Override
    public MvpBasePresenter getPresenter() {
        return new MvpBasePresenter(getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    protected void bindView() {
        super.bindView();
        tl_title = findViewById(R.id.tl_title);
        rl_mode_auto_play = findViewById(R.id.rl_mode_auto_play);
        rl_mode_sub = findViewById(R.id.rl_mode_sub);
        rl_mode_add = findViewById(R.id.rl_mode_add);
        rl_mode_rotate = findViewById(R.id.rl_mode_rotate);
        rl_mode_shake = findViewById(R.id.rl_mode_shake);
        rl_mode_menu = findViewById(R.id.rl_mode_menu);

        ViewHelper.setOnClickListener(this, this,
                R.id.iv_title_left,
                R.id.rl_koan, R.id.rl_skin,
                R.id.rl_sleep, R.id.rl_player_mode,
                R.id.rl_about);
    }

    @Override
    protected void init() {
        StatusBarCompat.setStatusBarColor(SettingActivity.this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
        initTitle();
        mPreferences = Preferences.getInstance(SettingActivity.this);
        rl_mode_auto_play.setOpen(mPreferences.getIsAutoPlay());
        rl_mode_sub.setOpen(mPreferences.getIsSubPull());
        rl_mode_add.setOpen(mPreferences.getIsShowAdd());
        rl_mode_rotate.setOpen(mPreferences.getIsAlbumRotate());
        rl_mode_shake.setOpen(mPreferences.getIsShake());
        rl_mode_menu.setOpen(mPreferences.getIsShowMenuIcon());

        rl_mode_auto_play.setOnToggleListener(this);
        rl_mode_sub.setOnToggleListener(this);
        rl_mode_add.setOnToggleListener(this);
        rl_mode_rotate.setOnToggleListener(this);
        rl_mode_shake.setOnToggleListener(this);
        rl_mode_menu.setOnToggleListener(this);
    }

    private void initTitle() {
        TextView tvTitle = (TextView) tl_title.findViewById(R.id.tv_title_title);
        tvTitle.setText(getResources().getString(R.string.module_common_setting));
        tl_title.setVisibility(R.id.iv_title_right, View.GONE);
    }

    @Override
    public void onToggle(View v, boolean isOpen) {
        switch (v.getId()) {
            case R.id.rl_mode_auto_play:
                mPreferences.putIsAutoPlay(isOpen);
                break;
            case R.id.rl_mode_sub:
                mPreferences.putIsSubPull(isOpen);
                break;
            case R.id.rl_mode_add:
                mPreferences.putIsShowAdd(isOpen);
                break;
            case R.id.rl_mode_rotate:
                mPreferences.putIsAlbumRotate(isOpen);
                break;
            case R.id.rl_mode_shake:
                mPreferences.putIsShake(isOpen);
                break;
            case R.id.rl_mode_menu:
                mPreferences.putIsShowMenuIcon(isOpen);
                break;
        }
    }

//    @Override
//    public void onThemeUpdate() {
//        super.onThemeUpdate();
//        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
//    }
}
