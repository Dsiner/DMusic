package com.d.lib.xrv;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import com.d.lib.xrv.adapter.WrapAdapter;
import com.d.lib.xrv.listener.AppBarStateChangeListener;
import com.d.lib.xrv.listener.DataObserver;
import com.d.lib.xrv.listener.IRecyclerView;
import com.d.lib.xrv.view.ArrowRefreshHeader;
import com.d.lib.xrv.view.ListState;
import com.d.lib.xrv.view.LoadingMoreFooter;

import java.util.ArrayList;
import java.util.List;

public class XRecyclerView extends ARecyclerView implements LoadingMoreFooter.OnRetryListener {

    private boolean isLoadingData = false;
    private boolean isNoMore = false;
    private boolean isLoadMoreError = false;
    private boolean canRefresh = true;
    private boolean canLoadMore = true;
    private float mLastY = -1;
    private AppBarStateChangeListener.State appbarState = AppBarStateChangeListener.State.EXPANDED;
    private IRecyclerView.LoadingListener listener;

    private /*static*/ List<Integer> headerTypes;//每个header必须有不同的type,不然滚动的时候顺序会变化
    private ArrayList<View> headers;
    private ArrowRefreshHeader refreshHeader;
    private View footer;
    private View emptyView;//adapter没有数据的时候显示,类似于listView的emptyView
    private WrapAdapter wrapAdapter;

    private DataObserver dataObserver;

    public XRecyclerView(Context context) {
        this(context, null);
    }

