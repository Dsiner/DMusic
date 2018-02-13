package com.d.lib.xrv.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.d.lib.xrv.itemtouchhelper.ItemTouchHelperViewHolder;

public class CommonHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
    private Context mContext;
    private SparseArray<View> mViews;
    private View mConvertView;
    public int mLayoutId;
    private ItemTouchHelperViewHolder itemTouchListener;

    public CommonHolder(Context context, View itemView, int layoutId) {
        super(itemView);
        mContext = context;
        mConvertView = itemView;
        mLayoutId = layoutId;
        mViews = new SparseArray<View>();
    }

    public static CommonHolder createViewHolder(Context context, View itemView, int layoutId) {
        return new CommonHolder(context, itemView, layoutId);
    }

    public static CommonHolder createViewHolder(Context context, ViewGroup parent, int layoutId) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new CommonHolder(context, itemView, layoutId);
    }

    /**
     * 通过viewId获取控件
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 设置文本控件Text值
     */
    public CommonHolder setText(int viewId, String text) {
        TextView textView = getView(viewId);
        textView.setText(text);
        return this;
    }

    /**
     * 设置Button是否可用
     */
    public CommonHolder setEnable(int viewId, boolean enable) {
        Button btn = getView(viewId);
        btn.setEnabled(enable);
        return this;
    }

    /**
     * 设置Checkbox是否选中
     */
    public CommonHolder setChecked(int viewId, boolean checked) {
        CheckBox checkBox = getView(viewId);
        checkBox.setChecked(checked);
        return this;
    }

    /**
     * 设置控件显示隐藏
     */
    public CommonHolder setViewVisibility(int viewId, int visibility) {
        View view = getView(viewId);
        view.setVisibility(visibility);
        return this;
    }

    public CommonHolder setViewOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    public CommonHolder setViewTag(int viewId, Object tag) {
        View view = getView(viewId);
        view.setTag(tag);
        return this;
    }

    public Object getViewTag(int viewId) {
        return getView(viewId).getTag();
    }

    /**
     * 设置ImageView res图片
     */
    public CommonHolder setImageResource(int viewId, int resId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resId);
        return this;
    }

    /**
     * 设置ImageView bitmap
     */
    public CommonHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView imageView = getView(viewId);
        imageView.setImageBitmap(bitmap);
        return this;
    }

    /**
     * 设置View Background
     */
    public CommonHolder setBackground(int viewId, int res) {
        View view = getView(viewId);
        view.setBackgroundResource(res);
        return this;
    }

    /**
     * 设置TextView的字体颜色
     */
    public CommonHolder setTextColor(int viewId, int res) {
        TextView textView = getView(viewId);
        textView.setTextColor(res);
        return this;
    }

    /**
     * 3-1:Just for ItemTouch (optional)
     */
    public void setOnItemTouchListener(ItemTouchHelperViewHolder listener) {
        this.itemTouchListener = listener;
    }

    /**
     * 3-2:Just for ItemTouch
     */
    @Override
    public void onItemSelected() {
        if (itemTouchListener != null) {
            itemTouchListener.onItemSelected();
        }
    }

    /**
     * 3-3:Just for ItemTouch
     */
    @Override
    public void onItemClear() {
        if (itemTouchListener != null) {
            itemTouchListener.onItemClear();
        }
    }
}
