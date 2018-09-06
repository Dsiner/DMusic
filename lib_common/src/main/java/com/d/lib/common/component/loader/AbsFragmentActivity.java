package com.d.lib.common.component.loader;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.d.lib.common.R;
import com.d.lib.common.component.mvp.base.BaseFragmentActivity;
import com.d.lib.common.utils.ViewHelper;
import com.d.lib.common.view.TitleLayout;
import com.d.lib.common.view.tab.ScrollTab;

import java.util.List;

/**
 * Auto-Pager - FragmentActivity
 * Created by D on 2017/7/19.
 */
public abstract class AbsFragmentActivity extends BaseFragmentActivity
        implements View.OnClickListener, ViewPager.OnPageChangeListener {
    protected TitleLayout tlTitle;
    protected ScrollTab indicator;
    protected ViewPager pager;

    protected List<String> titles;
    protected List<Fragment> fragments;
    protected Fragment curFragment;

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        if (resId == R.id.iv_title_left) {
            finish();
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.lib_pub_activity_abs_page;
    }

    @Override
    protected void bindView() {
        super.bindView();
        tlTitle = ViewHelper.findView(this, R.id.tl_title);
        indicator = ViewHelper.findView(this, R.id.indicator);
        pager = ViewHelper.findView(this, R.id.vp_page);

        ViewHelper.setOnClick(this, this, R.id.iv_title_left);
    }

    @Override
    protected void init() {
        titles = getTitles();
        fragments = getFragments();
        if (titles.size() != fragments.size()) {
            throw new RuntimeException("The size of titles is not equal size of fragments.");
        }
        curFragment = fragments.get(0);
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
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
