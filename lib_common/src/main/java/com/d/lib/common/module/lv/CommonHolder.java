package com.d.lib.common.module.lv;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * CommonHolder for ListView
 * Created by D on 2017/4/25.
 */
public class CommonHolder {
    private final SparseArray<View> mViews;
    private View mConvertView;
    private int position;
    public int mLayoutId;

    private CommonHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.position = position;
        this.mLayoutId = layoutId;
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }

    /**
     * 获取CommonHolder对象
     *
     * @param context
     * @param convertView
     * @param parent
     * @param layoutId
     * @param position
     * @return
     */
    public static CommonHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new CommonHolder(context, parent, layoutId, position);
        }
        CommonHolder holder = (CommonHolder) convertView.getTag();
        if (holder.mLayoutId != layoutId) {
            return new CommonHolder(context, parent, layoutId, position);
        }
        holder.position = position;
        return holder;
    }

    public int getPosition() {
        return position;
    }

    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 根据控件id获取控件
     *
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 设置文本控件Text值
     *
     * @param viewId
     * @param text
     * @return
     */
    public CommonHolder setText(int viewId, CharSequence text) {
        TextView textView = getView(viewId);
        textView.setText(text);
        return this;
    }

    /**
     * 设置Button是否可用
     *
     * @param viewId
     * @param enable
     * @return
     */
    public CommonHolder setEnable(int viewId, boolean enable) {
        Button btn = getView(viewId);
        btn.setEnabled(enable);
        return this;
    }

    /**
     * 设置Checkbox是否选中
     *
     * @param viewId
     * @param checked
     * @return
     */
    public CommonHolder setChecked(int viewId, boolean checked) {
        CheckBox checkBox = getView(viewId);
        checkBox.setChecked(checked);
        return this;
    }

    /**
     * 设置控件显示隐藏
     *
     * @param viewId
     * @param visibility
     * @return
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
     *
     * @param viewId
     * @param resId
     * @return
     */
    public CommonHolder setImageResource(int viewId, int resId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resId);
        return this;
    }

    /**
     * 设置ImageView bitmap
     *
     * @param viewId
     * @param bitmap
     * @return
     */
    public CommonHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView imageView = getView(viewId);
        imageView.setImageBitmap(bitmap);
        return this;
    }

    /**
     * 设置View Background
     *
     * @param viewId
     * @param res
     * @return
     */
    public CommonHolder setBackground(int viewId, int res) {
        View view = getView(viewId);
        view.setBackgroundResource(res);
        return this;
    }

    /**
     * 设置背景
     *
     * @param viewId
     * @param color
     * @return
     */
    public CommonHolder setBackgroundColor(int viewId, int color) {
        View view = getView(viewId);
        view.setBackgroundColor(color);
        return this;
    }

    /**
     * 设置TextView的字体颜色
     *
     * @param viewId
     * @param res
     * @return
     */
    public CommonHolder setTextColor(int viewId, int res) {
        TextView textView = getView(viewId);
        textView.setTextColor(res);
        return this;
    }
}
