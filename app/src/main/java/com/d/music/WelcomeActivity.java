package com.d.music;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.d.commen.module.mvp.base.BaseActivity;
import com.d.commen.module.mvp.MvpBasePresenter;
import com.d.commen.module.mvp.MvpView;
import com.d.commen.module.repeatclick.ClickUtil;
import com.d.music.module.service.MusicService;
import com.d.commen.utils.Util;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * WelcomeActivity
 * Created by D on 2017/6/16.
 */
public class WelcomeActivity extends BaseActivity<MvpBasePresenter> implements MvpView, ViewPager.OnPageChangeListener, View.OnClickListener {
    @Bind(R.id.vp_page)
    ViewPager page;
    @Bind(R.id.iv_dot0)
    ImageView ivDot0;
    @Bind(R.id.iv_dot1)
    ImageView ivDot1;
    @Bind(R.id.iv_dot2)
    ImageView ivDot2;

    private Context context;

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
        context = this;
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
        page.addOnPageChangeListener(this);
    }

    /**
     * 刷新指示器
     */
    private void refreshDotsState(int position) {
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

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_start:
                //启动音乐主界面
                ddc();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        refreshDotsState(position);
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            RxPermissions rxPermissions = new RxPermissions((Activity) context);
            rxPermissions.requestEach(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Permission>() {
                        @Override
                        public void accept(@NonNull Permission permission) throws Exception {
                            if (isFinishing()) {
                                return;
                            }
                            if (permission.granted) {
                                // `permission.name` is granted !
                                ddc();
                            } else if (permission.shouldShowRequestPermissionRationale) {
                                // Denied permission without ask never again
                                Util.toast(context.getApplicationContext(), "Denied permission!");
                                ddc();
                            } else {
                                // Denied permission with ask never again
                                // Need to go to the settings
                                Util.toast(context.getApplicationContext(), "Denied permission with ask never again!");
                                ddc();
                            }
                        }
                    });
        } else {
            ddc();
        }
    }

    private void ddc() {
        MusicService.startService(getApplicationContext());//开启service服务
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
