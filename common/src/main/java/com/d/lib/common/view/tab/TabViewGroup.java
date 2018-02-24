package com.d.lib.common.view.tab;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.d.lib.common.R;

import cn.feng.skin.manager.loader.SkinManager;

/**
 * TabViewGroup
 * Created by D on 2017/8/25.
 */
public class TabViewGroup extends RelativeLayout implements TabView {
    private Context context;
    private TextView tvTitle, tvNumber;
    private int color, colorCur;
    private boolean focus;

    public TabViewGroup(Context context) {
        this(context, null);
    }

    public TabViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        this.color = ContextCompat.getColor(context, R.color.lib_pub_color_gray);
        this.colorCur = SkinManager.getInstance().getColor(R.color.lib_pub_color_main);
        View root = LayoutInflater.from(context).inflate(R.layout.lib_pub_view_tab, this);
        tvTitle = (TextView) root.findViewById(R.id.tv_title);
        tvNumber = (TextView) root.findViewById(R.id.tv_number);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (hasWindowFocus) {
            colorCur = SkinManager.getInstance().getColor(R.color.lib_pub_color_main);
        }
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    public void setText(String text) {
        tvTitle.setText(text);
    }

    @Override
    public void setPadding(int padding) {
        setPadding(padding, 0, padding, 0);
    }

    @Override
    public void setNumber(String text, int visibility) {
        tvNumber.setText(text);
        tvNumber.setVisibility(visibility);
    }

    @Override
    public void notifyData(boolean focus) {
        this.focus = focus;
        if (tvTitle != null) {
            tvTitle.setTextColor(focus ? colorCur : color);
        }
    }

    @Override
    public void onScroll(float factor) {

    }
}
