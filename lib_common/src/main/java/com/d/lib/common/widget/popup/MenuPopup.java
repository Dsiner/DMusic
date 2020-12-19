package com.d.lib.common.widget.popup;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.d.lib.common.R;
import com.d.lib.common.util.DimenUtils;
import com.d.lib.common.util.ViewHelper;
import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.lib.pulllayout.rv.adapter.CommonHolder;

import java.util.ArrayList;
import java.util.List;

public class MenuPopup extends AbstractPopup {
    private RecyclerView rv_list;
    private List<Bean> mDatas;
    private OnMenuListener mListener;

    public MenuPopup(Context context, List<Bean> datas) {
        super(context, R.layout.lib_pub_popup_menu,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true, 0);
        mDatas = datas != null ? datas : new ArrayList<Bean>();
        bindView(mRootView);
        init();
    }

    private RecyclerView.Adapter getAdapter() {
        return new SheetAdapter(mContext, mDatas, R.layout.lib_pub_adapter_popup_menu);
    }

    @Override
    protected boolean isInitEnabled() {
        return false;
    }

    @Override
    protected void bindView(View rootView) {
        rv_list = ViewHelper.findViewById(rootView, R.id.rv_list);
    }

    @Override
    protected void init() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_list.setLayoutManager(layoutManager);
        rv_list.setAdapter(getAdapter());
    }

    @Override
    public void showAsDropDown(View anchor) {
        if (!isShowing() && mContext != null && !((Activity) mContext).isFinishing()) {
            int[] measuredSize = getMeasuredSizeOfContent();
            int width = measuredSize[0];
            int height = measuredSize[1] + DimenUtils.dp2px(mContext, 6);
            setWidth(width);
            setHeight(height);
            super.showAsDropDown(anchor, -(width - DimenUtils.dp2px(mContext, 45)), 0);
        }
    }

    private int[] getMeasuredSizeOfContent() {
        int width = 0;
        int height = 0;
        CommonAdapter adapter = (CommonAdapter) getAdapter();
        int count = adapter.getItemCount();
        for (int position = 0; position < count; position++) {
            CommonHolder holder = adapter.onCreateViewHolder(rv_list,
                    adapter.getItemViewType(position));
            adapter.onBindViewHolder(holder, position);
            View view = holder.itemView;
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            if (view.getMeasuredWidth() > width) {
                width = view.getMeasuredWidth();
            }
            height += view.getMeasuredHeight();
        }
        return new int[]{width, height};
    }

    protected void onItemClick(int position, String item) {
        dismiss();
        if (mListener != null) {
            mListener.onClick(this, position, item);
        }
    }

    public class SheetAdapter extends CommonAdapter<Bean> {
        SheetAdapter(Context context, List<Bean> datas, int layoutId) {
            super(context, datas, layoutId);
        }

        @Override
        public void convert(final int position, CommonHolder holder, final Bean item) {
            holder.setVisibility(R.id.v_menu_line, position != 0 ? View.VISIBLE : View.GONE);
            holder.setText(R.id.tv_menu_item, item.item);
            holder.setTextColor(R.id.tv_menu_item, ContextCompat.getColor(mContext, item.color));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(position, item.item);
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

    public interface OnMenuListener {

        /**
         * Click item
         *
         * @param position From 0 to datas.size()-1;
         */
        void onClick(PopupWindow popup, int position, String item);
    }

    public void setOnMenuListener(OnMenuListener listener) {
        this.mListener = listener;
    }
}
