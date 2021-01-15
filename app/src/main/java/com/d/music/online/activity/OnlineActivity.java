package com.d.music.online.activity;

import android.content.Intent;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.d.lib.common.component.loader.v4.BasePagerFragmentActivity;
import com.d.lib.common.component.quickclick.QuickClick;
import com.d.lib.common.component.statusbarcompat.StatusBarCompat;
import com.d.lib.common.util.ViewHelper;
import com.d.lib.common.widget.TitleLayout;
import com.d.music.R;
import com.d.music.online.fragment.BillFragment;
import com.d.music.online.fragment.MVFragment;
import com.d.music.online.fragment.RadioFragment;
import com.d.music.online.fragment.SingerFragment;
import com.d.music.play.activity.SearchActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.feng.skin.manager.loader.SkinManager;

/**
 * OnlineActivity
 * Created by D on 2018/8/11.
 */
public class OnlineActivity extends BasePagerFragmentActivity
        implements View.OnClickListener {

    TitleLayout tl_title;

    @Override
    public void onClick(View v) {
        if (QuickClick.isQuickClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_title_right:
                startActivity(new Intent(mActivity, SearchActivity.class));
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.module_online_activity_online;
    }

    @Override
    protected List<String> getTitles() {
        return Arrays.asList(getResources().getString(R.string.module_common_online_singer),
                getResources().getString(R.string.module_common_online_rank),
                getResources().getString(R.string.module_common_online_radio),
                getResources().getString(R.string.module_common_online_mv));
    }

    @Override
    protected List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new SingerFragment());
        fragments.add(new BillFragment());
        fragments.add(new RadioFragment());
        fragments.add(new MVFragment());
        return fragments;
    }

    @Override
    protected void bindView() {
        super.bindView();
        tl_title = findViewById(R.id.tl_title);

        ViewHelper.setOnClickListener(this, this,
                R.id.iv_title_right);
    }

    @Override
    protected void init() {
        super.init();
        StatusBarCompat.setStatusBarColor(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
        tl_title.setText(R.id.tv_title_title, getResources().getString(R.string.module_common_music_hall));
    }
}
