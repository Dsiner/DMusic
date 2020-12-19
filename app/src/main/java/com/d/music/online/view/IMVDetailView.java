package com.d.music.online.view;

import com.d.lib.common.component.loader.MvpBaseLoaderView;
import com.d.music.online.model.MVDetailModel;

import java.util.List;

/**
 * IMVDetailView
 * Created by D on 2018/8/13.
 */
public interface IMVDetailView extends MvpBaseLoaderView<MVDetailModel> {
    void setInfo(MVDetailModel info);

    void setSimilar(List<MVDetailModel> similar);
}
