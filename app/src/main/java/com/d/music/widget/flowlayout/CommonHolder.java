package com.d.music.widget.flowlayout;

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

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * CommonHolder for ListView
 * Created by D on 2017/4/25.
 */
public class CommonHolder {
    public final View itemView;
    public final int layoutId;
    private final SparseArray<View> mViews;
    int mItemViewType = -1;

    private CommonHolder(View itemView, int layoutId) {
        this.itemView = itemView;
        this.layoutId = layoutId;
        this.mViews = new SparseArray<>();
    }

    @NonNull
    public static CommonHolder create(@NonNull Context context,
                                      ViewGroup parent,
                                      int layoutId) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new CommonHolder(itemView, layoutId);
    }

    /**
     * @return The view type of this ViewHolder.
     */
    public final int getItemViewType() {
        return mItemViewType;
    }

    /**
     * Finds the first descendant view with the given ID
     */
    public <T extends View> T getView(@IdRes int id) {
        View view = mViews.get(id);
        if (view == null) {
            view = itemView.findViewById(id);
            mViews.put(id, view);
        }
        return (T) view;
    }

    /**
     * Sets the text to be displayed
     */
    public CommonHolder setText(@IdRes int id, CharSequence text) {
        TextView view = getView(id);
        view.setText(text);
        return this;
    }

    /**
     * Changes the enabled state of this button.
     */
    public CommonHolder setEnable(@IdRes int id, boolean enable) {
        Button view = getView(id);
        view.setEnabled(enable);
        return this;
    }

    /**
     * Changes the checked state of this button.
     */
    public CommonHolder setChecked(@IdRes int id, boolean checked) {
        CheckBox view = getView(id);
        view.setChecked(checked);
        return this;
    }

    /**
     * Set the visibility state of this view.
     */
    public CommonHolder setVisibility(@IdRes int id, int visibility) {
        View view = getView(id);
        view.setVisibility(visibility);
        return this;
    }

    public CommonHolder setOnClickListener(@IdRes int id, @Nullable View.OnClickListener l) {
        View view = getView(id);
        view.setOnClickListener(l);
        return this;
    }

    public CommonHolder setTag(@IdRes int id, Object tag) {
        View view = getView(id);
        view.setTag(tag);
        return this;
    }

    public Object getTag(@IdRes int id) {
        return getView(id).getTag();
    }

    /**
     * Sets a drawable as the content of this ImageView.
     */
    public CommonHolder setImageResource(@IdRes int id, @DrawableRes int resId) {
        ImageView view = getView(id);
        view.setImageResource(resId);
        return this;
    }

    /**
     * Sets a Bitmap as the content of this ImageView.
     */
    public CommonHolder setImageBitmap(@IdRes int id, Bitmap bitmap) {
        ImageView view = getView(id);
        view.setImageBitmap(bitmap);
        return this;
    }

    /**
     * Set the background to a given resource.
     */
    public CommonHolder setBackgroundResource(@IdRes int id, @DrawableRes int resId) {
        View view = getView(id);
        view.setBackgroundResource(resId);
        return this;
    }

    /**
     * Sets the background color for this view.
     */
    public CommonHolder setBackgroundColor(@IdRes int id, @ColorInt int color) {
        View view = getView(id);
        view.setBackgroundColor(color);
        return this;
    }

    /**
     * Sets the text color for all the states (normal, selected,
     * focused) to be this color.
     */
    public CommonHolder setTextColor(@IdRes int id, @ColorInt int color) {
        TextView view = getView(id);
        view.setTextColor(color);
        return this;
    }
}
