package com.d.music.transfer.adapter;

import android.content.Context;

import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;
import com.d.lib.xrv.adapter.MultiItemTypeSupport;
import com.d.music.transfer.model.TransferModel;

import java.util.List;

/**
 * TransferAdapter
 * Created by D on 2018/8/25.
 */
public class TransferAdapter extends CommonAdapter<TransferModel> {

    public TransferAdapter(Context context, List<TransferModel> datas, MultiItemTypeSupport<TransferModel> multiItemTypeSupport) {
        super(context, datas, multiItemTypeSupport);
    }

    @Override
    public void convert(int position, CommonHolder holder, TransferModel item) {

    }
}
