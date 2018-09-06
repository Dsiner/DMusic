package com.d.music.online.presenter;

import android.content.Context;

import com.d.lib.common.component.loader.IAbsView;
import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.rxnet.RxNet;
import com.d.lib.rxnet.base.Params;
import com.d.lib.rxnet.listener.SimpleCallBack;
import com.d.music.api.API;
import com.d.music.online.model.BillModel;
import com.d.music.online.model.BillRespModel;

/**
 * BillPresenter
 * Created by D on 2018/8/11.
 */
public class BillPresenter extends MvpBasePresenter<IAbsView<BillModel>> {

    public BillPresenter(Context context) {
        super(context);
    }

    public void getBill() {
        Params params = new Params(API.BaiduBill.rtpType);
        params.addParam(API.BaiduBill.method, API.Baidu.METHOD_CATEGORY);
        params.addParam(API.BaiduBill.operator, "" + 1);
        params.addParam(API.BaiduBill.kflag, "" + 2);
        params.addParam(API.BaiduBill.format, "json");
        RxNet.get(API.BaiduBill.rtpType, params)
                .request(new SimpleCallBack<BillRespModel>() {
                    @Override
                    public void onSuccess(BillRespModel response) {
                        if (getView() == null) {
                            return;
                        }
                        getView().setData(response.content);
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
