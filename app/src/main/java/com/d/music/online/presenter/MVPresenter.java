package com.d.music.online.presenter;

import android.content.Context;

import com.d.lib.common.module.loader.CommonLoader;
import com.d.lib.common.module.loader.IAbsView;
import com.d.lib.common.module.mvp.MvpBasePresenter;
import com.d.lib.rxnet.RxNet;
import com.d.lib.rxnet.base.Params;
import com.d.lib.rxnet.listener.SimpleCallBack;
import com.d.music.api.API;
import com.d.music.online.model.MVModel;
import com.d.music.online.model.MVRespModel;

/**
 * MVPresenter
 * Created by D on 2018/8/11.
 */
public class MVPresenter extends MvpBasePresenter<IAbsView<MVModel>> {

    public MVPresenter(Context context) {
        super(context);
    }

    public void getMV(int page) {
        int offset = CommonLoader.PAGE_COUNT * (page - 1);
        int limit = CommonLoader.PAGE_COUNT;
        Params params = new Params(API.MVTop.rtpType);
        params.addParam(API.MVTop.offset, "" + offset);
        params.addParam(API.MVTop.limit, "" + limit);
        RxNet.get(API.MVTop.rtpType, params)
                .request(new SimpleCallBack<MVRespModel>() {
                    @Override
                    public void onSuccess(MVRespModel response) {
                        if (getView() == null) {
                            return;
                        }
                        getView().setData(response.data);
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
