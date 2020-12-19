package com.d.music;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.component.mvp.app.BaseActivity;
import com.d.lib.common.component.quickclick.QuickClick;
import com.d.music.component.service.NotificationService;

import java.util.ArrayList;
import java.util.List;

/**
 * WelcomeActivity
 * Created by D on 2017/6/16.
 */
public class WelcomeActivity extends BaseActivity<MvpBasePresenter>
        implements MvpView, View.OnClickListener {

    ViewPager vp_page;
    ImageView iv_dot0;
    ImageView iv_dot1;
    ImageView iv_dot2;

    @Override
    public void onClick(View v) {
        if (QuickClick.isQuickClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_start:
                // 启动音乐主界面
                gotoMain();
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.module_common_activity_welcome;
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
    protected void bindView() {
        super.bindView();
        vp_page = findViewById(R.id.vp_page);
        iv_dot0 = findViewById(R.id.iv_dot0);
        iv_dot1 = findViewById(R.id.iv_dot1);
        iv_dot2 = findViewById(R.id.iv_dot2);
    }

    @Override
    protected void init() {
        final LayoutInflater inflater = LayoutInflater.from(this);
        final View page0 = inflater.inflate(R.layout.module_common_welcome_page0, null);
        final View page1 = inflater.inflate(R.layout.module_common_welcome_page1, null);
        final View page2 = inflater.inflate(R.layout.module_common_welcome_page2, null);

        page2.findViewById(R.id.btn_start).setOnClickListener(this);

        final List<View> pages = new ArrayList<>();
        pages.add(page0);
        pages.add(page1);
        pages.add(page2);

        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return pages.size();
            }

            @Override
            public void destroyItem(View container, int position, Object object) {
                ((ViewPager) container).removeView(pages.get(position));
            }

            @Override
            public Object instantiateItem(View container, int position) {
                ((ViewPager) container).addView(pages.get(position));
                return pages.get(position);
            }
        };
        vp_page.setAdapter(pagerAdapter);
        vp_page.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
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
                iv_dot1.setImageDrawable(getResources().getDrawable(R.drawable.module_common_dot_wel));
                iv_dot0.setImageDrawable(getResources().getDrawable(R.drawable.module_common_dot_wel_cover));
                break;

            case 1:
                iv_dot0.setImageDrawable(getResources().getDrawable(R.drawable.module_common_dot_wel));
                iv_dot2.setImageDrawable(getResources().getDrawable(R.drawable.module_common_dot_wel));
                iv_dot1.setImageDrawable(getResources().getDrawable(R.drawable.module_common_dot_wel_cover));
                break;

            case 2:
                iv_dot1.setImageDrawable(getResources().getDrawable(R.drawable.module_common_dot_wel));
                iv_dot2.setImageDrawable(getResources().getDrawable(R.drawable.module_common_dot_wel_cover));
                break;
        }
    }

    private void gotoMain() {
        // 开启Service服务
        NotificationService.startService(getApplicationContext());
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
