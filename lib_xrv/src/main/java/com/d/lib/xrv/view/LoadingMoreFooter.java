package com.d.lib.xrv.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d.lib.xrv.R;

public class LoadingMoreFooter extends LinearLayout implements View.OnClickListener {
    private LoadingView ldvLoading;
    private TextView tvLoadMore;
    private OnRetryListener listener;

    public LoadingMoreFooter(Context context) {
        super(context);
        initView(context);
    }

    public LoadingMoreFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View root = inflater.inflate(R.layout.lib_xrv_list_footer_more, this);
        setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ldvLoading = (LoadingView) root.findViewById(R.id.ldv_loading);
        tvLoadMore = (TextView) root.findViewById(R.id.tv_load_more);
        tvLoadMore.setText(getResources().getString(R.string.lib_xrv_list_loading));
    }

    public void setProgressStyle(int style) {
    }

    public void setState(int state) {
        switch (state) {
            case ListState.STATE_LOADING:
                setOnClickListener(null);
                ldvLoading.setVisibility(View.VISIBLE);
                tvLoadMore.setText(getResources().getString(R.string.lib_xrv_list_loading));
                this.setVisibility(View.VISIBLE);
                break;
            case ListState.STATE_COMPLETE:
                setOnClickListener(null);
                ldvLoading.setVisibility(View.GONE);
                tvLoadMore.setText(getResources().getString(R.string.lib_xrv_list_loading_complete));
                this.setVisibility(View.GONE);
                break;
            case ListState.STATE_NOMORE:
                setOnClickListener(null);
                ldvLoading.setVisibility(View.GONE);
                tvLoadMore.setText(getResources().getString(R.string.lib_xrv_list_nomore));
                this.setVisibility(View.VISIBLE);
                break;
            case ListState.STATE_LOADMORE_ERROR:
                setOnClickListener(this);
                ldvLoading.setVisibility(View.GONE);
                tvLoadMore.setText(getResources().getString(R.string.lib_xrv_list_load_more_error));
                this.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onRetry();
        }
    }

    public interface OnRetryListener {
        void onRetry();
    }

    public void setOnRetryListener(OnRetryListener listener) {
        this.listener = listener;
    }
}
