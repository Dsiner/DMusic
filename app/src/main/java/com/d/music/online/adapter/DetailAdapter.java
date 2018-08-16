package com.d.music.online.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.d.lib.common.utils.log.ULog;
import com.d.lib.common.view.dialog.AbsSheetDialog;
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
import com.d.music.module.service.MusicControl;
import com.d.music.module.service.MusicService;
import com.d.music.online.model.SongInfoRespModel;
import com.d.music.view.dialog.OperationDialog;

import java.util.ArrayList;
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
                play(item);
            }
        });
        holder.setViewOnClickListener(R.id.iv_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<OperationDialog.Bean> datas = new ArrayList<>();
                datas.add(new OperationDialog.Bean().with(OperationDialog.Bean.TYPE_DOWNLOAD, false));
                OperationDialog.getOperationDialog(mContext, OperationDialog.TYPE_NORMAL, "", datas,
                        new AbsSheetDialog.OnItemClickListener<OperationDialog.Bean>() {
                            @Override
                            public void onClick(Dialog dlg, int position, OperationDialog.Bean bean) {
                                if (bean.type == OperationDialog.Bean.TYPE_DOWNLOAD) {
                                    download1(item);
                                }
                            }

                            @Override
                            public void onCancel(Dialog dlg) {

                            }
                        });
            }
        });
    }

    private void play(final MusicModel item) {
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
                        // FIXME: @dsiner 在线播放 2018/8/16
                        SongInfoRespModel.DataBean.SongListBean song = response.data.songList.get(0);
                        MusicModel model = new MusicModel();
                        model.singer = song.artistName;
                        model.songName = song.songName;
                        model.url = song.songLink;
                        List<MusicModel> datas = new ArrayList<>();
                        datas.add(model);
                        MusicControl control = MusicService.getControl(mContext.getApplicationContext());
                        control.init(mContext.getApplicationContext(), datas, 0, true);
                    }

                    @Override
                    public void onError(Throwable e) {

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
                        // Download song
                        download2(song.songLink, Constants.Path.song, song.songName + "." + song.format);
                        // Download lrc
                        download2(song.lrcLink, Constants.Path.lyric, song.songName + ".lrc");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private void download2(String url, String path, String name) {
        RxNet.download(url)
                .connectTimeout(60 * 1000)
                .readTimeout(60 * 1000)
                .writeTimeout(60 * 1000)
                .retryCount(3)
                .retryDelayMillis(1000)
                .tag(path + name)
                .request(path, name, new DownloadCallBack() {

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
