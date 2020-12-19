package com.d.lib.common.component.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.d.lib.pulllayout.lv.adapter.CommonHolder;
import com.d.lib.pulllayout.lv.adapter.MultiItemTypeSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * CommonPagerAdapter for ViewPager
 * Created by D on 2018/1/25.
 */
public abstract class CommonPagerAdapter<T> extends PagerAdapter {
    protected Context mContext;
    @NonNull
    protected List<T> mDatas;
    protected int mLayoutId;
    protected MultiItemTypeSupport<T> mMultiItemTypeSupport;

    public CommonPagerAdapter(@NonNull Context context, List<T> datas, int layoutId) {
        mContext = context;
        mDatas = datas != null ? new ArrayList<>(datas) : new ArrayList<T>();
        mLayoutId = layoutId;
    }

    public CommonPagerAdapter(@NonNull Context context, List<T> datas, MultiItemTypeSupport<T> multiItemTypeSupport) {
        mContext = context;
        mDatas = datas != null ? new ArrayList<>(datas) : new ArrayList<T>();
        mMultiItemTypeSupport = multiItemTypeSupport;
    }

    public List<T> getDatas() {
        return mDatas;
    }

    public void setDatas(List<T> datas) {
        if (mDatas != null && datas != null) {
            mDatas.clear();
            mDatas.addAll(datas);
        }
    }

    @Override
    public int getCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    public int getItemViewType(int position) {
        if (mMultiItemTypeSupport != null) {
            return mMultiItemTypeSupport.getItemViewType(position, mDatas.get(position));
        }
        return 0;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        int layoutId = mLayoutId;
        if (mMultiItemTypeSupport != null) {
            // MultiType
            layoutId = mMultiItemTypeSupport.getLayoutId(getItemViewType(position));
        }
        CommonHolder holder = CommonHolder.create(mContext, container, layoutId);
        onViewHolderCreated(holder, holder.itemView);
        convert(position, holder, mDatas.get(position));
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

    public void onViewHolderCreated(CommonHolder holder, View itemView) {
    }

    /**
     * @param position The position of the item within the adapter's data set.
     * @param holder   Holder
     * @param item     Data
     */
    public abstract void convert(int position, CommonHolder holder, T item);
}
