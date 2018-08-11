package com.d.music.online.presenter;

import android.content.Context;

import com.d.lib.common.module.loader.IAbsView;
import com.d.lib.common.module.mvp.MvpBasePresenter;
import com.d.lib.rxnet.RxNet;
import com.d.lib.rxnet.base.Params;
import com.d.lib.rxnet.listener.SimpleCallBack;
import com.d.music.api.API;
import com.d.music.online.model.SingerModel;
import com.d.music.online.model.SingerRespModel;

/**
 * SingerPresenter
 * Created by D on 2018/8/11.
 */
public class SingerPresenter extends MvpBasePresenter<IAbsView<SingerModel>> {

    public SingerPresenter(Context context) {
        super(context);
    }

    public void getSinger() {
        Params params = new Params(API.TopArtists.rtpType);
        params.addParam(API.TopArtists.offset, "" + 0);
        params.addParam(API.TopArtists.limit, "" + 30);
        RxNet.get(API.TopArtists.rtpType, params)
                .request(new SimpleCallBack<SingerRespModel>() {
                    @Override
                    public void onSuccess(SingerRespModel response) {
                        if (getView() == null) {
                            return;
                        }
                        getView().setData(response.list.artists);
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
