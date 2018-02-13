package com.d.lib.xrv.listener;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.d.lib.xrv.XRecyclerView;
import com.d.lib.xrv.adapter.WrapAdapter;

/**
 * AdapterDataObserver for RecyclerView
 * Created by D on 2017/4/25.
 */

public class DataObserver extends RecyclerView.AdapterDataObserver {
    private XRecyclerView xRecyclerView;
    private WrapAdapter mWrapAdapter;
    private View mEmptyView;
    private boolean canLoadMore = true;

    public void setArgs(XRecyclerView xRecyclerView,
                        WrapAdapter mWrapAdapter,
                        View mEmptyView,
                        boolean enabled) {
        this.xRecyclerView = xRecyclerView;
        this.mWrapAdapter = mWrapAdapter;
        this.mEmptyView = mEmptyView;
        this.canLoadMore = enabled;
    }

    @Override
    public void onChanged() {
        if (mWrapAdapter != null) {
            mWrapAdapter.notifyDataSetChanged();
        }
        if (xRecyclerView != null && mWrapAdapter != null && mEmptyView != null) {
            int emptyCount = 1 + mWrapAdapter.getHeadersCount();
            if (canLoadMore) {
                emptyCount++;
            }
            if (mWrapAdapter.getItemCount() == emptyCount) {
                mEmptyView.setVisibility(View.VISIBLE);
                xRecyclerView.setVisibility(View.GONE);
            } else {
                mEmptyView.setVisibility(View.GONE);
                xRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        if (mWrapAdapter == null) {
            return;
        }
        mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
        if (mWrapAdapter == null) {
            return;
        }
        mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
        if (mWrapAdapter == null) {
            return;
        }
        mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        if (mWrapAdapter == null) {
            return;
        }
        mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        if (mWrapAdapter == null) {
            return;
        }
        mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
    }
}