package com.d.music.setting.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;

import com.d.lib.common.view.dialog.AlertDialogFactory;
import com.d.lib.common.module.mvp.MvpBasePresenter;
import com.d.lib.common.module.mvp.MvpView;
import com.d.lib.common.module.mvp.base.BaseActivity;
import com.d.lib.common.module.repeatclick.ClickUtil;
import com.d.lib.xrv.LRecyclerView;
import com.d.music.App;
import com.d.music.R;
import com.d.music.common.Preferences;
import com.d.music.setting.adapter.RadioAdapter;
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
public class PlayerModeActivity extends BaseActivity<MvpBasePresenter> implements MvpView {
    @BindView(R.id.lrv_list)
    LRecyclerView lrvList;

    private int index;
    private Preferences p;
    private RadioAdapter adapter;
    private List<RadioModel> datas;

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
        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));//沉浸式状态栏
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

    @Override
    public void onThemeUpdate() {
        super.onThemeUpdate();
        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));//沉浸式状态栏
    }
}
