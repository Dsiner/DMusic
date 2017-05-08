package com.d.commen.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import butterknife.ButterKnife;

/**
 * BaseFragmentActivity
 * Created by D on 2017/4/27.
 */
public abstract class BaseFragmentActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutRes());
        ButterKnife.bind(this);
        init();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    protected abstract int getLayoutRes();

    protected abstract void init();
}
