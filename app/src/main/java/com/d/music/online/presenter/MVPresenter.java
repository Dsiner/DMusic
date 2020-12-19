package com.d.music.online.presenter;

import android.content.Context;

import com.d.lib.aster.Aster;
import com.d.lib.aster.base.Params;
import com.d.lib.aster.callback.SimpleCallback;
import com.d.lib.common.component.loader.MvpBaseLoaderView;
import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.pulllayout.loader.CommonLoader;
import com.d.music.component.aster.API;
import com.d.music.online.model.MVModel;
import com.d.music.online.model.MVRespModel;

/**
 * MVPresenter
 * Created by D on 2018/8/11.
 */
public class MVPresenter extends MvpBasePresenter<MvpBaseLoaderView<MVModel>> {

    public MVPresenter(Context context) {
        super(context);
    }

    public void getMV(int page) {
        int offset = CommonLoader.PAGE_COUNT * (page - 1);
        int limit = CommonLoader.PAGE_COUNT;
        Params params = new Params(API.MVTop.rtpType);
        params.addParam(API.MVTop.offset, "" + offset);
        params.addParam(API.MVTop.limit, "" + limit);
        Aster.get(API.MVTop.rtpType, params)
                .request(new SimpleCallback<MVRespModel>() {
                    @Override
                    public void onSuccess(MVRespModel response) {
                        if (getView() == null) {
                            return;
                        }
                        getView().loadSuccess(response.data);
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
