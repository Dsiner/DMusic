package com.d.music.local.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;

import com.d.lib.common.module.repeatclick.ClickUtil;
import com.d.music.MainActivity;
import com.d.music.R;
import com.d.music.local.activity.ScanActivity;
import com.d.music.module.greendao.db.MusicDB;
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
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_title_left:
                MainActivity.popBackStack();
        }
    }

    @Override
    protected List<String> getTitles() {
        return Arrays.asList("歌曲", "歌手", "专辑", "文件夹");
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
        tlTitle.setText(R.id.tv_title_title, "本地歌曲");
        tlTitle.setVisibility(R.id.iv_title_left, View.VISIBLE);
        tlTitle.setVisibility(R.id.iv_title_right, View.VISIBLE);
        tlTitle.setOnMenuListener(new MenuDialog.OnMenuListener() {
            @Override
            public void onRefresh(View v) {

            }

            @Override
            public void onClick(View v) {
                if (ClickUtil.isFastDoubleClick()) {
                    return;
                }
                switch (v.getId()) {
                    case R.id.menu_scan:
                        Activity activity = getActivity();
                        Intent intent = new Intent(activity, ScanActivity.class);
                        intent.putExtra("type", MusicDB.LOCAL_ALL_MUSIC);
                        activity.startActivity(intent);
                        break;
                }
            }
        });
    }
}
