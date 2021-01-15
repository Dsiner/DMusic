package com.d.music.setting.activity;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.component.mvp.app.BaseActivity;
import com.d.lib.common.component.quickclick.QuickClick;
import com.d.lib.common.component.statusbarcompat.StatusBarCompat;
import com.d.lib.common.util.ViewHelper;
import com.d.lib.common.widget.dialog.AlertDialogFactory;
import com.d.music.App;
import com.d.music.R;
import com.d.music.data.preferences.Preferences;
import com.d.music.setting.adapter.ModeAdapter;
import com.d.music.setting.model.RadioModel;

import java.util.ArrayList;
import java.util.List;

import cn.feng.skin.manager.loader.SkinManager;

/**
 * ModeActivity
 * Created by D on 2017/6/13.
 */
public class ModeActivity extends BaseActivity<MvpBasePresenter>
        implements MvpView, View.OnClickListener {
    RecyclerView rv_list;

    private int mIndex;
    private Preferences mPreferences;
    private ModeAdapter mAdapter;

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
                int mode = mAdapter.getIndex();
                if (mode >= RadioModel.MODE_NORMAL && mode <= RadioModel.MODE_NOTIFICATION) {
                    mPreferences.putPlayerMode(mode);
                }
                AlertDialogFactory.createFactory(ModeActivity.this)
                        .getAlertDialog(getResources().getString(R.string.module_common_tips),
                                getResources().getString(R.string.module_common_tips_mode_switch),
                                getResources().getString(R.string.module_common_close_now),
                                getResources().getString(R.string.module_common_not_now),
                                new AlertDialogFactory.OnClickListener() {
                                    @Override
                                    public void onClick(Dialog dlg, View v) {
                                        dlg.dismiss();
                                        App.Companion.exit();
                                    }
                                }, new AlertDialogFactory.OnClickListener() {
                                    @Override
                                    public void onClick(Dialog dlg, View v) {
                                        dlg.dismiss();
                                        finish();
                                    }
                                });
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.module_setting_activity_mode;
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
        rv_list = findViewById(R.id.rv_list);

        ViewHelper.setOnClickListener(this, this,
                R.id.iv_title_left, R.id.tv_title_right);
    }

    @Override
    protected void init() {
        if (App.toFinish(getIntent())) {
            finish();
            return;
        }
        StatusBarCompat.setStatusBarColor(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
        mPreferences = Preferences.getInstance(getApplicationContext());
        mIndex = mPreferences.getPlayerMode();
        mAdapter = new ModeAdapter(this, getDatas(), R.layout.module_setting_adapter_radio);
        mAdapter.setIndex(mIndex);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_list.setLayoutManager(layoutManager);
        rv_list.setAdapter(mAdapter);
    }

    private List<RadioModel> getDatas() {
        List<RadioModel> datas = new ArrayList<>();
        RadioModel model0 = new RadioModel();
        model0.content = getResources().getString(R.string.module_common_mode_normal);
        model0.isChecked = mIndex == RadioModel.MODE_NORMAL;

        RadioModel model1 = new RadioModel();
        model1.content = getResources().getString(R.string.module_common_mode_minimalist);
        model1.isChecked = mIndex == RadioModel.MODE_MINIMALIST;

        RadioModel model2 = new RadioModel();
        model2.content = getResources().getString(R.string.module_common_mode_notification);
        model2.isChecked = mIndex == RadioModel.MODE_NOTIFICATION;

        datas.add(model0);
        datas.add(model1);
        datas.add(model2);
        return datas;
    }

//    @Override
//    public void onThemeUpdate() {
//        super.onThemeUpdate();
//        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
//    }
}
