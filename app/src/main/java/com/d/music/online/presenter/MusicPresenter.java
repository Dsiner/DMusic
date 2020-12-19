package com.d.music.online.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.d.lib.aster.Aster;
import com.d.lib.aster.base.Params;
import com.d.lib.aster.callback.AsyncCallback;
import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.pulllayout.loader.CommonLoader;
import com.d.music.component.aster.API;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.online.model.ArtistSongsRespModel;
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

    public void getArtistSongs(final String tinguid, final int page) {
        Params params = new Params(API.Baidu.ArtistSongs.rtpType);
        params.addParam(API.Baidu.ArtistSongs.method, API.Baidu.METHOD_ARTIST_SONGS);
        params.addParam(API.Baidu.ArtistSongs.from, API.Baidu.FROM_QIANQIAN);
        params.addParam(API.Baidu.ArtistSongs.version, API.Baidu.VERSION);
        params.addParam(API.Baidu.ArtistSongs.format, API.Baidu.FORMAT_JSON);
        params.addParam(API.Baidu.ArtistSongs.order, "" + 2);
        params.addParam(API.Baidu.ArtistSongs.tinguid, tinguid);
        params.addParam(API.Baidu.ArtistSongs.offset, "" + (CommonLoader.PAGE_COUNT * (page - 1)));
        params.addParam(API.Baidu.ArtistSongs.limits, "" + CommonLoader.PAGE_COUNT);
        Aster.get(params.rtp, params)
                .request(new AsyncCallback<ArtistSongsRespModel, List<MusicModel>>() {

                    @Override
                    public List<MusicModel> apply(ArtistSongsRespModel resp) throws Exception {
                        List<MusicModel> datas = new ArrayList<>();
                        if (resp.songlist != null) {
                            List<ArtistSongsRespModel.SonglistBean> song_list = resp.songlist;
                            for (ArtistSongsRespModel.SonglistBean model : song_list) {
                                MusicModel music = new MusicModel();
                                music.id = MusicModel.generateId(MusicModel.TYPE_BAIDU,
                                        MusicModel.Channel.CHANNEL_TYPE_NONE,
                                        (TextUtils.isEmpty(tinguid) ? tinguid : "") + model.song_id);
                                music.type = MusicModel.TYPE_BAIDU;
                                music.songId = model.song_id;
                                music.songName = model.title;
                                music.artistId = model.artist_id;
                                music.artistName = model.author;
                                music.albumId = model.album_id;
                                music.albumName = model.album_title;
                                music.albumUrl = model.pic_small;
                                datas.add(music);
                            }
                        }
                        return datas;
                    }

                    @Override
                    public void onSuccess(List<MusicModel> response) {
                        if (getView() == null) {
                            return;
                        }
                        getView().loadSuccess(response);
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

    public void getBillSongs(final String channel, final int page) {
        Params params = new Params(API.BaiduBillSongs.rtpType);
        params.addParam(API.BaiduBillSongs.method, API.Baidu.METHOD_GET_BILL_LIST);
        params.addParam(API.BaiduBillSongs.type, channel);
        params.addParam(API.BaiduBillSongs.offset, "" + (CommonLoader.PAGE_COUNT * (page - 1)));
        params.addParam(API.BaiduBillSongs.size, "" + CommonLoader.PAGE_COUNT);
        Aster.get(params.rtp, params)
                .request(new AsyncCallback<BillSongsRespModel, BillSongsRespModel>() {

                    @Override
                    public BillSongsRespModel apply(BillSongsRespModel resp) throws Exception {
                        resp.datas = new ArrayList<>();
                        if (resp.song_list != null) {
                            List<BillSongsModel> song_list = resp.song_list;
                            for (BillSongsModel model : song_list) {
                                MusicModel music = new MusicModel();
                                music.id = MusicModel.generateId(MusicModel.TYPE_BAIDU,
                                        MusicModel.Channel.CHANNEL_TYPE_NONE,
                                        (TextUtils.isEmpty(channel) ? channel : "") + model.song_id);
                                music.type = MusicModel.TYPE_BAIDU;
                                music.songId = model.song_id;
                                music.songName = model.title;
                                music.artistId = model.artist_id;
                                music.artistName = model.author;
                                music.albumId = model.album_id;
                                music.albumName = model.album_title;
                                music.albumUrl = model.pic_small;
                                resp.datas.add(music);
                            }
                        }
                        return resp;
                    }

                    @Override
                    public void onSuccess(BillSongsRespModel response) {
                        if (getView() == null) {
                            return;
                        }
                        getView().setInfo(response);
                        getView().loadSuccess(response.datas);
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

    public void getRadioSongs(final String channel, final int page) {
        Params params = new Params(API.RadioChannelSongs.rtpType);
        params.addParam(API.RadioChannelSongs.pn, "" + 0);
        params.addParam(API.RadioChannelSongs.rn, "" + 10);
        params.addParam(API.RadioChannelSongs.channelname, channel);
        Aster.get(params.rtp, params)
                .request(new AsyncCallback<RadioSongsRespModel, RadioSongsRespModel>() {

                    @Override
                    public RadioSongsRespModel apply(RadioSongsRespModel resp) throws Exception {
                        resp.datas = new ArrayList<>();
                        if (resp.result.songlist != null) {
                            List<RadioSongsModel> songlist = resp.result.songlist;
                            for (RadioSongsModel model : songlist) {
                                if (model == null || TextUtils.isEmpty(model.songid)) {
                                    continue;
                                }
                                MusicModel music = new MusicModel();
                                music.id = MusicModel.generateId(MusicModel.TYPE_BAIDU,
                                        MusicModel.Channel.CHANNEL_TYPE_NONE,
                                        (TextUtils.isEmpty(channel) ? channel : "") + model.songid);
                                music.type = MusicModel.TYPE_BAIDU;
                                music.songId = model.songid;
                                music.songName = model.title;
                                music.artistId = model.artist_id;
                                music.artistName = model.artist;
                                music.albumUrl = model.thumb;
                                resp.datas.add(music);
                            }
                        }
                        return resp;
                    }

                    @Override
                    public void onSuccess(RadioSongsRespModel response) {
                        if (getView() == null) {
                            return;
                        }
                        getView().setInfo(response);
                        getView().loadSuccess(response.datas);
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
