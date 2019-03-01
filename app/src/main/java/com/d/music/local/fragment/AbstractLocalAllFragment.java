package com.d.music.local.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.component.mvp.app.v4.BaseFragment;
import com.d.lib.common.view.tab.ScrollTab;
import com.d.music.R;
import com.d.music.view.TitleLayout;

import java.util.List;

import butterknife.BindView;

/**
 * 首页-本地歌曲
 * Created by D on 2017/4/29.
 */
public abstract class AbstractLocalAllFragment extends BaseFragment<MvpBasePresenter> implements MvpView, ViewPager.OnPageChangeListener {
    @BindView(R.id.tl_title)
    public TitleLayout tlTitle;
    @BindView(R.id.indicator)
    public ScrollTab indicator;
    @BindView(R.id.vp_page)
    public ViewPager pager;

    protected List<String> titles;
    protected List<Fragment> fragments;
    protected Fragment curFragment;

    @Override
    protected int getLayoutRes() {
        return R.layout.module_local_fragment_local;
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
        titles = getTitles();
        fragments = getFragments();
        if (titles.size() != fragments.size()) {
            throw new RuntimeException("The size of titles is not equal size of fragments.");
        }
        curFragment = fragments.get(0);
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
        pager.setOffscreenPageLimit(fragments.size() - 1);
        pager.setAdapter(fragmentPagerAdapter);
        pager.addOnPageChangeListener(this);
        indicator.setTitles(titles);
        indicator.setViewPager(pager);
        indicator.setOnTabListener(new ScrollTab.OnTabListener() {
            @Override
            public void onChange(int position, View v) {
                pager.setCurrentItem(position, true);
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position >= 0 && position < fragments.size()) {
            curFragment = fragments.get(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    protected abstract List<String> getTitles();

    protected abstract List<Fragment> getFragments();
}
