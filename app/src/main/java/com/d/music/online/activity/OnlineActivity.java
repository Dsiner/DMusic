package com.d.music.online.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;

import com.d.lib.common.component.loader.v4.AbsFragmentActivity;
import com.d.lib.common.component.repeatclick.ClickFast;
import com.d.lib.common.view.TitleLayout;
import com.d.music.R;
import com.d.music.online.fragment.BillFragment;
import com.d.music.online.fragment.MVFragment;
import com.d.music.online.fragment.RadioFragment;
import com.d.music.online.fragment.SingerFragment;
import com.d.music.play.activity.SearchActivity;
import com.d.music.utils.StatusBarCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.feng.skin.manager.loader.SkinManager;

/**
 * OnlineActivity
 * Created by D on 2018/8/11.
 */
public class OnlineActivity extends AbsFragmentActivity {
    @BindView(R.id.tl_title)
    TitleLayout tlTitle;

    @OnClick({R.id.iv_title_right})
    public void onClickListener(View v) {
        if (ClickFast.isFastDoubleClick()) {
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
    protected void init() {
        super.init();
        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
        tlTitle.setText(R.id.tv_title_title, getResources().getString(R.string.module_common_music_hall));
    }
}
