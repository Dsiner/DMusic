package com.d.dmusic.view.popup;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d.dmusic.R;
import com.d.dmusic.api.IQueueListener;
import com.d.dmusic.commen.Preferences;
import com.d.dmusic.module.global.Cst;
import com.d.dmusic.module.global.MusicCst;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.module.repeatclick.ClickUtil;
import com.d.dmusic.module.service.MusicService;
import com.d.dmusic.mvp.adapter.PlayQueueAdapter;
import com.d.lib.xrv.LRecyclerView;

import java.util.List;

/**
 * PlayQueuePopup
 * Created by D on 2017/4/29.
 */
public class PlayQueuePopup extends AbstractPopup implements View.OnClickListener, IQueueListener {
    private Preferences p;
    private ImageView ivPlayMode;
    private TextView tvPlayMode;
    private TextView tvCount;
    private LRecyclerView lrvList;
    private PlayQueueAdapter adapter;
    private IQueueListener listener;
    private List<MusicModel> models;

    public PlayQueuePopup(Context context) {
        super(context, R.style.AnimBottom);
    }

    @Override
    protected void init() {
        p = Preferences.getInstance(context.getApplicationContext());
        View vBlank = rootView.findViewById(R.id.v_queue_blank);
        LinearLayout llytQueue = (LinearLayout) rootView.findViewById(R.id.llyt_queue);
        ViewGroup.LayoutParams lp = llytQueue.getLayoutParams();
        lp.height = (int) (Cst.SCREEN_HEIGHT * 0.618f);
        llytQueue.setLayoutParams(lp);
        FrameLayout flytPlayMode = (FrameLayout) rootView.findViewById(R.id.flyt_play_mode);
        ivPlayMode = (ImageView) rootView.findViewById(R.id.iv_play_mode);
        tvPlayMode = (TextView) rootView.findViewById(R.id.tv_play_mode);
        tvCount = (TextView) rootView.findViewById(R.id.tv_count);
        TextView ivDeleteAll = (TextView) rootView.findViewById(R.id.tv_delete_all);
        TextView ivQuit = (TextView) rootView.findViewById(R.id.tv_quit);
        lrvList = (LRecyclerView) rootView.findViewById(R.id.lrv_list);

        models = MusicService.getControl().getModels();
        adapter = new PlayQueueAdapter(context, models, R.layout.adapter_play_queue, this);
        lrvList.setAdapter(adapter);

        int playMode = p.getPlayMode();
        tvCount.setText("(" + (models != null ? models.size() : 0) + "首)");
        ivPlayMode.setBackgroundResource(MusicCst.PLAY_MODE_DRAWABLE[playMode]);
        tvPlayMode.setText(MusicCst.PLAY_MODE[playMode]);

        vBlank.setOnClickListener(this);
        flytPlayMode.setOnClickListener(this);
        ivDeleteAll.setOnClickListener(this);
        ivQuit.setOnClickListener(this);
    }

    private int changeMode() {
        int playMode = p.getPlayMode();
        if (++playMode > 3) {
            playMode = 0;
        }
        ivPlayMode.setBackgroundResource(MusicCst.PLAY_MODE_DRAWABLE[playMode]);
        tvPlayMode.setText(MusicCst.PLAY_MODE[playMode]);
        p.putPlayMode(playMode);
        return playMode;
    }

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.flyt_play_mode:
                int playMode = changeMode();
                if (listener != null) {
                    listener.onPlayModeChange(playMode);
                }
                break;
            case R.id.tv_delete_all:
                MusicService.getControl().delelteAll();
                adapter.notifyDataSetChanged();
                tvCount.setText("(" + 0 + "首)");
                break;
            case R.id.v_queue_blank:
            case R.id.tv_quit:
                dismiss();
                break;
        }
    }

    @Override
    public void show() {
        if (popupWindow != null && !popupWindow.isShowing() && context != null && !((Activity) context).isFinishing()) {
            popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.popup_play_queue;
    }

    @Override
    public void onPlayModeChange(int playMode) {

    }

    @Override
    public void onCountChange(int count) {
        tvCount.setText(count + "首");
    }

    public void setOnQueueListener(IQueueListener listener) {
        this.listener = listener;
    }
}