package com.d.music.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.d.lib.common.component.repeatclick.ClickFast;
import com.d.music.R;
import com.d.music.online.model.SearchHotRespModel;
import com.d.music.play.adapter.FlowTagAdapter;
import com.d.music.view.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * SearchHeaderView
 * Created by D on 2017/5/7.
 */
public class SearchHeaderView extends LinearLayout implements View.OnClickListener {
    private FlowTagAdapter flowTagAdapter;

    private OnHeaderListener listener;

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
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setOrientation(VERTICAL);
        View root = LayoutInflater.from(context).inflate(R.layout.module_play_layout_search, this);
        FlowLayout flFlow = (FlowLayout) root.findViewById(R.id.fl_flow);
        flowTagAdapter = new FlowTagAdapter(context, new ArrayList<SearchHotRespModel.HotsBean>(),
                R.layout.module_play_adapter_search_tag);
        flowTagAdapter.setOnClickListener(new FlowTagAdapter.OnClickListener() {
            @Override
            public void onClick(View v, String tag) {
                if (listener != null) {
                    listener.onClick(v, tag);
                }
            }
        });
        flFlow.setAdapter(flowTagAdapter);
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
        if (ClickFast.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.tv_sweep_history:
                if (listener != null) {
                    listener.onSweepHistory();
                }
                break;
        }
    }

    public void setDatas(List<SearchHotRespModel.HotsBean> datas) {
        flowTagAdapter.setDatas(datas);
        flowTagAdapter.notifyDataSetChanged();
    }

    public interface OnHeaderListener {
        void onClick(View v, String tag);

        void onSweepHistory();
    }

    public void setOnHeaderListener(OnHeaderListener listener) {
        this.listener = listener;
    }
}
