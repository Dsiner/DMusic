package com.d.music.online.presenter;

import android.content.Context;

import com.d.lib.aster.Aster;
import com.d.lib.aster.base.Params;
import com.d.lib.aster.callback.SimpleCallback;
import com.d.lib.common.component.loader.MvpBaseLoaderView;
import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.music.component.aster.API;
import com.d.music.online.model.SingerModel;
import com.d.music.online.model.SingerRespModel;

/**
 * SingerPresenter
 * Created by D on 2018/8/11.
 */
public class SingerPresenter extends MvpBasePresenter<MvpBaseLoaderView<SingerModel>> {

    public SingerPresenter(Context context) {
        super(context);
    }

    public void getSinger() {
        Params params = new Params(API.Baidu.HotArtists.rtpType);
        params.addParam(API.Baidu.HotArtists.method, API.Baidu.METHOD_72_HOT_ARTIST);
        params.addParam(API.Baidu.HotArtists.from, API.Baidu.FROM_QIANQIAN);
        params.addParam(API.Baidu.HotArtists.version, "2.1.0");
        params.addParam(API.Baidu.HotArtists.format, "json");
        params.addParam(API.Baidu.HotArtists.order, "" + 1);
        params.addParam(API.Baidu.HotArtists.offset, "" + 0);
        params.addParam(API.Baidu.HotArtists.limit, "" + 50);
        Aster.get(params.rtp, params)
                .request(new SimpleCallback<SingerRespModel>() {
                    @Override
                    public void onSuccess(SingerRespModel response) {
                        if (getView() == null) {
                            return;
                        }
                        getView().loadSuccess(response.artist);
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
