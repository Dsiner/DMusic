package com.d.music.mvp.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.d.commen.base.BaseActivity;
import com.d.commen.mvp.MvpBasePresenter;
import com.d.commen.mvp.MvpView;
import com.d.music.R;
import com.d.music.App;
import com.d.music.commen.Preferences;
import com.d.music.module.events.SleepFinishEvent;
import com.d.music.module.repeatclick.ClickUtil;
import com.d.music.module.service.MusicService;
import com.d.music.mvp.adapter.TimingAdapter;
import com.d.music.mvp.model.RadioModel;
import com.d.music.utils.StatusBarCompat;
import com.d.music.view.TitleLayout;
import com.d.lib.xrv.LRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.feng.skin.manager.loader.SkinManager;

/**
 * PlayerModeActivity
 * Created by D on 2017/6/13.
 */
public class SleepActivity extends BaseActivity<MvpBasePresenter> implements MvpView, TimingAdapter.OnChangeListener {
    @Bind(R.id.tl_title)
    TitleLayout tlTitle;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.iv_check)
    ImageView ivCheck;
    @Bind(R.id.lrv_list)
    LRecyclerView lrvList;

    private Preferences p;
    private TimingAdapter adapter;
    private List<RadioModel> datas;
    private int index;

    @OnClick({R.id.iv_title_left, R.id.tv_title_right, R.id.rlyt_first})
    public void onClickListener(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_title_left:
                finish();
                break;
            case R.id.tv_title_right:
                if (index < 0 || index > 6) {
                    return;
                }
                MusicService.startService(getApplicationContext());
                long time = 0;
                switch (index) {
                    case 0:
                        time = 0;
                        break;
                    case 1:
                        time = 10 * 60 * 1000;
                        break;
                    case 2:
                        time = 20 * 60 * 1000;
                        break;
                    case 3:
                        time = 30 * 60 * 1000;
                        break;
                    case 4:
                        time = 60 * 60 * 1000;
                        break;
                    case 5:
                        time = 90 * 60 * 1000;
                        break;
                }
                MusicService.timing(getApplicationContext(), false, 0);
                MusicService.timing(getApplicationContext(), time > 0, time);
                p.putSleepType(index);
                finish();
                break;
            case R.id.rlyt_first:
                if (index == 0) {
                    return;
                }
                ivCheck.setVisibility(View.VISIBLE);
                adapter.setIndex(-1);
                if (index - 1 >= 0 && index - 1 < adapter.getDatas().size()) {
                    adapter.getDatas().get(index - 1).isChecked = false;
                }
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_sleep;
    }

    @Override
    public MvpBasePresenter getPresenter() {
        return new MvpBasePresenter(getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (App.toFinish(intent)) {
            finish();
        }
    }

    @Override
    protected void init() {
        if (App.toFinish(getIntent())) {
            finish();
            return;
        }
        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.color_main));//沉浸式状态栏
        EventBus.getDefault().register(this);
        p = Preferences.getInstance(getApplicationContext());
        index = p.getSleepType();
        tvContent.setText("关闭");
        ivCheck.setVisibility(index == 0 ? View.VISIBLE : View.GONE);
        adapter = new TimingAdapter(this, getDatas(), R.layout.adapter_radio);
        adapter.setIndex(index - 1);
        adapter.setOnChangeListener(this);
        lrvList.showAsList();
        lrvList.setAdapter(adapter);
    }

    private List<RadioModel> getDatas() {
        datas = new ArrayList<>();
        RadioModel model0 = new RadioModel();
        model0.content = "10分钟";
        model0.isChecked = index == 1;

        RadioModel model1 = new RadioModel();
        model1.content = "20分钟";
        model1.isChecked = index == 2;

        RadioModel model2 = new RadioModel();
        model2.content = "30分钟";
        model2.isChecked = index == 3;

        RadioModel model3 = new RadioModel();
        model3.content = "1小时";
        model3.isChecked = index == 4;

        RadioModel model4 = new RadioModel();
        model4.content = "1.5小时";
        model4.isChecked = index == 5;

        RadioModel model5 = new RadioModel();
        model5.content = "自定义";
        model5.isChecked = index == 6;

        datas.add(model0);
        datas.add(model1);
        datas.add(model2);
        datas.add(model3);
        datas.add(model4);
        datas.add(model5);
        return datas;
    }

    @Override
    public void onChange(int index) {
        this.index = index + 1;
        ivCheck.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SleepFinishEvent event) {
        if (event == null || isFinishing()) {
            return;
        }
        finish();
    }

    @Override
    public void onThemeUpdate() {
        super.onThemeUpdate();
        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.color_main));//沉浸式状态栏
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
