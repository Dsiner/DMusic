package com.d.music.setting.activity;

import android.view.View;
import android.widget.EditText;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.component.mvp.app.BaseActivity;
import com.d.lib.common.component.quickclick.QuickClick;
import com.d.lib.common.component.statusbarcompat.StatusBarCompat;
import com.d.lib.common.util.ViewHelper;
import com.d.music.R;
import com.d.music.data.preferences.Preferences;

import cn.feng.skin.manager.loader.SkinManager;

/**
 * KoanActivity
 * Created by D on 2017/6/13.
 */
public class KoanActivity extends BaseActivity<MvpBasePresenter>
        implements MvpView, View.OnClickListener {
    EditText et_signature;
    EditText et_stroke;

    private Preferences mPreferences;

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
                mPreferences.putSignature(et_signature.getText().toString().trim());
                mPreferences.putStroke(et_stroke.getText().toString().trim());
                finish();
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.module_setting_activity_koan;
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
        et_signature = findViewById(R.id.et_signature);
        et_stroke = findViewById(R.id.et_stroke);

        ViewHelper.setOnClickListener(this, this,
                R.id.iv_title_left, R.id.tv_title_right);
    }

    @Override
    protected void init() {
        StatusBarCompat.setStatusBarColor(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));//沉浸式状态栏
        mPreferences = Preferences.getInstance(getApplicationContext());
        et_signature.setText(mPreferences.getSignature());
        et_stroke.setText(mPreferences.getStroke());
        et_signature.setSelection(et_signature.getText().length());
    }

//    @Override
//    public void onThemeUpdate() {
//        super.onThemeUpdate();
//        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));//沉浸式状态栏
//    }
}
