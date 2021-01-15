package com.d.music.play.presenter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.d.lib.aster.Aster;
import com.d.lib.aster.base.Params;
import com.d.lib.aster.callback.AsyncCallback;
import com.d.lib.aster.callback.SimpleCallback;
import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.common.util.log.ULog;
import com.d.music.component.aster.API;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.online.model.SearchHotRespModel;
import com.d.music.online.model.SearchRespModel;
import com.d.music.play.view.ISearchView;

import java.util.ArrayList;
import java.util.List;

/**
 * SearchPresenter
 * Created by D on 2017/6/2.
 */
public class SearchPresenter extends MvpBasePresenter<ISearchView> {

    public SearchPresenter(Context context) {
        super(context);
    }

    /**
     * Get hot search
     */
    public void getSearchHot() {
        Aster.getDefault().get(API.HotSearch.rtpType)
                .request(new SimpleCallback<SearchHotRespModel>() {
                    @Override
                    public void onSuccess(SearchHotRespModel response) {
                        if (getView() == null) {
                            return;
                        }
                        if (response == null || response.result == null
                                || response.result.hots == null) {
                            getView().getSearchHotSuccess(new ArrayList<SearchHotRespModel.HotsBean>());
                            return;
                        }
                        getView().getSearchHotSuccess(response.result.hots);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getView() == null) {
                            return;
                        }
                        List<SearchHotRespModel.HotsBean> hots = getHotsBeans();
                        getView().getSearchHotSuccess(hots);
                    }

                    @NonNull
                    private List<SearchHotRespModel.HotsBean> getHotsBeans() {
                        List<SearchHotRespModel.HotsBean> hots = new ArrayList<>();
                        hots.add(new SearchHotRespModel.HotsBean("沙漠骆驼"));
                        hots.add(new SearchHotRespModel.HotsBean("三妻四妾"));
                        hots.add(new SearchHotRespModel.HotsBean("女人花"));
                        hots.add(new SearchHotRespModel.HotsBean("一曲相思"));
                        hots.add(new SearchHotRespModel.HotsBean("感谢你来过"));
                        hots.add(new SearchHotRespModel.HotsBean("张楚"));
                        hots.add(new SearchHotRespModel.HotsBean("口是心非"));
                        return hots;
                    }
                });
    }

    /**
     * Search
     */
    public void search(String key, int pageNo, int pageSize) {
        Params params = new Params(API.Search.rtpType);
        params.addParam(API.Search.method, API.Baidu.METHOD_SEARCH_MUSIC);
        params.addParam(API.Search.query, key);
        params.addParam(API.Search.page_no, "" + pageNo);
        params.addParam(API.Search.page_size, "" + pageSize);
        Aster.getDefault().get(params.rtp, params)
                .request(new AsyncCallback<SearchRespModel, List<MusicModel>>() {
                    @Override
                    public List<MusicModel> apply(SearchRespModel resp) throws Exception {
                        if (resp == null || resp.song_list == null) {
                            return new ArrayList<>();
                        }
                        List<MusicModel> datas = new ArrayList<>();
                        int size = resp.song_list.size();
                        for (int i = 0; i < size; i++) {
                            SearchRespModel.SongListBean bean = resp.song_list.get(i);
                            MusicModel model = new MusicModel();
                            model.id = MusicModel.generateId(MusicModel.TYPE_BAIDU,
                                    MusicModel.Channel.CHANNEL_TYPE_NONE, bean.song_id);
                            model.type = MusicModel.TYPE_BAIDU;
                            model.songId = bean.song_id;
                            model.songName = bean.title != null ?
                                    bean.title.replaceAll("<em>", "").replaceAll("</em>", "")
                                    : "";
                            model.artistId = bean.artist_id;
                            model.artistName = bean.author != null ?
                                    bean.author.replaceAll("<em>", "").replaceAll("</em>", "")
                                    : "";
                            model.albumId = bean.album_id;
                            model.albumName = bean.album_title != null ?
                                    bean.album_title.replaceAll("<em>", "").replaceAll("</em>", "")
                                    : "";
                            datas.add(model);
                        }
                        return datas;
                    }

                    @Override
                    public void onSuccess(List<MusicModel> response) {
                        if (getView() == null) {
                            return;
                        }
                        ULog.d("search--> onSuccess " + response.toString());
                        getView().loadSuccess(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getView() == null) {
                            return;
                        }
                        ULog.d("search --> onError " + e.toString());
                        getView().loadError();
                    }
                });
    }
}
