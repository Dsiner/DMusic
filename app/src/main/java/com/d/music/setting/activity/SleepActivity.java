package com.d.music.setting.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.component.mvp.app.BaseActivity;
import com.d.lib.common.component.quickclick.QuickClick;
import com.d.lib.common.component.statusbarcompat.StatusBarCompat;
import com.d.lib.common.util.ViewHelper;
import com.d.music.App;
import com.d.music.R;
import com.d.music.component.service.MusicService;
import com.d.music.data.preferences.Preferences;
import com.d.music.setting.adapter.TimingAdapter;
import com.d.music.setting.model.RadioModel;

import java.util.ArrayList;
import java.util.List;

import cn.feng.skin.manager.loader.SkinManager;

/**
 * SleepActivity
 * Created by D on 2017/6/13.
 */
public class SleepActivity extends BaseActivity<MvpBasePresenter>
        implements MvpView, View.OnClickListener, TimingAdapter.OnChangeListener {
    TextView tv_content;
    ImageView iv_check;
    RecyclerView rv_list;

    private Preferences mPreferences;
    private TimingAdapter mTimingAdapter;
    private int mSleepType;

    @Override
    public void onClick(View v) {
        if (QuickClick.isQuickClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_title_left:
                finish();
                break;

            case R.id.tv_title_right:
                if (mSleepType < 0 || mSleepType > 6) {
                    return;
                }
                MusicService.startService(getApplicationContext());
                long time = 0;
                switch (mSleepType) {
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
                mPreferences.putSleepType(mSleepType);
                finish();
                break;

            case R.id.rlyt_first:
                if (mSleepType == 0) {
                    return;
                }
                iv_check.setVisibility(View.VISIBLE);
                mTimingAdapter.setIndex(-1);
                if (mSleepType - 1 >= 0 && mSleepType - 1 < mTimingAdapter.getDatas().size()) {
                    mTimingAdapter.getDatas().get(mSleepType - 1).isChecked = false;
                }
                mTimingAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.module_setting_activity_sleep;
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
    protected void bindView() {
        super.bindView();
        tv_content = findViewById(R.id.tv_content);
        iv_check = findViewById(R.id.iv_check);
        rv_list = findViewById(R.id.rv_list);

        ViewHelper.setOnClickListener(this, this,
                R.id.iv_title_left, R.id.tv_title_right,
                R.id.rlyt_first);
    }

    @Override
    protected void init() {
        if (App.toFinish(getIntent())) {
            finish();
            return;
        }
        StatusBarCompat.setStatusBarColor(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
        mPreferences = Preferences.getInstance(getApplicationContext());
        mSleepType = mPreferences.getSleepType();
        tv_content.setText(getResources().getString(R.string.module_common_close));
        iv_check.setVisibility(mSleepType == 0 ? View.VISIBLE : View.GONE);
        mTimingAdapter = new TimingAdapter(this, getDatas(), R.layout.module_setting_adapter_radio);
        mTimingAdapter.setIndex(mSleepType - 1);
        mTimingAdapter.setOnChangeListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_list.setLayoutManager(layoutManager);
        rv_list.setAdapter(mTimingAdapter);
    }

    private List<RadioModel> getDatas() {
        String[] values = getResources().getStringArray(R.array.module_setting_timing_values);
        List<RadioModel> datas = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            RadioModel model = new RadioModel();
            model.content = values[i];
            model.isChecked = (mSleepType == i + 1);
            datas.add(model);
        }
        return datas;
    }

    @Override
    public void onChange(int index) {
        mSleepType = index + 1;
        iv_check.setVisibility(View.GONE);
    }

//    @Override
//    public void onThemeUpdate() {
//        super.onThemeUpdate();
//        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
//    }
}
