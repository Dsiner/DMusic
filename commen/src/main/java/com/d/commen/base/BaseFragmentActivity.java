package com.d.commen.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;

import butterknife.ButterKnife;
import cn.feng.skin.manager.base.BaseSkinFragmentActivity;

/**
 * BaseFragmentActivity
 * Created by D on 2017/4/27.
 */
public abstract class BaseFragmentActivity extends BaseSkinFragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutRes());
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    protected abstract int getLayoutRes();

    protected abstract void init();
}
