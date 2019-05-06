package com.d.music.view.popup;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d.lib.common.component.repeatclick.ClickFast;
import com.d.lib.common.view.popup.AbstractPopup;
import com.d.lib.xrv.LRecyclerView;
import com.d.music.R;
import com.d.music.component.media.controler.MediaControler;
import com.d.music.data.Constants;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.preferences.Preferences;
import com.d.music.play.adapter.PlayQueueAdapter;

import java.util.List;

/**
 * PlayQueuePopup
 * Created by D on 2017/4/29.
 */
public class PlayQueuePopup extends AbstractPopup implements View.OnClickListener, PlayQueueAdapter.IQueueListener {
    private Preferences p;
    private ImageView ivPlayMode;
    private TextView tvPlayMode;
    private TextView tvCount;
    private LRecyclerView lrvList;
    private PlayQueueAdapter adapter;
    private PlayQueueAdapter.IQueueListener listener;
    private List<MusicModel> models;

    public PlayQueuePopup(Context context) {
        super(context, R.layout.module_play_popup_play_queue, R.style.AnimBottom);
    }

    @Override
    protected void init() {
        p = Preferences.getIns(mContext.getApplicationContext());
        LinearLayout llytQueue = (LinearLayout) mRootView.findViewById(R.id.llyt_queue);
        FrameLayout flytPlayMode = (FrameLayout) mRootView.findViewById(R.id.flyt_play_mode);
        ivPlayMode = (ImageView) mRootView.findViewById(R.id.iv_play_mode);
        tvPlayMode = (TextView) mRootView.findViewById(R.id.tv_play_mode);
        tvCount = (TextView) mRootView.findViewById(R.id.tv_count);
        TextView ivDeleteAll = (TextView) mRootView.findViewById(R.id.tv_delete_all);
        TextView ivQuit = (TextView) mRootView.findViewById(R.id.tv_quit);
        lrvList = (LRecyclerView) mRootView.findViewById(R.id.lrv_list);

        models = MediaControler.getIns(mContext).list();
        adapter = new PlayQueueAdapter(mContext, models, R.layout.module_play_adapter_play_queue, this);
        lrvList.setAdapter(adapter);

        final int playMode = p.getPlayMode();
        final int count = models != null ? models.size() : 0;
        tvCount.setText(count > 0 ? String.format(mContext.getResources().getString(
                R.string.module_common_song_unit_format_with_parentheses), count) : "");
        ivPlayMode.setBackgroundResource(Constants.PlayMode.PLAY_MODE_DRAWABLE[playMode]);
        tvPlayMode.setText(mContext.getResources().getString(Constants.PlayMode.PLAY_MODE[playMode]));

        mRootView.setOnClickListener(this);
        llytQueue.setOnClickListener(this);
        flytPlayMode.setOnClickListener(this);
        ivDeleteAll.setOnClickListener(this);
        ivQuit.setOnClickListener(this);
    }

    private int changeMode() {
        int playMode = p.getPlayMode();
        if (++playMode > 3) {
            playMode = 0;
        }
        ivPlayMode.setBackgroundResource(Constants.PlayMode.PLAY_MODE_DRAWABLE[playMode]);
        tvPlayMode.setText(mContext.getResources().getString(Constants.PlayMode.PLAY_MODE[playMode]));
        p.putPlayMode(playMode);
        return playMode;
    }

    @Override
    public void onPlayModeChange(int playMode) {

    }

    @Override
    public void onCountChange(int count) {
        tvCount.setText(count > 0 ? String.format(mContext.getResources().getString(
                R.string.module_common_song_unit_format_with_parentheses), count) : "");
        if (listener != null) {
            listener.onCountChange(count);
        }
    }

    @Override
    public void onClick(View v) {
        if (ClickFast.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.root:
            case R.id.tv_quit:
                dismiss();
                break;

            case R.id.llyt_queue:
                // Do nothing
                break;

            case R.id.flyt_play_mode:
                int playMode = changeMode();
                if (listener != null) {
                    listener.onPlayModeChange(playMode);
                }
                break;

            case R.id.tv_delete_all:
                if (models == null || models.size() <= 0) {
                    return;
                }
                MediaControler controler = MediaControler.getIns(mContext);
                controler.deleteAll();
                models = controler.list();
                adapter.setDatas(models);
                adapter.notifyDataSetChanged();
                onCountChange(models != null ? models.size() : 0);
                break;
        }
    }

    @Override
    public void show() {
        if (!isShowing() && mContext != null && !((Activity) mContext).isFinishing()) {
            showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
        }
    }

    public void setOnQueueListener(PlayQueueAdapter.IQueueListener listener) {
        this.listener = listener;
    }
}