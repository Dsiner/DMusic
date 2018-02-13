package com.d.music.mvp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.d.commen.module.mvp.MvpBasePresenter;
import com.d.commen.module.mvp.MvpView;
import com.d.commen.module.mvp.base.BaseFragment;
import com.d.commen.module.repeatclick.ClickUtil;
import com.d.commen.view.tab.ScrollTab;
import com.d.music.MainActivity;
import com.d.music.R;
import com.d.music.module.greendao.db.MusicDB;
import com.d.music.mvp.activity.ScanActivity;
import com.d.music.view.TitleLayout;
import com.d.music.view.dialog.MenuDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 首页-本地歌曲
 * Created by D on 2017/4/29.
 */
public class LocalAllFragment extends BaseFragment<MvpBasePresenter> implements MvpView {
    @BindView(R.id.tl_title)
    TitleLayout tlTitle;
    @BindView(R.id.indicator)
    ScrollTab indicator;
    @BindView(R.id.vp_page)
    ViewPager pager;

    private List<String> titles = Arrays.asList("歌曲", "歌手", "专辑", "文件夹");

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
    protected int getLayoutRes() {
        return R.layout.fragment_local;
    }

    @Override
    public MvpBasePresenter getPresenter() {
        return new MvpBasePresenter(getActivity().getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    protected void init() {
        initTitle();
        LMSongFragment songFragment = new LMSongFragment();
        LMSingerFragment singerFragment = new LMSingerFragment();
        LMAlbumFragment albumFragment = new LMAlbumFragment();
        LMFolderFragment folderFragment = new LMFolderFragment();
        final List<Fragment> fragments = new ArrayList<>();
        fragments.add(songFragment);
        fragments.add(singerFragment);
        fragments.add(albumFragment);
        fragments.add(folderFragment);
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return fragments.get(arg0);
            }
        };
        pager.setOffscreenPageLimit(3);//高内存占用，持久化页面数
        pager.setAdapter(fragmentPagerAdapter);
        indicator.setTitles(titles);
        indicator.setViewPager(pager);
        indicator.setOnTabListener(new ScrollTab.OnTabListener() {
            @Override
            public void onChange(int position, View v) {
                pager.setCurrentItem(position, true);
            }
        });
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
