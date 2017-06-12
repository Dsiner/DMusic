package com.d.dmusic;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.d.commen.base.BaseActivity;
import com.d.commen.mvp.MvpBasePresenter;
import com.d.commen.mvp.MvpView;
import com.d.dmusic.commen.Preferences;
import com.d.dmusic.module.repeatclick.ClickUtil;
import com.d.dmusic.module.service.MusicService;
import com.d.dmusic.mvp.activity.PlayActivity;
import com.d.dmusic.utils.StatusBarCompat;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * SsssActivity
 * Created by D on 2017/4/28.
 */
public class SplashActivity extends BaseActivity<MvpBasePresenter> implements MvpView, ViewPager.OnPageChangeListener, View.OnClickListener {
    @Bind(R.id.vp_page)
    ViewPager page;
    @Bind(R.id.iv_dot0)
    ImageView ivDot0;
    @Bind(R.id.iv_dot1)
    ImageView ivDot1;
    @Bind(R.id.iv_dot2)
    ImageView ivDot2;
    @Bind(R.id.iv_splash)
    ImageView ivSplash;

    private Context context;
    private WeakHandler handler = new WeakHandler(this);
    private Preferences p;
    private int delayTime = 2500;//splash时间
    private boolean isBackPressed;

    static class WeakHandler extends Handler {
        WeakReference<SplashActivity> weakReference;

        public WeakHandler(SplashActivity activity) {
            weakReference = new WeakReference<SplashActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SplashActivity theActivity = weakReference.get();
            if (theActivity != null && !theActivity.isFinishing() && !theActivity.isBackPressed) {
                switch (msg.what) {
                    case 1:
                        MusicService.startService(theActivity.getApplicationContext());
                        theActivity.startActivity(new Intent(theActivity, MainActivity.class));
                        theActivity.finish();
                        break;
                    case 2:
                        MusicService.startService(theActivity.getApplicationContext());
                        theActivity.startActivity(new Intent(theActivity, PlayActivity.class));
                        theActivity.finish();
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_splash;
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
        p = Preferences.getInstance(getApplicationContext());

        boolean isFirst = p.getIsFirst();
        int playerMode = p.getPlayerMode();

        if (!isFirst) {
            switch (playerMode) {
                case 0:
                    if (MusicService.isRunning()) {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        initSplash();
                        handler.sendEmptyMessageDelayed(1, delayTime);
                    }
                    break;
                case 1:
                    if (MusicService.isRunning()) {
                        startActivity(new Intent(this, PlayActivity.class));
                        finish();
                    } else {
                        initSplash();
                        handler.sendEmptyMessageDelayed(2, delayTime);
                    }
                    break;
            }
        } else {
            initWelcome();
        }
    }

    private void initSplash() {
        ivSplash.setVisibility(View.VISIBLE);
        StatusBarCompat.compat(this, 0x00000000);//沉浸式状态栏
    }

    private void initWelcome() {
        ivSplash.setVisibility(View.GONE);
        StatusBarCompat.compat(this, 0x00000000);//沉浸式状态栏
        LayoutInflater inflater = LayoutInflater.from(this);
        View view0 = inflater.inflate(R.layout.welcome_page0, null);
        View view1 = inflater.inflate(R.layout.welcome_page1, null);
        View view2 = inflater.inflate(R.layout.welcome_page2, null);
//        view2.findViewById(R.id.btn_start).setOnClickListener(this);

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
//        switch (v.getId()) {
//            case R.id.btn_start:
//                //启动音乐主界面
//                p.putIsFirst(false);
//                MusicService.startService(getApplicationContext());//开启service服务
//                startActivity(new Intent(this, MainActivity.class));
//                finish();
//                break;
//        }
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

    @Override
    public void onBackPressed() {
        isBackPressed = true;
        super.onBackPressed();
    }
}
