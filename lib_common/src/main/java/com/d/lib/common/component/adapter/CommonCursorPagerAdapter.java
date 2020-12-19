package com.d.lib.common.component.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.d.lib.common.util.IOUtils;
import com.d.lib.pulllayout.lv.adapter.CommonHolder;
import com.d.lib.pulllayout.lv.adapter.MultiItemTypeSupport;

/**
 * CommonCursorPagerAdapter for ViewPager
 * Created by D on 2018/1/25.
 */
public abstract class CommonCursorPagerAdapter extends PagerAdapter {
    protected Context mContext;
    protected int mLayoutId;
    protected MultiItemTypeSupport<Cursor> mMultiItemTypeSupport;
    protected Cursor mCursor;

    public CommonCursorPagerAdapter(@NonNull Context context, int layoutId) {
        mContext = context;
        mLayoutId = layoutId;
    }

    public CommonCursorPagerAdapter(@NonNull Context context, MultiItemTypeSupport<Cursor> multiItemTypeSupport) {
        mContext = context;
        mMultiItemTypeSupport = multiItemTypeSupport;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public void setCursor(Cursor cursor) {
        setCursor(cursor, false);
    }

    public void setCursor(Cursor cursor, boolean close) {
        if (mCursor == cursor) {
            return;
        }
        final Cursor oldCursor = mCursor;
        mCursor = cursor;
        notifyDataSetChanged();
        if (close) {
            IOUtils.closeQuietly(oldCursor);
        }
    }

    protected boolean isDataValid(Cursor cursor) {
        return cursor != null && !cursor.isClosed();
    }

    @Override
    public int getCount() {
        if (isDataValid(mCursor)) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    public int getItemViewType(int position) {
        if (mMultiItemTypeSupport != null) {
            if (!mCursor.moveToPosition(position)) {
                throw new IllegalStateException("Could not move cursor to position " + position
                        + " when trying to get item view type.");
            }
            return mMultiItemTypeSupport.getItemViewType(position, mCursor);
        }
        return 0;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        if (!isDataValid(mCursor)) {
            throw new IllegalStateException("Cannot bind instantiate item when cursor is in invalid state.");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Could not move cursor to position " + position
                    + " when trying to instantiate item");
        }
        int layoutId = mLayoutId;
        if (mMultiItemTypeSupport != null) {
            // MultiType
            layoutId = mMultiItemTypeSupport.getLayoutId(getItemViewType(position));
        }
        CommonHolder holder = CommonHolder.create(mContext, container, layoutId);
        convert(position, holder, mCursor);
        container.addView(holder.itemView);
        return holder.itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /**
     * @param position The position of the item within the adapter's data set.
     * @param holder   Holder
     * @param cursor   Cursor
     */
    public abstract void convert(int position, CommonHolder holder, Cursor cursor);
}
