package com.d.music.transfer.adapter;

import android.content.Context;
import android.view.View;

import com.d.lib.rxnet.callback.DownloadCallback;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;
import com.d.lib.xrv.adapter.MultiItemTypeSupport;
import com.d.music.R;
import com.d.music.component.greendao.bean.TransferModel;
import com.d.music.transfer.fragment.TransferFragment;
import com.d.music.transfer.manager.Operater;
import com.d.music.transfer.manager.TransferManager;
import com.d.music.view.CircleProgressBar;

import java.util.List;

/**
 * TransferAdapter
 * Created by D on 2018/8/25.
 */
public class TransferAdapter extends CommonAdapter<TransferModel> {
    private int mType;

    public TransferAdapter(Context context, List<TransferModel> datas, int type,
                           MultiItemTypeSupport<TransferModel> multiItemTypeSupport) {
        super(context, datas, multiItemTypeSupport);
        this.mType = type;
    }

    @Override
    public void convert(int position, CommonHolder holder, final TransferModel item) {
        switch (holder.mLayoutId) {
            case R.layout.module_transfer_adapter_head_downloading:
                holder.setViewOnClickListener(R.id.tv_clear_task, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                holder.setViewOnClickListener(R.id.tv_pause_all, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                break;

            case R.layout.module_transfer_adapter_head_downloaded:
                holder.setViewOnClickListener(R.id.tv_clear_task, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                break;

            case R.layout.module_transfer_adapter_song:
                holder.setText(R.id.tv_title, item.songName);
                holder.setText(R.id.tv_singer, item.artistName);
                holder.setViewVisibility(R.id.iv_more, item.state == TransferModel.STATE_DONE ? View.VISIBLE : View.GONE);
                final CircleProgressBar circleBar = holder.getView(R.id.cpbar_bar);
                circleBar.setVisibility(item.state == TransferModel.STATE_DONE ? View.GONE : View.VISIBLE);
                circleBar.setState(item.state);
                circleBar.setOnClickListener(new CircleProgressBar.OnClickListener() {
                    @Override
                    public void onRestart() {
                        getOperater().start(item);
                    }

                    @Override
                    public void onResume() {
                        getOperater().start(item);
                    }

                    @Override
                    public void onPause() {
                        getOperater().pause(item);
                    }
                });
                item.setDownloadCallback(item.state == TransferModel.STATE_DONE ? null
                        : new DownloadCallback() {
                    @Override
                    public void onProgress(long currentLength, long totalLength) {
                        circleBar.setState(CircleProgressBar.STATE_PROGRESS).progress(1f * currentLength / totalLength);
                    }

                    @Override
                    public void onError(Throwable e) {
                        circleBar.setState(CircleProgressBar.STATE_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
                break;
        }
    }

    private Operater getOperater() {
        if (mType == TransferFragment.TYPE_MV) {
            return TransferManager.getIns().optMV();
        } else {
            return TransferManager.getIns().optSong();
        }
    }
}
