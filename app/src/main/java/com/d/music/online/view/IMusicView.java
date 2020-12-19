package com.d.music.online.view;

import com.d.lib.common.component.loader.MvpBaseLoaderView;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.online.model.BillSongsRespModel;
import com.d.music.online.model.RadioSongsRespModel;

/**
 * IMusicView
 * Created by D on 2018/8/13.
 */
public interface IMusicView extends MvpBaseLoaderView<MusicModel> {
    void setInfo(BillSongsRespModel info);

    void setInfo(RadioSongsRespModel info);
}
