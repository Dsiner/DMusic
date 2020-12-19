package com.d.music.transfer.view;

import com.d.lib.common.component.loader.MvpBaseLoaderView;
import com.d.music.data.database.greendao.bean.TransferModel;

import java.util.List;

/**
 * ITransferView
 * Created by D on 2018/8/25.
 */
public interface ITransferView extends MvpBaseLoaderView<TransferModel> {
    void notifyDataSetChanged(List<List<TransferModel>> lists);
}
