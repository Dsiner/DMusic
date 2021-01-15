package com.d.music.widget.popup;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.d.lib.common.component.quickclick.QuickClick;
import com.d.lib.common.widget.popup.AbstractPopup;
import com.d.music.R;
import com.d.music.component.media.controler.MediaControl;
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
    private Preferences mPreferences;
    private ImageView iv_play_mode;
    private TextView tv_play_mode;
    private TextView tv_count;
    private RecyclerView rv_list;
    private PlayQueueAdapter mAdapter;
    private PlayQueueAdapter.IQueueListener mListener;
    private List<MusicModel> mModels;

    public PlayQueuePopup(Context context) {
        super(context, R.layout.module_play_popup_play_queue, R.style.AnimBottom);
    }

    @Override
    protected void init() {
        mPreferences = Preferences.getInstance(mContext.getApplicationContext());
        LinearLayout llyt_queue = (LinearLayout) mRootView.findViewById(R.id.llyt_queue);
        FrameLayout flyt_play_mode = (FrameLayout) mRootView.findViewById(R.id.flyt_play_mode);
        iv_play_mode = (ImageView) mRootView.findViewById(R.id.iv_play_mode);
        tv_play_mode = (TextView) mRootView.findViewById(R.id.tv_play_mode);
        tv_count = (TextView) mRootView.findViewById(R.id.tv_count);
        TextView tv_delete_all = (TextView) mRootView.findViewById(R.id.tv_delete_all);
        TextView tv_quit = (TextView) mRootView.findViewById(R.id.tv_quit);
        rv_list = mRootView.findViewById(R.id.rv_list);

        mModels = MediaControl.getInstance(mContext).list();
        mAdapter = new PlayQueueAdapter(mContext, mModels, R.layout.module_play_adapter_play_queue, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_list.setLayoutManager(layoutManager);
        rv_list.setAdapter(mAdapter);

        final int playMode = mPreferences.getPlayMode();
        final int count = mModels != null ? mModels.size() : 0;
        tv_count.setText(count > 0 ? String.format(mContext.getResources().getString(
                R.string.module_common_song_unit_format_with_parentheses), count) : "");
        iv_play_mode.setBackgroundResource(Constants.PlayMode.PLAY_MODE_DRAWABLE[playMode]);
        tv_play_mode.setText(mContext.getResources().getString(Constants.PlayMode.PLAY_MODE[playMode]));

        mRootView.setOnClickListener(this);
        llyt_queue.setOnClickListener(this);
        flyt_play_mode.setOnClickListener(this);
        tv_delete_all.setOnClickListener(this);
        tv_quit.setOnClickListener(this);
    }

    private int changeMode() {
        int playMode = mPreferences.getPlayMode();
        if (++playMode > 3) {
            playMode = 0;
        }
        iv_play_mode.setBackgroundResource(Constants.PlayMode.PLAY_MODE_DRAWABLE[playMode]);
        tv_play_mode.setText(mContext.getResources().getString(Constants.PlayMode.PLAY_MODE[playMode]));
        mPreferences.putPlayMode(playMode);
        return playMode;
    }

    @Override
    public void onPlayModeChange(int playMode) {

    }

    @Override
    public void onCountChange(int count) {
        tv_count.setText(count > 0 ? String.format(mContext.getResources().getString(
                R.string.module_common_song_unit_format_with_parentheses), count) : "");
        if (mListener != null) {
            mListener.onCountChange(count);
        }
    }

    @Override
    public void onClick(View v) {
        if (QuickClick.isQuickClick()) {
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
                if (mListener != null) {
                    mListener.onPlayModeChange(playMode);
                }
                break;

            case R.id.tv_delete_all:
                if (mModels == null || mModels.size() <= 0) {
                    return;
                }
                MediaControl controler = MediaControl.getInstance(mContext);
                controler.deleteAll();
                mModels = controler.list();
                mAdapter.setDatas(mModels);
                mAdapter.notifyDataSetChanged();
                onCountChange(mModels != null ? mModels.size() : 0);
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
        this.mListener = listener;
    }
}