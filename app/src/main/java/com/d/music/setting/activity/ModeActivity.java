package com.d.music.setting.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;

import com.d.lib.common.module.mvp.MvpBasePresenter;
import com.d.lib.common.module.mvp.MvpView;
import com.d.lib.common.module.mvp.base.BaseActivity;
import com.d.lib.common.module.repeatclick.ClickUtil;
import com.d.lib.common.view.dialog.AlertDialogFactory;
import com.d.lib.xrv.LRecyclerView;
import com.d.music.App;
import com.d.music.R;
import com.d.music.common.preferences.Preferences;
import com.d.music.setting.adapter.ModeAdapter;
import com.d.music.setting.model.RadioModel;
import com.d.music.utils.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.feng.skin.manager.loader.SkinManager;

/**
 * PlayerModeActivity
 * Created by D on 2017/6/13.
 */
public class ModeActivity extends BaseActivity<MvpBasePresenter> implements MvpView {
    @BindView(R.id.lrv_list)
    LRecyclerView lrvList;

    private int index;
    private Preferences p;
    private ModeAdapter adapter;

    @OnClick({R.id.iv_title_left, R.id.tv_title_right})
    public void onClickListener(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_title_left:
                finish();
                break;
            case R.id.tv_title_right:
                int mode = adapter.getIndex();
                if (mode >= RadioModel.MODE_NORMAL && mode <= RadioModel.MODE_NOTIFICATION) {
                    p.putPlayerMode(mode);
                }
                AlertDialogFactory.createFactory(ModeActivity.this)
                        .getAlertDialog(getResources().getString(R.string.module_common_tips),
                                getResources().getString(R.string.module_common_tips_mode_switch),
                                getResources().getString(R.string.module_common_close_now),
                                getResources().getString(R.string.module_common_not_now),
                                new AlertDialogFactory.OnClickListener() {
                                    @Override
                                    public void onClick(AlertDialog dlg, View v) {
                                        dlg.dismiss();
                                        App.exit(getApplicationContext());
                                    }
                                }, new AlertDialogFactory.OnClickListener() {
                                    @Override
                                    public void onClick(AlertDialog dlg, View v) {
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
    protected void init() {
        if (App.toFinish(getIntent())) {
            finish();
            return;
        }
        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
        p = Preferences.getIns(getApplicationContext());
        index = p.getPlayerMode();
        adapter = new ModeAdapter(this, getDatas(), R.layout.module_setting_adapter_radio);
        adapter.setIndex(index);
        lrvList.showAsList();
        lrvList.setAdapter(adapter);
    }

    private List<RadioModel> getDatas() {
        List<RadioModel> datas = new ArrayList<>();
        RadioModel model0 = new RadioModel();
        model0.content = getResources().getString(R.string.module_common_mode_normal);
        model0.isChecked = index == RadioModel.MODE_NORMAL;

        RadioModel model1 = new RadioModel();
        model1.content = getResources().getString(R.string.module_common_mode_minimalist);
        model1.isChecked = index == RadioModel.MODE_MINIMALIST;

        RadioModel model2 = new RadioModel();
        model2.content = getResources().getString(R.string.module_common_mode_notification);
        model2.isChecked = index == RadioModel.MODE_NOTIFICATION;

        datas.add(model0);
        datas.add(model1);
        datas.add(model2);
        return datas;
    }

    @Override
    public void onThemeUpdate() {
        super.onThemeUpdate();
        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
    }
}
