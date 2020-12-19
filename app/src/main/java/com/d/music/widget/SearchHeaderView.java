package com.d.music.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.d.lib.common.component.quickclick.QuickClick;
import com.d.music.R;
import com.d.music.online.model.SearchHotRespModel;
import com.d.music.play.adapter.FlowTagAdapter;
import com.d.music.widget.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * SearchHeaderView
 * Created by D on 2017/5/7.
 */
public class SearchHeaderView extends LinearLayout implements View.OnClickListener {
    private FlowTagAdapter mFlowTagAdapter;

    private OnHeaderListener mOnHeaderListener;

    public SearchHeaderView(Context context) {
        super(context);
        init(context);
    }

    public SearchHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SearchHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(VERTICAL);
        View root = LayoutInflater.from(context).inflate(R.layout.module_play_layout_search, this);
        FlowLayout flFlow = (FlowLayout) root.findViewById(R.id.fl_flow);
        mFlowTagAdapter = new FlowTagAdapter(context, new ArrayList<SearchHotRespModel.HotsBean>(),
                R.layout.module_play_adapter_search_tag);
        mFlowTagAdapter.setOnClickListener(new FlowTagAdapter.OnClickListener() {
            @Override
            public void onClick(View v, String tag) {
                if (mOnHeaderListener != null) {
                    mOnHeaderListener.onClick(v, tag);
                }
            }
        });
        flFlow.setAdapter(mFlowTagAdapter);
        root.findViewById(R.id.tv_sweep_history).setOnClickListener(this);
    }

    public void setVisibility(int resId, int visibility) {
        View v = findViewById(resId);
        if (v != null) {
            v.setVisibility(visibility);
        }
    }

    @Override
    public void onClick(View v) {
        if (QuickClick.isQuickClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.tv_sweep_history:
                if (mOnHeaderListener != null) {
                    mOnHeaderListener.onSweepHistory();
                }
                break;
        }
    }

    public void setDatas(List<SearchHotRespModel.HotsBean> datas) {
        mFlowTagAdapter.setDatas(datas);
        mFlowTagAdapter.notifyDataSetChanged();
    }

    public void setOnHeaderListener(OnHeaderListener listener) {
        this.mOnHeaderListener = listener;
    }

    public interface OnHeaderListener {
        void onClick(View v, String tag);

        void onSweepHistory();
    }
}
