package com.d.commen.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.d.commen.mvp.MvpBasePresenter;
import com.d.commen.mvp.MvpView;

import butterknife.ButterKnife;

public abstract class BaseFragment<T extends MvpBasePresenter> extends Fragment {
    protected T mPresenter;
    protected View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (rootView == null) {
            int layoutRes = getLayoutRes();
            mPresenter = getPresenter();
            rootView = getActivity().getLayoutInflater().inflate(layoutRes, null);
            ButterKnife.bind(this, rootView);
            init();
        } else {
            if (rootView.getParent() != null) {
                ((ViewGroup) rootView.getParent()).removeView(rootView);
            }
            ButterKnife.bind(this, rootView);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.attachView(getMvpView());
    }

    @Override
    public void onDestroyView() {
        mPresenter.detachView(false);
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Return the layout resource like R.layout.my_layout
     *
     * @return the layout resource or zero ("0"), if you don't want to have an UI
     */
    protected abstract int getLayoutRes();

    public abstract T getPresenter();

    protected abstract MvpView getMvpView();

    protected abstract void init();
}
