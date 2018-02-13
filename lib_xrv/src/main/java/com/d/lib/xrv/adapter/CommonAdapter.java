package com.d.lib.xrv.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.d.lib.xrv.itemtouchhelper.ItemTouchHelperAdapter;
import com.d.lib.xrv.itemtouchhelper.OnStartDragListener;

import java.util.ArrayList;
import java.util.List;

/**
 * CommonAdapter for RecyclerView
 * Created by D on 2017/4/25.
 */
public abstract class CommonAdapter<T> extends RecyclerView.Adapter<CommonHolder> implements ItemTouchHelperAdapter {
    protected Context mContext;
    protected List<T> mDatas;
    protected int mLayoutId;
    protected MultiItemTypeSupport<T> multiItemTypeSupport;
    protected OnStartDragListener startDragListener;

    public CommonAdapter(Context context, List<T> datas, int layoutId) {
        mContext = context;
        mDatas = datas == null ? new ArrayList<T>() : datas;
        mLayoutId = layoutId;
    }

    public CommonAdapter(Context context, List<T> datas, MultiItemTypeSupport<T> multiItemTypeSupport) {
        mContext = context;
        mDatas = datas == null ? new ArrayList<T>() : datas;
        this.multiItemTypeSupport = multiItemTypeSupport;
    }

    public void setDatas(List<T> datas) {
        if (mDatas != null && datas != null) {
            mDatas.clear();
            mDatas.addAll(datas);
        }
    }

    public List<T> getDatas() {
        return mDatas;
    }

    @Override
    public int getItemViewType(int position) {
        if (multiItemTypeSupport != null) {
            return multiItemTypeSupport.getItemViewType(position, position < mDatas.size() ? mDatas.get(position) : null);
        }
        return super.getItemViewType(position);
    }

    @Override
    public CommonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = mLayoutId;
        if (multiItemTypeSupport != null) {
            //multiType
            if (mDatas != null && mDatas.size() > 0) {
                layoutId = multiItemTypeSupport.getLayoutId(viewType);
            }
        }
        CommonHolder holder = CommonHolder.createViewHolder(mContext, parent, layoutId);
        onViewHolderCreated(holder, holder.getConvertView());
        return holder;
    }

    @Override
    public void onBindViewHolder(CommonHolder holder, int position) {
        convert(position, holder, mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public void onViewHolderCreated(CommonHolder holder, View itemView) {
    }

    /**
     * @param position:position
     * @param holder:holder
     * @param item:position对应的数据item
     */
    public abstract void convert(int position, CommonHolder holder, T item);

    /**
     * 3-1:Just for ItemTouch (optional)
     */
    public void setOnStartDragListener(OnStartDragListener startDragListener) {
        this.startDragListener = startDragListener;
    }

    /**
     * 3-2:Just for ItemTouch (optional)
     */
    @Override
    public void onItemDismiss(int position) {
    }

    /**
     * 3-3:Just for ItemTouch (optional)
     */
    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }
}
