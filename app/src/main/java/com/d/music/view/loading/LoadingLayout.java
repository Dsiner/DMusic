package com.d.music.view.loading;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.d.music.R;

/**
 * Created by D on 2017/5/2.
 */

public class LoadingLayout extends LinearLayout {

    protected LoadingView ldvLoading;

    public LoadingLayout(Context context) {
        this(context, null);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected void init(Context context) {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        View root = LayoutInflater.from(context).inflate(R.layout.layout_loading, this);
        ldvLoading = (LoadingView) root.findViewById(R.id.ldv_loading);
    }

    @Override
    public void setVisibility(int visibility) {
        switch (visibility) {
            case VISIBLE:
                ldvLoading.restart();
                break;
            case GONE:
                ldvLoading.stop();
                break;
            case INVISIBLE:
                ldvLoading.stop();
                break;
        }
        super.setVisibility(visibility);
    }
}