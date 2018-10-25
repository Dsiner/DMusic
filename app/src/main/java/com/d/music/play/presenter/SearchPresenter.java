package com.d.music.play.presenter;

import android.content.Context;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.rxnet.RxNet;
import com.d.lib.rxnet.base.Params;
import com.d.lib.rxnet.callback.AsyncCallback;
import com.d.lib.rxnet.callback.SimpleCallback;
import com.d.lib.rxnet.utils.ULog;
import com.d.music.api.API;
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
        RxNet.getDefault().get(API.HotSearch.rtpType)
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
                        getView().getSearchHotError();
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
        RxNet.getDefault().get(API.Search.rtpType, params)
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
                            model.type = bean.resource_type;
                            model.songName = bean.title;
                            model.songId = bean.song_id;
                            model.artistId = bean.artist_id;
                            model.artistName = bean.author;
                            model.albumId = bean.album_id;
                            model.albumName = bean.album_title;
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
                        getView().setData(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getView() == null) {
                            return;
                        }
                        ULog.d("search --> onError " + e.toString());
                    }
                });
    }
}
