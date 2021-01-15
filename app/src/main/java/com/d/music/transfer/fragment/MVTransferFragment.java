package com.d.music.transfer.fragment;

import android.view.View;

import androidx.annotation.NonNull;

import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.lib.pulllayout.rv.adapter.MultiItemTypeSupport;
import com.d.music.R;
import com.d.music.data.database.greendao.bean.TransferModel;
import com.d.music.transfer.activity.TransferActivity;
import com.d.music.transfer.adapter.TransferAdapter;
import com.d.music.transfer.manager.TransferManager;
import com.d.music.transfer.manager.operation.TransferOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * MVTransferFragment
 * Created by D on 2018/10/12.
 */
public class MVTransferFragment extends TransferFragment {

    @NonNull
    @Override
    protected TransferOperator getOperator() {
        return TransferManager.getInstance().optMV();
    }

    @Override
    protected CommonAdapter<TransferModel> getAdapter() {
        return new TransferAdapter(mContext, new ArrayList<TransferModel>(), mType,
                new MultiItemTypeSupport<TransferModel>() {
                    @Override
                    public int getLayoutId(int viewType) {
                        switch (viewType) {
                            case TransferModel.TRANSFER_TYPE_HEAD_NOT:
                                return R.layout.module_transfer_adapter_head_downloading;
                            case TransferModel.TRANSFER_TYPE_HEAD_DONE:
                                return R.layout.module_transfer_adapter_head_downloaded;
                            case TransferModel.TRANSFER_TYPE_MV:
                            default:
                                return R.layout.module_transfer_adapter_mv;
                        }
                    }

                    @Override
                    public int getItemViewType(int position, TransferModel model) {
                        return model.transferType;
                    }
                });
    }

    @Override
    public void notifyDataSetChanged(List<List<TransferModel>> lists) {
        final int countDownloading = lists.get(0).size();
        ((TransferActivity) getActivity()).setTabNumber(mType,
                countDownloading > 0 ? "" + countDownloading : "",
                countDownloading > 0 ? View.VISIBLE : View.GONE);
        super.notifyDataSetChanged(lists);
    }
}
