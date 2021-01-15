package com.d.music.local.fragment;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.component.mvp.app.v4.BaseFragment;
import com.d.lib.common.widget.tab.ScrollTab;
import com.d.music.R;
import com.d.music.widget.TitleLayout;

import java.util.List;

/**
 * 首页-本地歌曲
 * Created by D on 2017/4/29.
 */
public abstract class AbstractLocalAllFragment extends BaseFragment<MvpBasePresenter>
        implements MvpView, ViewPager.OnPageChangeListener {
    public TitleLayout tl_title;
    public ScrollTab indicator;
    public ViewPager vp_page;

    protected List<String> mTitles;
    protected List<Fragment> mFragments;
    protected Fragment mCurFragment;

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
    protected void bindView(View rootView) {
        super.bindView(rootView);
        tl_title = rootView.findViewById(R.id.tl_title);
        indicator = rootView.findViewById(R.id.indicator);
        vp_page = rootView.findViewById(R.id.vp_page);
    }

    @Override
    protected void init() {
        mTitles = getTitles();
        mFragments = getFragments();
        if (mTitles.size() != mFragments.size()) {
            throw new RuntimeException("The size of titles is not equal size of fragments.");
        }
        mCurFragment = mFragments.get(0);
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mFragments.get(arg0);
            }
        };
        vp_page.setOffscreenPageLimit(mFragments.size() - 1);
        vp_page.setAdapter(fragmentPagerAdapter);
        vp_page.addOnPageChangeListener(this);
        indicator.setTitles(mTitles);
        indicator.setViewPager(vp_page);
        indicator.setOnTabListener(new ScrollTab.OnTabListener() {
            @Override
            public void onChange(int position, View v) {
                vp_page.setCurrentItem(position, true);
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position >= 0 && position < mFragments.size()) {
            mCurFragment = mFragments.get(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    protected abstract List<String> getTitles();

    protected abstract List<Fragment> getFragments();
}
