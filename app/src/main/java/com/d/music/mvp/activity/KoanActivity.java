package com.d.music.mvp.activity;

import android.view.View;
import android.widget.EditText;

import com.d.lib.common.module.mvp.MvpBasePresenter;
import com.d.lib.common.module.mvp.MvpView;
import com.d.lib.common.module.mvp.base.BaseActivity;
import com.d.lib.common.module.repeatclick.ClickUtil;
import com.d.music.R;
import com.d.music.common.Preferences;
import com.d.music.utils.StatusBarCompat;

import butterknife.BindView;
import butterknife.OnClick;
import cn.feng.skin.manager.loader.SkinManager;

/**
 * KoanActivity
 * Created by D on 2017/6/13.
 */
public class KoanActivity extends BaseActivity<MvpBasePresenter> implements MvpView {
    @BindView(R.id.et_signature)
    EditText etSignature;
    @BindView(R.id.et_stroke)
    EditText etStroke;

    private Preferences p;

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
                p.putSignature(etSignature.getText().toString().trim());
                p.putStroke(etStroke.getText().toString().trim());
                finish();
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_koan;
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
    protected void init() {
        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));//沉浸式状态栏
        p = Preferences.getInstance(getApplicationContext());
        etSignature.setText(p.getSignature());
        etStroke.setText(p.getStroke());
        etSignature.setSelection(etSignature.getText().length());
    }

    @Override
    public void onThemeUpdate() {
        super.onThemeUpdate();
        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));//沉浸式状态栏
    }
}
