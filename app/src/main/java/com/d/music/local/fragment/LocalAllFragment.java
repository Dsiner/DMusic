package com.d.music.local.fragment;

import android.support.v4.app.Fragment;
import android.view.View;

import com.d.lib.common.component.repeatclick.ClickFast;
import com.d.music.MainActivity;
import com.d.music.R;
import com.d.music.local.activity.ScanActivity;
import com.d.music.component.greendao.db.AppDB;
import com.d.music.view.dialog.MenuDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.OnClick;

/**
 * 首页-本地歌曲
 * Created by D on 2017/4/29.
 */
public class LocalAllFragment extends AbstractLocalAllFragment {

    @OnClick({R.id.iv_title_left})
    public void onClickListener(View v) {
        if (ClickFast.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_title_left:
                MainActivity.getManger().popBackStack();
        }
    }

    @Override
    protected List<String> getTitles() {
        return Arrays.asList(getResources().getString(R.string.module_common_song),
                getResources().getString(R.string.module_common_singer),
                getResources().getString(R.string.module_common_album),
                getResources().getString(R.string.module_common_folder));
    }

    @Override
    protected List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new LMSongFragment());
        fragments.add(new LMSingerFragment());
        fragments.add(new LMAlbumFragment());
        fragments.add(new LMFolderFragment());
        return fragments;
    }

    @Override
    protected void init() {
        initTitle();
        super.init();
    }

    private void initTitle() {
        tlTitle.setText(R.id.tv_title_title, getResources().getString(R.string.module_common_local_song));
        tlTitle.setVisibility(R.id.iv_title_left, View.VISIBLE);
        tlTitle.setVisibility(R.id.iv_title_right, View.VISIBLE);
        tlTitle.setOnMenuListener(new MenuDialog.OnMenuListener() {
            @Override
            public void onRefresh(View v) {

            }

            @Override
            public void onClick(View v) {
                if (ClickFast.isFastDoubleClick()) {
                    return;
                }
                switch (v.getId()) {
                    case R.id.menu_scan:
                        ScanActivity.startActivity(getActivity(), AppDB.LOCAL_ALL_MUSIC);
                        break;
                }
            }
        });
    }
}
