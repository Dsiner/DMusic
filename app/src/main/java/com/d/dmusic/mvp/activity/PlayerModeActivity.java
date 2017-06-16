package com.d.dmusic.mvp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;

import com.d.commen.base.BaseActivity;
import com.d.commen.mvp.MvpBasePresenter;
import com.d.commen.mvp.MvpView;
import com.d.dmusic.R;
import com.d.dmusic.application.SysApplication;
import com.d.dmusic.commen.AlertDialogFactory;
import com.d.dmusic.commen.Preferences;
import com.d.dmusic.module.repeatclick.ClickUtil;
import com.d.dmusic.mvp.adapter.RadioAdapter;
import com.d.dmusic.mvp.model.RadioModel;
import com.d.dmusic.utils.StatusBarCompat;
import com.d.dmusic.view.TitleLayout;
import com.d.lib.xrv.LRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * PlayerModeActivity
 * Created by D on 2017/6/13.
 */
public class PlayerModeActivity extends BaseActivity<MvpBasePresenter> implements MvpView {
    @Bind(R.id.tl_title)
    TitleLayout tlTitle;
    @Bind(R.id.lrv_list)
    LRecyclerView lrvList;

    private Preferences p;
    private RadioAdapter adapter;
    private List<RadioModel> datas;
    private int index;

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
                if (mode >= 0 && mode <= 2) {
                    p.putPlayerMode(mode);
                }
                AlertDialogFactory.createFactory(PlayerModeActivity.this).getAlertDialog("提示",
                        "模式切换将于重启后生效,是否立即关闭？", "立即关闭", "暂不", new AlertDialogFactory.OnClickListener() {
                            @Override
                            public void onClick(AlertDialog dlg, View v) {
                                dlg.dismiss();
                                SysApplication.getInstance().exit();
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
        return R.layout.activity_player_mode;
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
        if (SysApplication.toFinish(intent)) {
            finish();
        }
    }

    @Override
    protected void init() {
        if (SysApplication.toFinish(getIntent())) {
            finish();
            return;
        }
        StatusBarCompat.compat(this, getResources().getColor(R.color.color_main));//沉浸式状态栏
        p = Preferences.getInstance(getApplicationContext());
        index = p.getPlayerMode();
        adapter = new RadioAdapter(this, getDatas(), R.layout.adapter_radio);
        adapter.setIndex(index);
        lrvList.showAsList();
        lrvList.setAdapter(adapter);
    }

    private List<RadioModel> getDatas() {
        datas = new ArrayList<>();
        RadioModel model0 = new RadioModel();
        model0.content = "普通模式";
        model0.isChecked = index == 0;

        RadioModel model1 = new RadioModel();
        model1.content = "极简模式";
        model1.isChecked = index == 1;

        RadioModel model2 = new RadioModel();
        model2.content = "通知栏模式";
        model2.isChecked = index == 2;

        datas.add(model0);
        datas.add(model1);
        datas.add(model2);
        return datas;
    }
}
