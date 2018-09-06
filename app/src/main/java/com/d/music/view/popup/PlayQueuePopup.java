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
import com.d.music.common.Constants;
import com.d.music.common.preferences.Preferences;
import com.d.music.component.greendao.bean.MusicModel;
import com.d.music.component.media.controler.MediaControler;
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
        p = Preferences.getIns(context.getApplicationContext());
        LinearLayout llytQueue = (LinearLayout) rootView.findViewById(R.id.llyt_queue);
        FrameLayout flytPlayMode = (FrameLayout) rootView.findViewById(R.id.flyt_play_mode);
        ivPlayMode = (ImageView) rootView.findViewById(R.id.iv_play_mode);
        tvPlayMode = (TextView) rootView.findViewById(R.id.tv_play_mode);
        tvCount = (TextView) rootView.findViewById(R.id.tv_count);
        TextView ivDeleteAll = (TextView) rootView.findViewById(R.id.tv_delete_all);
        TextView ivQuit = (TextView) rootView.findViewById(R.id.tv_quit);
        lrvList = (LRecyclerView) rootView.findViewById(R.id.lrv_list);

        models = MediaControler.getIns(context).list();
        adapter = new PlayQueueAdapter(context, models, R.layout.module_play_adapter_play_queue, this);
        lrvList.setAdapter(adapter);

        int playMode = p.getPlayMode();
        tvCount.setText("(" + (models != null ? models.size() : 0) + "首)");
        ivPlayMode.setBackgroundResource(Constants.PlayMode.PLAY_MODE_DRAWABLE[playMode]);
        tvPlayMode.setText(Constants.PlayMode.PLAY_MODE[playMode]);

        rootView.setOnClickListener(this);
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
        tvPlayMode.setText(Constants.PlayMode.PLAY_MODE[playMode]);
        p.putPlayMode(playMode);
        return playMode;
    }

    @Override
    public void onPlayModeChange(int playMode) {

    }

    @Override
    public void onCountChange(int count) {
        tvCount.setText("(" + count + "首)");
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
                //do nothing
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
                MediaControler controler = MediaControler.getIns(context);
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
        if (!isShowing() && context != null && !((Activity) context).isFinishing()) {
            showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        }
    }

    public void setOnQueueListener(PlayQueueAdapter.IQueueListener listener) {
        this.listener = listener;
    }
}