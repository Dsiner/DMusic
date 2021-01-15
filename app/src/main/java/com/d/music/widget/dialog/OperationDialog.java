package com.d.music.widget.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.d.lib.common.widget.dialog.AbsSheetDialog;
import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.lib.pulllayout.rv.adapter.CommonHolder;
import com.d.music.R;

import java.util.ArrayList;
import java.util.List;

/**
 * OperationDialog
 * Created by D on 2018/8/16.
 */
public class OperationDialog extends AbsSheetDialog<OperationDialog.Bean> {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_NIGHT = 1;

    public int type;

    private OperationDialog(Context context, int type, String title, List<OperationDialog.Bean> datas) {
        super(context);
        this.type = type;
        this.mTitle = title;
        this.mDatas = datas;
        init();
    }

    public static OperationDialog getOperationDialog(Context context, int type, String title, List<OperationDialog.Bean> datas,
                                                     AbsSheetDialog.OnItemClickListener<OperationDialog.Bean> listener) {
        OperationDialog dialog = new OperationDialog(context, type, title, new ArrayList<>(datas));
        dialog.setOnItemClickListener(listener);
        dialog.show();
        return dialog;
    }

    @Override
    protected boolean isInitEnabled() {
        return false;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.module_play_dialog_operation;
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return new OperationDialog.SheetAdapter(mContext, mDatas, R.layout.module_play_adapter_operation);
    }

    @Override
    protected void init() {
        initRecyclerList(R.id.rv_list, LinearLayoutManager.HORIZONTAL);

        TextView tvCancel = (TextView) mRootView.findViewById(R.id.tv_cancel);
        TextView tvTitle = (TextView) mRootView.findViewById(R.id.tv_title);
        if (!TextUtils.isEmpty(mTitle)) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(mTitle);
        } else {
            tvTitle.setVisibility(View.GONE);
        }

        // Change Skin
        mRootView.setBackgroundResource(type == TYPE_NIGHT ?
                R.color.color_popup_more_bg : R.color.color_popup_more_bg_light);
        tvCancel.setTextColor(type == TYPE_NIGHT ? ContextCompat.getColor(mContext, R.color.color_popup_more)
                : ContextCompat.getColor(mContext, R.color.color_popup_more_light));
        mRootView.findViewById(R.id.v_bottom_line).setBackgroundResource(type == TYPE_NIGHT ?
                R.color.color_popup_more_line : R.color.color_popup_more_line_light);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(-1, null);
            }
        });
    }

    public static class Bean {
        public static final int TYPE_ADDLIST = 0;
        public static final int TYPE_FAV = 1;
        public static final int TYPE_RING = 2;
        public static final int TYPE_ADJUSTLRC = 3;
        public static final int TYPE_INFO = 4;
        public static final int TYPE_DELETE = 5;
        public static final int TYPE_EDIT = 6;
        public static final int TYPE_DOWNLOAD = 7;
        public static final int TYPE_SEARCH_LRC = 8;
        public static final int TYPE_CHANGE_MODE = 9;
        public static final int TYPE_SETTING = 10;
        public static final int TYPE_EXIT = 11;


        public int type;
        public String item;
        public int drawble;

        public Bean() {
        }

        public Bean(int type, String item, int drawble) {
            this.type = type;
            this.item = item;
            this.drawble = drawble;
        }

        public Bean item(String item) {
            this.item = item;
            return this;
        }

        public Bean with(Context context, int type, boolean night) {
            context = context.getApplicationContext();
            this.type = type;
            switch (type) {
                case TYPE_ADDLIST:
                    this.item = context.getResources().getString(R.string.module_common_add_to_list);
                    this.drawble = night ? R.drawable.module_common_ic_song_addlist_m : R.drawable.module_common_ic_song_addlist_lm;
                    break;
                case TYPE_FAV:
                    this.item = context.getResources().getString(R.string.module_common_collect);
                    this.drawble = night ? R.drawable.module_common_ic_song_fav_m : R.drawable.module_common_ic_song_fav_lm;
                    break;
                case TYPE_RING:
                    this.item = context.getResources().getString(R.string.module_common_set_ring);
                    this.drawble = night ? R.drawable.module_common_ic_song_ring_m : R.drawable.module_common_ic_song_ring_lm;
                    break;
                case TYPE_ADJUSTLRC:
                    this.item = context.getResources().getString(R.string.module_common_set_lrc);
                    this.drawble = night ? R.drawable.module_common_ic_song_adjust_lrc_m : R.drawable.module_common_ic_song_adjust_lrc_lm;
                    break;
                case TYPE_INFO:
                    this.item = context.getResources().getString(R.string.module_common_song_info);
                    this.drawble = night ? R.drawable.module_common_ic_song_info_m : R.drawable.module_common_ic_song_info_lm;
                    break;
                case TYPE_DELETE:
                    this.item = context.getResources().getString(R.string.module_common_delete);
                    this.drawble = night ? R.drawable.module_common_ic_song_delete_m : R.drawable.module_common_ic_song_delete_lm;
                    break;
                case TYPE_EDIT:
                    this.item = context.getResources().getString(R.string.module_common_edit);
                    this.drawble = night ? R.drawable.module_common_ic_song_edit_m : R.drawable.module_common_ic_song_edit_lm;
                    break;
                case TYPE_DOWNLOAD:
                    this.item = context.getResources().getString(R.string.module_common_download);
                    this.drawble = night ? R.drawable.module_common_ic_song_edit_m : R.drawable.module_common_ic_song_edit_lm;
                    break;
                case TYPE_SEARCH_LRC:
                    this.item = context.getResources().getString(R.string.module_common_search_lrc);
                    this.drawble = night ? R.drawable.module_common_ic_song_search_lrc_m : R.drawable.module_common_ic_song_search_lrc_lm;
                    break;
                case TYPE_CHANGE_MODE:
                    this.item = context.getResources().getString(R.string.module_common_mode_switch);
                    this.drawble = night ? R.drawable.module_common_ic_song_edit_m : R.drawable.module_common_ic_song_edit_lm;
                    break;
                case TYPE_SETTING:
                    this.item = context.getResources().getString(R.string.module_common_setting);
                    this.drawble = night ? R.drawable.module_common_ic_song_edit_m : R.drawable.module_common_ic_song_edit_lm;
                    break;
                case TYPE_EXIT:
                    this.item = context.getResources().getString(R.string.module_common_exit);
                    this.drawble = night ? R.drawable.module_setting_ic_menu_exit : R.drawable.module_setting_ic_menu_exit;
                    break;
            }
            return this;
        }
    }

    public class SheetAdapter extends CommonAdapter<OperationDialog.Bean> {
        SheetAdapter(Context context, List<OperationDialog.Bean> datas, int layoutId) {
            super(context, datas, layoutId);
        }

        @Override
        public void convert(final int position, CommonHolder holder, final OperationDialog.Bean item) {
            // Change Skin
            holder.setBackgroundResource(R.id.iv_item, type == TYPE_NIGHT ?
                    R.drawable.module_common_corner_more : R.drawable.module_common_corner_more_light);
            holder.setTextColor(R.id.tv_item, type == TYPE_NIGHT ?
                    ContextCompat.getColor(mContext, R.color.color_popup_more)
                    : ContextCompat.getColor(mContext, R.color.color_popup_more_light));

            holder.setImageResource(R.id.iv_item, item.drawble);
            holder.setText(R.id.tv_item, item.item);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(position, item);
                }
            });
        }
    }
}