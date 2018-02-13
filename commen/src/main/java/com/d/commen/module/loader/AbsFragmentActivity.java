package com.d.commen.module.loader;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.d.commen.R;
import com.d.commen.R2;
import com.d.commen.module.mvp.base.BaseFragmentActivity;
import com.d.commen.view.TitleLayout;
import com.d.commen.view.tab.ScrollTab;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 通用ViewPage-FragmentActivity
 * Created by D on 2017/7/19.
 */
public abstract class AbsFragmentActivity extends BaseFragmentActivity implements ViewPager.OnPageChangeListener {
    @BindView(R2.id.tl_title)
    public TitleLayout tlTitle;
    @BindView(R2.id.tv_title_right)
    public TextView tvTitleR;
    @BindView(R2.id.indicator)
    public ScrollTab indicator;
    @BindView(R2.id.vp_page)
    public ViewPager pager;

    protected List<String> titles;
    protected List<Fragment> fragments;
    protected Fragment curFragment;

    @OnClick({R2.id.iv_title_left, R2.id.tv_title_right})
    public void onAbsFragmentActivityClickListener(View v) {
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
    protected int getDSLayoutRes() {
        return 0;
    }

    @Override
    protected void init() {
        tvTitleR.setVisibility(View.GONE);
        titles = getTitles();
        fragments = getFraments();
        if (titles.size() != fragments.size()) {
            throw new RuntimeException("The size of titles are not equal size of fragments.");
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

    protected abstract List<Fragment> getFraments();
}