    public XRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context) {
        headers = new ArrayList<>();
        headerTypes = new ArrayList<>();
        dataObserver = new DataObserver();
    }

    @Override
    public void installHeader() {
        refreshHeader = new ArrowRefreshHeader(getContext());
    }

    @Override
    public void installFooter() {
        LoadingMoreFooter footView = new LoadingMoreFooter(getContext());
        footView.setOnRetryListener(this);
        footer = footView;
        footer.setVisibility(GONE);
    }

    public void addHeaderView(View view) {
        headerTypes.add(IRecyclerView.HEADER_INIT_INDEX + headers.size());
        headers.add(view);
        synchronizeArgs();
        if (wrapAdapter != null) {
            wrapAdapter.notifyDataSetChanged();
        }
    }

    public void loadMoreComplete() {
        isLoadingData = false;
        isLoadMoreError = false;
        if (footer instanceof LoadingMoreFooter) {
            ((LoadingMoreFooter) footer).setState(ListState.STATE_COMPLETE);
        } else {
            footer.setVisibility(View.GONE);
        }
    }

    public void setNoMore(boolean noMore) {
        isLoadingData = false;
        isNoMore = noMore;
        isLoadMoreError = false;
        if (footer instanceof LoadingMoreFooter) {
            ((LoadingMoreFooter) footer).setState(isNoMore ? ListState.STATE_NOMORE : ListState.STATE_COMPLETE);
        } else {
            footer.setVisibility(View.GONE);
        }
    }

    public void loadMoreError() {
        isLoadingData = false;
        isLoadMoreError = true;
        if (footer instanceof LoadingMoreFooter) {
            ((LoadingMoreFooter) footer).setState(ListState.STATE_LOADMORE_ERROR);
        } else {
            footer.setVisibility(View.GONE);
        }
    }

    public void refresh() {
        if (canRefresh && listener != null) {
            refreshHeader.setState(ListState.STATE_REFRESHING);
            listener.onRefresh();
        }
    }

    public void reset() {
        setNoMore(false);
        loadMoreComplete();
        refreshComplete();
    }

    public void refreshComplete() {
        isLoadMoreError = false;
        refreshHeader.refreshComplete();
        setNoMore(false);
    }

    public void setCanRefresh(boolean enable) {
        this.canRefresh = enable;
    }

    public void setCanLoadMore(boolean enable) {
        canLoadMore = enable;
        synchronizeObserverArgs();
        synchronizeArgs();
        if (!enable) {
            if (footer instanceof LoadingMoreFooter) {
                ((LoadingMoreFooter) footer).setState(ListState.STATE_COMPLETE);
            }
        }
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        synchronizeObserverArgs();
        dataObserver.onChanged();
    }

    public View getEmptyView() {
        return emptyView;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        wrapAdapter = new WrapAdapter(adapter,
                canLoadMore,
                headers,
                refreshHeader,
                footer,
                headerTypes);
        if (refreshHeader != null) {
            refreshHeader.updateTime();
        }
        super.setAdapter(wrapAdapter);
        synchronizeObserverArgs();
        adapter.registerAdapterDataObserver(dataObserver);
        dataObserver.onChanged();
    }

    //避免用户自己调用getAdapter() 引起的ClassCastException
    @Override
    public Adapter getAdapter() {
        if (wrapAdapter != null) {
            return wrapAdapter.getOriginalAdapter();
        } else {
            return null;
        }
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (wrapAdapter != null) {
            if (layout instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) layout);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (wrapAdapter.isHeader(position) || wrapAdapter.isFooter(position) || wrapAdapter.isRefreshHeader(position))
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE && listener != null && !isLoadingData && canLoadMore) {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition;
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            } else {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
            if (layoutManager.getChildCount() > 0
                    && lastVisibleItemPosition >= layoutManager.getItemCount() - 1
                    && !isNoMore && !isLoadMoreError && refreshHeader.getState() < ListState.STATE_REFRESHING) {
                loadMore();
            }
        }
    }

    private void loadMore() {
        if (listener != null && canLoadMore) {
            isLoadingData = true;
            isLoadMoreError = false;
            if (footer instanceof LoadingMoreFooter) {
                ((LoadingMoreFooter) footer).setState(ListState.STATE_LOADING);
            } else {
                footer.setVisibility(View.VISIBLE);
            }
            listener.onLoadMore();
        }
    }

    @Override
    public void onRetry() {
        if (!isLoadingData) {
            loadMore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (isOnTop() && canRefresh && appbarState == AppBarStateChangeListener.State.EXPANDED) {
                    refreshHeader.onMove(deltaY / IRecyclerView.DRAG_RATE);
                    if (refreshHeader.getVisibleHeight() > 0 && refreshHeader.getState() < ListState.STATE_REFRESHING) {
                        return false;
                    }
                }
                break;
            default:
                mLastY = -1;
                // reset
                if (isOnTop() && canRefresh && appbarState == AppBarStateChangeListener.State.EXPANDED) {
                    if (refreshHeader.releaseAction()) {
                        if (listener != null) {
                            listener.onRefresh();
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private boolean isOnTop() {
        return refreshHeader.getParent() != null;
    }

    /**
     * 同步引用参数
     */
    private void synchronizeArgs() {
        if (wrapAdapter != null) {
            wrapAdapter.setArgs(canLoadMore,
                    headers,
                    refreshHeader,
                    footer,
                    headerTypes);//synchronize
        }
    }

    private void synchronizeObserverArgs() {
        if (dataObserver != null) {
            dataObserver.setArgs(this,
                    wrapAdapter,
                    emptyView,
                    canLoadMore);//synchronize
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //解决和CollapsingToolbarLayout冲突的问题
        AppBarLayout appBarLayout = null;
        ViewParent p = getParent();
        while (p != null) {
            if (p instanceof CoordinatorLayout) {
                break;
            }
            p = p.getParent();
        }
        if (p instanceof CoordinatorLayout) {
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) p;
            final int childCount = coordinatorLayout.getChildCount();
            for (int i = childCount - 1; i >= 0; i--) {
                final View child = coordinatorLayout.getChildAt(i);
                if (child instanceof AppBarLayout) {
                    appBarLayout = (AppBarLayout) child;
                    break;
                }
            }
            if (appBarLayout != null) {
                appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
                    @Override
                    public void onStateChanged(AppBarLayout appBarLayout, State state) {
                        appbarState = state;
                    }
                });
            }
        }
    }

    public void setLoadingListener(IRecyclerView.LoadingListener listener) {
        this.listener = listener;
    }
}