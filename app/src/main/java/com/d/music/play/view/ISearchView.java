package com.d.music.play.view;

import com.d.lib.common.component.loader.IAbsView;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.online.model.SearchHotRespModel;

import java.util.List;

/**
 * ISearchView
 * Created by D on 2017/6/2.
 */
public interface ISearchView extends IAbsView<MusicModel> {
    void getSearchHotSuccess(List<SearchHotRespModel.HotsBean> datas);

    void getSearchHotError();
}
