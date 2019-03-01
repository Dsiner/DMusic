package com.d.lib.common.component.loader.v7;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.d.lib.common.R;
import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.common.component.mvp.app.v7.BaseAppCompatActivity;
import com.d.lib.common.utils.ViewHelper;
import com.d.lib.common.view.TitleLayout;
import com.d.lib.common.view.tab.ScrollTab;

import java.util.List;

/**
 * Auto-Pager - AbsAppCompatActivity
 * Created by D on 2017/7/19.
 */
public abstract class AbsAppCompatActivity<T extends MvpBasePresenter>
        extends BaseAppCompatActivity<T>
        implements View.OnClickListener, ViewPager.OnPageChangeListener {

    protected TitleLayout mTlTitle;
    protected ScrollTab mScrollTab;
    protected ViewPager mViewPager;

    protected List<String> mTitles;
    protected List<Fragment> mFragmentList;
    protected Fragment mCurFragment;

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
        mTlTitle = ViewHelper.findView(this, R.id.tl_title);
        mScrollTab = ViewHelper.findView(this, R.id.indicator);
        mViewPager = ViewHelper.findView(this, R.id.vp_page);

        ViewHelper.setOnClick(this, this, R.id.iv_title_left);
    }

    @Override
    protected void init() {
        mTitles = getTitles();
        mFragmentList = getFragments();
        if (mTitles.size() != mFragmentList.size()) {
            throw new RuntimeException("The size of titles is not equal size of fragments.");
        }
        mCurFragment = mFragmentList.get(0);
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragmentList.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mFragmentList.get(arg0);
            }
        };
        mViewPager.setOffscreenPageLimit(mFragmentList.size() - 1);
        mViewPager.setAdapter(fragmentPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        mScrollTab.setTitles(mTitles);
        mScrollTab.setViewPager(mViewPager);
        mScrollTab.setOnTabListener(new ScrollTab.OnTabListener() {
            @Override
            public void onChange(int position, View v) {
                mViewPager.setCurrentItem(position, true);
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position >= 0 && position < mFragmentList.size()) {
            mCurFragment = mFragmentList.get(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    protected abstract List<String> getTitles();

    protected abstract List<Fragment> getFragments();
}
