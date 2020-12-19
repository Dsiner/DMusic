package com.d.music.online.presenter;

import android.content.Context;

import com.d.lib.aster.Aster;
import com.d.lib.aster.base.Params;
import com.d.lib.aster.callback.SimpleCallback;
import com.d.lib.common.component.loader.MvpBaseLoaderView;
import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.music.component.aster.API;
import com.d.music.online.model.BillModel;
import com.d.music.online.model.BillRespModel;

/**
 * BillPresenter
 * Created by D on 2018/8/11.
 */
public class BillPresenter extends MvpBasePresenter<MvpBaseLoaderView<BillModel>> {

    public BillPresenter(Context context) {
        super(context);
    }

    public void getBill() {
        Params params = new Params(API.BaiduBill.rtpType);
        params.addParam(API.BaiduBill.method, API.Baidu.METHOD_CATEGORY);
        params.addParam(API.BaiduBill.operator, "" + 1);
        params.addParam(API.BaiduBill.kflag, "" + 2);
        params.addParam(API.BaiduBill.format, "json");
        Aster.get(API.BaiduBill.rtpType, params)
                .request(new SimpleCallback<BillRespModel>() {
                    @Override
                    public void onSuccess(BillRespModel response) {
                        if (getView() == null) {
                            return;
                        }
                        getView().loadSuccess(response.content);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getView() == null) {
                            return;
                        }
                        getView().loadError();
                    }
                });
    }
}
