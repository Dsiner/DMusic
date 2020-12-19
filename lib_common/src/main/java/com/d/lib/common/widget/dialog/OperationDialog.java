package com.d.lib.common.widget.dialog;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.d.lib.common.R;
import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.lib.pulllayout.rv.adapter.CommonHolder;

import java.util.List;

/**
 * OperationDialog
 * Created by D on 2018/6/15.
 */
public class OperationDialog extends AbsSheetDialog<OperationDialog.Bean> {
    private TextView tv_title;
    private View v_line_top;
    private boolean mIsChecked;

    public OperationDialog(Context context, String title, List<Bean> datas) {
        super(context, R.style.lib_pub_dialog_style, false, 0, 0, 0);
        this.mTitle = title;
        this.mDatas = datas;
        bindView(mRootView);
        init();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.lib_pub_dialog_operation;
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        int adapterLayoutId = R.layout.lib_pub_adapter_dlg_bottom_ver;
        if (mDatas != null && mDatas.size() > 0) {
            int size = mDatas.size();
            for (int i = 0; i < size; i++) {
                if (mDatas.get(i).isChecked) {
                    adapterLayoutId = R.layout.lib_pub_adapter_dlg_bottom_ver_check;
                    mIsChecked = true;
                    break;
                }
            }
        }
        return new SheetAdapter(mContext, mDatas, adapterLayoutId);
    }

    @Override
    protected void bindView(View rootView) {
        tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        v_line_top = rootView.findViewById(R.id.line_top);
        TextView tv_cancel = (TextView) rootView.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(-1, null);
            }
        });
    }

    @Override
    protected void init() {
        if (!TextUtils.isEmpty(mTitle)) {
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(mTitle);
            v_line_top.setVisibility(View.VISIBLE);
        } else {
            tv_title.setVisibility(View.GONE);
            v_line_top.setVisibility(View.GONE);
        }
        initRecyclerList(R.id.rv_list, LinearLayoutManager.VERTICAL);
    }

    public class SheetAdapter extends CommonAdapter<Bean> {
        SheetAdapter(Context context, List<Bean> datas, int layoutId) {
            super(context, datas, layoutId);
        }

        @Override
        public void convert(final int position, CommonHolder holder, final Bean item) {
            holder.setText(R.id.tv_item, item.item);
            holder.setTextColor(R.id.tv_item, ContextCompat.getColor(mContext, item.color));
            holder.setVisibility(R.id.iv_check, item.isChecked ? View.VISIBLE : View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mIsChecked) {
                        item.isChecked = true;
                        notifyDataSetChanged();
                    }
                    onItemClick(position, item);
                }
            });
        }
    }

    public static class Bean {
        public String item;
        public int color;
        public boolean isChecked;

        public Bean(String item, int color, boolean isChecked) {
            this.item = item;
            this.color = color;
            this.isChecked = isChecked;
        }
    }
}
