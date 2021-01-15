package com.d.music.local.fragment;

import android.view.View;

import androidx.fragment.app.Fragment;

import com.d.lib.common.component.quickclick.QuickClick;
import com.d.lib.common.util.ViewHelper;
import com.d.music.MainActivity;
import com.d.music.R;
import com.d.music.data.database.greendao.db.AppDatabase;
import com.d.music.local.activity.ScanActivity;
import com.d.music.widget.dialog.MenuDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 首页 - 本地歌曲
 * Created by D on 2017/4/29.
 */
public class LocalAllFragment extends AbstractLocalAllFragment
        implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        if (QuickClick.isQuickClick()) {
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
    protected void bindView(View rootView) {
        super.bindView(rootView);
        ViewHelper.setOnClickListener(rootView, this, R.id.iv_title_left);
    }

    @Override
    protected void init() {
        initTitle();
        super.init();
    }

    private void initTitle() {
        tl_title.setText(R.id.tv_title_title, getResources().getString(R.string.module_common_local_song));
        tl_title.setVisibility(R.id.iv_title_left, View.VISIBLE);
        tl_title.setVisibility(R.id.iv_title_right, View.VISIBLE);
        tl_title.setOnMenuListener(new MenuDialog.OnMenuListener() {
            @Override
            public void onRefresh(View v) {

            }

            @Override
            public void onClick(View v) {
                if (QuickClick.isQuickClick()) {
                    return;
                }
                switch (v.getId()) {
                    case R.id.menu_scan:
                        ScanActivity.openActivity(getActivity(), AppDatabase.LOCAL_ALL_MUSIC);
                        break;
                }
            }
        });
    }
}
