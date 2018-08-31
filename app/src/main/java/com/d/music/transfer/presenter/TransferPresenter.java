package com.d.music.transfer.presenter;

import android.content.Context;

import com.d.lib.common.module.mvp.MvpBasePresenter;
import com.d.music.transfer.view.ITransferView;

/**
 * TransferPresenter
 * Created by D on 2018/8/25.
 */
public class TransferPresenter extends MvpBasePresenter<ITransferView> {

    public TransferPresenter(Context context) {
        super(context);
    }

    public void getDatas() {

    }
}
