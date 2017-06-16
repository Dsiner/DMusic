package com.d.dmusic.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.d.dmusic.R;

/**
 * TitleLayout
 * Created by D on 2017/5/3.
 */
public class RowLayout extends RelativeLayout {
    private final String content;
    private int visibilityToggle;
    private int visibilityGoto;
    private TextView tvContent;
    private ToggleButton tbToggle;
    private OnToggleListener listener;

    public RowLayout(Context context) {
        this(context, null);
    }

    public RowLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RowLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RowLayout);
        content = typedArray.getString(R.styleable.RowLayout_rl_text);
        visibilityToggle = typedArray.getInteger(R.styleable.RowLayout_rl_toggleVisibility, 0);
        visibilityGoto = typedArray.getInteger(R.styleable.RowLayout_rl_gotoVisibility, 0);
        typedArray.recycle();
        init(context);
    }

    private void init(Context context) {
        final View root = LayoutInflater.from(context).inflate(R.layout.layout_row, this);
        tvContent = (TextView) root.findViewById(R.id.tv_content);
        tbToggle = (ToggleButton) root.findViewById(R.id.tb_toggle);
        ImageView ivGoto = (ImageView) root.findViewById(R.id.iv_goto);
        ivGoto.setVisibility(visibilityGoto);
        tvContent.setText(content);
        tbToggle.setVisibility(visibilityToggle);
        tbToggle.setOnToggleListener(new ToggleButton.OnToggleListener() {
            @Override
            public void onToggle(boolean isOpen) {
                if (listener != null) {
                    listener.onToggle(RowLayout.this, isOpen);
                }
            }
        });
    }

    /**
     * 设置文本内容
     */
    public void setText(CharSequence text) {
        tvContent.setText(text);
    }

    /**
     * toggle按钮设置开闭
     */
    public void setOpen(boolean open) {
        tbToggle.setOpen(open);
    }

    /**
     * toggle按钮开闭状态
     */
    public boolean isOpen() {
        return tbToggle.isOpen();
    }

    public interface OnToggleListener {
        void onToggle(View v, boolean isOpen);
    }

    public void setOnToggleListener(OnToggleListener listener) {
        this.listener = listener;
    }
}
