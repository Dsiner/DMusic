package com.d.lib.xrv.adapter;

/**
 * MultiItemTypeSupport for RecyclerView
 * Created by D on 2017/4/25.
 */
public interface MultiItemTypeSupport<T> {
    public int getLayoutId(int viewType);

    public int getItemViewType(int position, T t);
}
