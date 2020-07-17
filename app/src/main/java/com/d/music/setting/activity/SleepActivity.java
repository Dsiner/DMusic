package com.d.music.setting.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.component.mvp.app.BaseActivity;
import com.d.lib.common.component.repeatclick.ClickFast;
import com.d.lib.xrv.LRecyclerView;
import com.d.music.App;
import com.d.music.R;
import com.d.music.component.service.NotificationService;
import com.d.music.data.preferences.Preferences;
import com.d.music.setting.adapter.TimingAdapter;
import com.d.music.setting.model.RadioModel;
import com.d.music.utils.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.feng.skin.manager.loader.SkinManager;

/**
 * SleepActivity
 * Created by D on 2017/6/13.
 */
public class SleepActivity extends BaseActivity<MvpBasePresenter> implements MvpView, TimingAdapter.OnChangeListener {
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_check)
    ImageView ivCheck;
    @BindView(R.id.lrv_list)
    LRecyclerView lrvList;

    private Preferences p;
    private TimingAdapter adapter;
    private int sleepType;

    @OnClick({R.id.iv_title_left, R.id.tv_title_right, R.id.rlyt_first})
    public void onClickListener(View v) {
        if (ClickFast.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_title_left:
                finish();
                break;
            case R.id.tv_title_right:
                if (sleepType < 0 || sleepType > 6) {
                    return;
                }
                NotificationService.startService(getApplicationContext());
                long time = 0;
                switch (sleepType) {
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
                NotificationService.timing(getApplicationContext(), false, 0);
                NotificationService.timing(getApplicationContext(), time > 0, time);
                p.putSleepType(sleepType);
                finish();
                break;
            case R.id.rlyt_first:
                if (sleepType == 0) {
                    return;
                }
                ivCheck.setVisibility(View.VISIBLE);
                adapter.setIndex(-1);
                if (sleepType - 1 >= 0 && sleepType - 1 < adapter.getDatas().size()) {
                    adapter.getDatas().get(sleepType - 1).isChecked = false;
                }
                adapter.notifyDataSetChanged();
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
    protected void init() {
        if (App.toFinish(getIntent())) {
            finish();
            return;
        }
        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
        p = Preferences.getIns(getApplicationContext());
        sleepType = p.getSleepType();
        tvContent.setText(getResources().getString(R.string.module_common_close));
        ivCheck.setVisibility(sleepType == 0 ? View.VISIBLE : View.GONE);
        adapter = new TimingAdapter(this, getDatas(), R.layout.module_setting_adapter_radio);
        adapter.setIndex(sleepType - 1);
        adapter.setOnChangeListener(this);
        lrvList.showAsList();
        lrvList.setAdapter(adapter);
    }

    private List<RadioModel> getDatas() {
        String[] values = getResources().getStringArray(R.array.module_setting_timing_values);
        List<RadioModel> datas = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            RadioModel model = new RadioModel();
            model.content = values[i];
            model.isChecked = (sleepType == i + 1);
            datas.add(model);
        }
        return datas;
    }

    @Override
    public void onChange(int index) {
        sleepType = index + 1;
        ivCheck.setVisibility(View.GONE);
    }

    @Override
    public void onThemeUpdate() {
        super.onThemeUpdate();
        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
    }
}
