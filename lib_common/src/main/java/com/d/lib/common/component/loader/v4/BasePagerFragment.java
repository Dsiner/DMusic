package com.d.lib.common.component.loader.v4;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.d.lib.common.R;
import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.common.component.mvp.app.v4.BaseFragment;
import com.d.lib.common.util.ViewHelper;
import com.d.lib.common.widget.tab.ScrollTab;

import java.util.List;

/**
 * Auto-Pager - Fragment
 * Created by D on 2017/7/19.
 */
public abstract class BasePagerFragment<T extends MvpBasePresenter>
        extends BaseFragment<T>
        implements View.OnClickListener, ViewPager.OnPageChangeListener {

    protected ScrollTab mScrollTab;
    protected ViewPager mViewPager;

    protected List<String> mTitles;
    protected List<Fragment> mFragmentList;
    protected Fragment mCurFragment;

    @Override
    protected int getLayoutRes() {
        return R.layout.lib_pub_fragment_pager;
    }

    @Override
    protected void bindView(View rootView) {
        super.bindView(rootView);
        mScrollTab = ViewHelper.findViewById(rootView, R.id.indicator);
        mViewPager = ViewHelper.findViewById(rootView, R.id.vp_page);
    }

    @Override
    protected void init() {
        mTitles = getTitles();
        mFragmentList = getFragments();
        if (mTitles.size() != mFragmentList.size()) {
            throw new RuntimeException("The size of titles is not equal size of fragments.");
        }
        mCurFragment = mFragmentList.get(0);
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
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
