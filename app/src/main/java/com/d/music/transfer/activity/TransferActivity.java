package com.d.music.transfer.activity;

import android.view.View;

import androidx.fragment.app.Fragment;

import com.d.lib.common.component.loader.v4.BasePagerFragmentActivity;
import com.d.lib.common.component.statusbarcompat.StatusBarCompat;
import com.d.lib.common.widget.TitleLayout;
import com.d.music.R;
import com.d.music.transfer.fragment.TransferFragment;
import com.d.music.transfer.manager.TransferManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.feng.skin.manager.loader.SkinManager;

/**
 * TransferActivity
 * Created by D on 2018/8/11.
 */
public class TransferActivity extends BasePagerFragmentActivity {
    TitleLayout tl_title;

    @Override
    protected List<String> getTitles() {
        return Arrays.asList(getResources().getString(R.string.module_common_song),
                getResources().getString(R.string.module_common_online_mv));
    }

    @Override
    protected List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(TransferFragment.getFragment(TransferFragment.TYPE_SONG));
        fragments.add(TransferFragment.getFragment(TransferFragment.TYPE_MV));
        return fragments;
    }

    @Override
    protected void bindView() {
        super.bindView();
        tl_title = findViewById(R.id.tl_title);
    }

    @Override
    protected void init() {
        super.init();
        StatusBarCompat.setStatusBarColor(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
        tl_title.setText(R.id.tv_title_title, getResources().getString(R.string.module_common_transfer));
        final int countSongDownloading = TransferManager.getInstance().optSong().pipe().lists().get(0).size();
        final int countMVDownloading = TransferManager.getInstance().optMV().pipe().lists().get(0).size();
        setTabNumber(TransferFragment.TYPE_SONG,
                countSongDownloading > 0 ? "" + countSongDownloading : "",
                countSongDownloading > 0 ? View.VISIBLE : View.GONE);
        setTabNumber(TransferFragment.TYPE_MV,
                countMVDownloading > 0 ? "" + countMVDownloading : "",
                countMVDownloading > 0 ? View.VISIBLE : View.GONE);
    }

    public void setTabNumber(int position, String text, int visibility) {
        if (mScrollTab == null) {
            return;
        }
        mScrollTab.setNumber(position, text, visibility);
    }
}
