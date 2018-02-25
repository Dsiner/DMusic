package com.d.music;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.d.lib.common.module.mvp.MvpBasePresenter;
import com.d.lib.common.module.mvp.MvpView;
import com.d.lib.common.module.mvp.base.BaseActivity;
import com.d.lib.common.module.repeatclick.ClickUtil;
import com.d.music.module.service.MusicService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * WelcomeActivity
 * Created by D on 2017/6/16.
 */
public class WelcomeActivity extends BaseActivity<MvpBasePresenter> implements MvpView, View.OnClickListener {
    @BindView(R.id.vp_page)
    ViewPager page;
    @BindView(R.id.iv_dot0)
    ImageView ivDot0;
    @BindView(R.id.iv_dot1)
    ImageView ivDot1;
    @BindView(R.id.iv_dot2)
    ImageView ivDot2;

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_start:
                //启动音乐主界面
                gotoMain();
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_welcome;
    }

    @Override
    public MvpBasePresenter<MvpView> getPresenter() {
        return new MvpBasePresenter<>(getApplication());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    protected void init() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view0 = inflater.inflate(R.layout.welcome_page0, null);
        View view1 = inflater.inflate(R.layout.welcome_page1, null);
        View view2 = inflater.inflate(R.layout.welcome_page2, null);
        view2.findViewById(R.id.btn_start).setOnClickListener(this);

        final List<View> views = new ArrayList<View>();
        views.add(view0);
        views.add(view1);
        views.add(view2);

        PagerAdapter pagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public void destroyItem(View container, int position, Object object) {
                ((ViewPager) container).removeView(views.get(position));
            }

            @Override
            public Object instantiateItem(View container, int position) {
                ((ViewPager) container).addView(views.get(position));
                return views.get(position);
            }
        };
        page.setAdapter(pagerAdapter);
        page.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                invalidateDots(position);
            }
        });
    }

    /**
     * 刷新指示器
     */
    private void invalidateDots(int position) {
        switch (position) {
            case 0:
                ivDot1.setImageDrawable(getResources().getDrawable(R.drawable.dot_wel));
                ivDot0.setImageDrawable(getResources().getDrawable(R.drawable.dot_wel_cover));
                break;
            case 1:
                ivDot0.setImageDrawable(getResources().getDrawable(R.drawable.dot_wel));
                ivDot2.setImageDrawable(getResources().getDrawable(R.drawable.dot_wel));
                ivDot1.setImageDrawable(getResources().getDrawable(R.drawable.dot_wel_cover));
                break;
            case 2:
                ivDot1.setImageDrawable(getResources().getDrawable(R.drawable.dot_wel));
                ivDot2.setImageDrawable(getResources().getDrawable(R.drawable.dot_wel_cover));
                break;
        }
    }

    private void gotoMain() {
        MusicService.startService(getApplicationContext());//开启service服务
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
