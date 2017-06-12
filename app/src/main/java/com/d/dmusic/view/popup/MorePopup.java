package com.d.dmusic.view.popup;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.d.dmusic.R;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.module.repeatclick.ClickUtil;
import com.d.dmusic.module.repeatclick.OnClickFastListener;
import com.d.dmusic.module.utils.MoreUtil;
import com.d.dmusic.mvp.adapter.MoreAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * MorePopup
 * Created by D on 2017/4/29.
 */
public class MorePopup implements View.OnClickListener {
    public static final int TYPE_SONG_PLAY = 0;
    public static final int TYPE_SONG_SUB = 1;

    private Context context;//must be Activity
    private PopupWindow popupWindow;
    private View rootView;
    private int type;
    private MusicModel model;
    private MoreAdapter adapter;

    public MorePopup(Context context, int type, MusicModel model) {
        this.type = type;
        this.model = model;
        this.context = context;
        rootView = LayoutInflater.from(context).inflate(type == TYPE_SONG_PLAY ? R.layout.popup_more : R.layout.popup_more_light, null);
        popupWindow = new PopupWindow(rootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setClippingEnabled(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        init();
    }

    private void init() {
        RecyclerView rvList0 = (RecyclerView) rootView.findViewById(R.id.rv_list0);
        LinearLayoutManager layoutManager0 = new LinearLayoutManager(context);
        layoutManager0.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvList0.setLayoutManager(layoutManager0);
        adapter = new MoreAdapter(context, getDatas(), type == TYPE_SONG_PLAY ? R.layout.adapter_more : R.layout.adapter_more_light);
        rvList0.setAdapter(adapter);
        rootView.findViewById(R.id.v_blank).setOnClickListener(this);
        rootView.findViewById(R.id.tv_quit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.v_blank:
            case R.id.tv_quit:
                dismiss();
                break;
        }
    }

    public void show() {
        if (popupWindow != null && !popupWindow.isShowing() && context != null && !((Activity) context).isFinishing()) {
            popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        }
    }

    /**
     * 隐藏popupWindow
     */
    public void dismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private List<MoreAdapter.Bean> getDatas() {
        List<MoreAdapter.Bean> datas = new ArrayList<>();
        MoreAdapter.Bean beans[] = new MoreAdapter.Bean[10];
        int icAddList = type == TYPE_SONG_PLAY ? R.drawable.ic_song_addlist_m : R.drawable.ic_song_addlist_lm;
        int icFav = type == TYPE_SONG_PLAY ? R.drawable.ic_song_fav_m : R.drawable.ic_song_fav_lm;
        int icRing = type == TYPE_SONG_PLAY ? R.drawable.ic_song_ring_m : R.drawable.ic_song_ring_lm;
        int icAdjustLrc = type == TYPE_SONG_PLAY ? R.drawable.ic_song_adjust_lrc_m : R.drawable.ic_song_adjust_lrc_lm;
        int icInfo = type == TYPE_SONG_PLAY ? R.drawable.ic_song_info_m : R.drawable.ic_song_info_lm;
        int icDelete = type == TYPE_SONG_PLAY ? R.drawable.ic_song_delete_m : R.drawable.ic_song_delete_lm;
        int icEdit = type == TYPE_SONG_PLAY ? R.drawable.ic_song_edit_m : R.drawable.ic_song_edit_lm;
        int icSearchLrc = type == TYPE_SONG_PLAY ? R.drawable.ic_song_search_lrc_m : R.drawable.ic_song_search_lrc_lm;
        int icModeChange = type == TYPE_SONG_PLAY ? R.drawable.ic_song_edit_m : R.drawable.ic_song_edit_lm;
        int icSetting = type == TYPE_SONG_PLAY ? R.drawable.ic_song_edit_m : R.drawable.ic_song_edit_lm;
        beans[0] = new MoreAdapter.Bean(icAddList, "加到歌单", new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {
                if (model == null) {
                    return;
                }
                dismiss();
                MoreUtil.addToList(context, model, type);
            }
        });
        beans[1] = new MoreAdapter.Bean(icFav, (model != null && model.isCollected) ? "已收藏" : "收藏", new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {
                if (model == null) {
                    return;
                }
                dismiss();
                MoreUtil.collect(context, model, type, true);
            }
        });
        beans[2] = new MoreAdapter.Bean(icRing, "设置铃声", new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {

            }
        });

        if (type == TYPE_SONG_PLAY)
            beans[3] = new MoreAdapter.Bean(icAdjustLrc, "调整歌词", new OnClickFastListener() {
                @Override
                public void onFastClick(View v) {

                }
            });

        beans[4] = new MoreAdapter.Bean(icInfo, "歌曲信息", new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {
                if (model == null) {
                    return;
                }
                dismiss();
                MoreUtil.showInfo(context, model);
            }
        });
        beans[5] = new MoreAdapter.Bean(icDelete, "删除", new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {

            }
        });
        beans[6] = new MoreAdapter.Bean(icEdit, "编辑", new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {

            }
        });

        if (type == TYPE_SONG_PLAY)
            beans[7] = new MoreAdapter.Bean(icSearchLrc, "歌词搜索", new OnClickFastListener() {
                @Override
                public void onFastClick(View v) {

                }
            });

        if (type == TYPE_SONG_PLAY)
            beans[8] = new MoreAdapter.Bean(icModeChange, "模式切换", new OnClickFastListener() {
                @Override
                public void onFastClick(View v) {

                }
            });

        if (type == TYPE_SONG_PLAY)
            beans[9] = new MoreAdapter.Bean(icSetting, "设置", new OnClickFastListener() {
                @Override
                public void onFastClick(View v) {

                }
            });

        for (MoreAdapter.Bean bean : beans) {
            if (bean != null) {
                datas.add(bean);
            }
        }
        return datas;
    }

    public void refresh(MusicModel model) {
        this.model = model;
    }
}
