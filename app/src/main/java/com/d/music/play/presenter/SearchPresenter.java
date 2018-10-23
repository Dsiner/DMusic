package com.d.music.play.presenter;

import android.content.Context;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.music.play.view.IPlayView;

/**
 * SearchPresenter
 * Created by D on 2017/6/2.
 */
public class SearchPresenter extends MvpBasePresenter<IPlayView> {

    public SearchPresenter(Context context) {
        super(context);
    }

}
