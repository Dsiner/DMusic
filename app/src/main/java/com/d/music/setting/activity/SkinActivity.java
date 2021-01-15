package com.d.music.setting.activity;

import android.app.Dialog;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.component.mvp.app.BaseActivity;
import com.d.lib.common.component.quickclick.QuickClick;
import com.d.lib.common.component.statusbarcompat.StatusBarCompat;
import com.d.lib.common.util.DimenUtils;
import com.d.lib.common.util.ToastUtils;
import com.d.lib.common.util.ViewHelper;
import com.d.lib.common.widget.dialog.AlertDialogFactory;
import com.d.music.R;
import com.d.music.component.skin.SkinUtil;
import com.d.music.data.preferences.Preferences;
import com.d.music.setting.adapter.SkinAdapter;
import com.d.music.setting.model.RadioModel;
import com.d.music.widget.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

import cn.feng.skin.manager.listener.ILoaderListener;
import cn.feng.skin.manager.loader.SkinManager;

/**
 * SkinActivity
 * Created by D on 2017/6/13.
 */
public class SkinActivity extends BaseActivity<MvpBasePresenter>
        implements MvpView, View.OnClickListener {

    RecyclerView rv_list;

    private int mIndex;
    private SkinAdapter mAdapter;
    private Dialog mDialog;

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
                mIndex = mAdapter.getIndex();
                SkinUtil.load(getApplicationContext(), mIndex, new ILoaderListener() {
                    @Override
                    public void onStart() {
                        showLoadingDialog();
                    }

                    @Override
                    public void onSuccess() {
                        dismissLoadingDialog();
                        finish();
                    }

                    @Override
                    public void onFailed() {
                        dismissLoadingDialog();
                        ToastUtils.toast(getApplicationContext(), getResources().getString(R.string.module_common_skinning_error));
                    }
                });
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.module_setting_activity_skin;
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
    protected void bindView() {
        super.bindView();
        rv_list = findViewById(R.id.rv_list);

        ViewHelper.setOnClickListener(this, this,
                R.id.iv_title_left, R.id.tv_title_right);
    }

    @Override
    protected void init() {
        StatusBarCompat.setStatusBarColor(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
        mIndex = Preferences.getInstance(getApplicationContext()).getSkinType();
        mAdapter = new SkinAdapter(this, getDatas(mIndex), R.layout.module_setting_adapter_skin);
        mAdapter.setIndex(mIndex);
        rv_list.setLayoutManager(new GridLayoutManager(this, 3));
        rv_list.addItemDecoration(new SpaceItemDecoration(DimenUtils.dp2px(this, 6)));
        rv_list.setAdapter(mAdapter);
    }

    @Override
    public void showLoadingDialog() {
        if (mDialog == null) {
            mDialog = AlertDialogFactory.createFactory(this)
                    .getLoadingDialog(getResources().getString(R.string.module_common_skinning));
        }
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    @Override
    public void dismissLoadingDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    private List<RadioModel> getDatas(int index) {
        List<RadioModel> datas = new ArrayList<>();
        for (int i = -1; i < SkinUtil.SKIN_COUNT; i++) {
            RadioModel model = new RadioModel();
            model.color = getSkinColor(i);
            model.isChecked = (i == index);
            datas.add(model);
        }
        return datas;
    }

    private int getSkinColor(int index) {
        switch (index) {
            case 0:
                return R.color.color_main_skin0;

            case 1:
                return R.color.color_main_skin1;

            case 2:
                return R.color.color_main_skin2;

            case 3:
                return R.color.color_main_skin3;

            case 4:
                return R.color.color_main_skin4;

            case 5:
                return R.color.color_main_skin5;

            case 6:
                return R.color.color_main_skin6;

            case 7:
                return R.color.color_main_skin7;

            case 8:
                return R.color.color_main_skin8;

            case 9:
                return R.color.color_main_skin9;

            case 10:
                return R.color.color_main_skin10;

            case 11:
                return R.color.color_main_skin11;

            case 12:
                return R.color.color_main_skin12;

            case 13:
                return R.color.color_main_skin13;

            case 14:
                return R.color.color_main_skin14;

            case 15:
                return R.color.color_main_skin15;

            case 16:
                return R.color.color_main_skin16;

            case 17:
                return R.color.color_main_skin17;

            default:
                return R.color.color_main_skin;
        }
    }

//    @Override
//    public void onThemeUpdate() {
//        super.onThemeUpdate();
//        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
//    }
}
