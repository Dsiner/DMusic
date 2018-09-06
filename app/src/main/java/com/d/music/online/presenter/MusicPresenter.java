package com.d.music.online.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.d.lib.common.component.loader.CommonLoader;
import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.rxnet.RxNet;
import com.d.lib.rxnet.base.Params;
import com.d.lib.rxnet.listener.AsyncCallBack;
import com.d.music.api.API;
import com.d.music.component.greendao.bean.MusicModel;
import com.d.music.online.model.BillSongsModel;
import com.d.music.online.model.BillSongsRespModel;
import com.d.music.online.model.RadioSongsModel;
import com.d.music.online.model.RadioSongsRespModel;
import com.d.music.online.view.IMusicView;

import java.util.ArrayList;
import java.util.List;

/**
 * MusicPresenter
 * Created by D on 2018/8/11.
 */
public class MusicPresenter extends MvpBasePresenter<IMusicView> {

    public MusicPresenter(Context context) {
        super(context);
    }

    public void getBillSongs(String channel, int page) {
        Params params = new Params(API.BaiduBillSongs.rtpType);
        params.addParam(API.BaiduBillSongs.method, API.Baidu.METHOD_GET_BILL_LIST);
        params.addParam(API.BaiduBillSongs.type, channel);
        params.addParam(API.BaiduBillSongs.offset, "" + (CommonLoader.PAGE_COUNT * (page - 1)));
        params.addParam(API.BaiduBillSongs.size, "" + CommonLoader.PAGE_COUNT);
        RxNet.get(API.BaiduBillSongs.rtpType, params)
                .request(new AsyncCallBack<BillSongsRespModel, BillSongsRespModel>() {

                    @Override
                    public BillSongsRespModel apply(BillSongsRespModel billSongsRespModel) throws Exception {
                        billSongsRespModel.datas = new ArrayList<>();
                        if (billSongsRespModel.song_list != null) {
                            List<BillSongsModel> song_list = billSongsRespModel.song_list;
                            for (BillSongsModel model : song_list) {
                                MusicModel music = new MusicModel();
                                music.type = MusicModel.TYPE_BAIDU;
                                music.url = model.song_id;
                                music.songId = model.song_id;
                                music.songName = model.title;
                                music.artistId = model.artist_id;
                                music.artistName = model.author;
                                music.albumId = model.album_id;
                                music.albumName = model.album_title;
                                music.albumUrl = model.pic_small;
                                billSongsRespModel.datas.add(music);
                            }
                        }
                        return billSongsRespModel;
                    }

                    @Override
                    public void onSuccess(BillSongsRespModel response) {
                        if (getView() == null) {
                            return;
                        }
                        getView().setInfo(response);
                        getView().setData(response.datas);
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

    public void getRadioSongs(String channel, int page) {
        Params params = new Params(API.RadioChannelSongs.rtpType);
        params.addParam(API.RadioChannelSongs.pn, "" + 0);
        params.addParam(API.RadioChannelSongs.rn, "" + 10);
        params.addParam(API.RadioChannelSongs.channelname, channel);
        RxNet.get(API.RadioChannelSongs.rtpType, params)
                .request(new AsyncCallBack<RadioSongsRespModel, RadioSongsRespModel>() {

                    @Override
                    public RadioSongsRespModel apply(RadioSongsRespModel radioSongsRespModel) throws Exception {
                        radioSongsRespModel.datas = new ArrayList<>();
                        if (radioSongsRespModel.result.songlist != null) {
                            List<RadioSongsModel> songlist = radioSongsRespModel.result.songlist;
                            for (RadioSongsModel model : songlist) {
                                if (model == null || TextUtils.isEmpty(model.songid)) {
                                    continue;
                                }
                                MusicModel music = new MusicModel();
                                music.type = MusicModel.TYPE_BAIDU;
                                music.url = model.songid;
                                music.songId = model.songid;
                                music.songName = model.title;
                                music.artistId = model.artist_id;
                                music.artistName = model.artist;
                                music.albumUrl = model.thumb;
                                radioSongsRespModel.datas.add(music);
                            }
                        }
                        return radioSongsRespModel;
                    }

                    @Override
                    public void onSuccess(RadioSongsRespModel response) {
                        if (getView() == null) {
                            return;
                        }
                        getView().setInfo(response);
                        getView().setData(response.datas);
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
