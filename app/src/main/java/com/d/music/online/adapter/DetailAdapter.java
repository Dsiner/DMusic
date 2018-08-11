package com.d.music.online.adapter;

import android.content.Context;
import android.view.View;

import com.d.lib.common.utils.log.ULog;
import com.d.lib.rxnet.RxNet;
import com.d.lib.rxnet.base.Params;
import com.d.lib.rxnet.listener.DownloadCallBack;
import com.d.lib.rxnet.listener.SimpleCallBack;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;
import com.d.music.R;
import com.d.music.api.API;
import com.d.music.common.Constants;
import com.d.music.module.greendao.music.base.MusicModel;
import com.d.music.online.model.SongInfoRespModel;

import java.util.List;

/**
 * DetailAdapter
 * Created by D on 2018/8/11.
 */
public class DetailAdapter extends CommonAdapter<MusicModel> {

    public DetailAdapter(Context context, List<MusicModel> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(int position, CommonHolder holder, final MusicModel item) {
        holder.setText(R.id.tv_seq, "" + (position + 1));
        holder.setText(R.id.tv_title, "" + item.songName);
        holder.setText(R.id.tv_singer, "" + item.singer);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download1(item);
            }
        });
    }

    private void download1(final MusicModel item) {
        Params params = new Params(API.SongInfo.rtpType);
        params.addParam(API.SongInfo.songIds, item.url);
        RxNet.get(API.SongInfo.rtpType, params)
                .request(new SimpleCallBack<SongInfoRespModel>() {
                    @Override
                    public void onSuccess(SongInfoRespModel response) {
                        if (response.data == null || response.data.songList == null
                                || response.data.songList.size() <= 0) {
                            return;
                        }
                        SongInfoRespModel.DataBean.SongListBean song = response.data.songList.get(0);
//                        download2(song.songLink, song.songName + "." + song.format);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private void download2(String url, String name) {
        RxNet.download(url)
                .connectTimeout(60 * 1000)
                .readTimeout(60 * 1000)
                .writeTimeout(60 * 1000)
                .retryCount(3)
                .retryDelayMillis(1000)
                .tag(Constants.Path.song + name)
                .request(Constants.Path.song, name, new DownloadCallBack() {

                    @Override
                    public void onProgress(long currentLength, long totalLength) {
                        ULog.d("dsiner_request onProgresss: --> download: " + currentLength + " total: " + totalLength);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ULog.d("dsiner_request onError " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        ULog.d("dsiner_request onComplete:");
                    }
                });
    }
}
