package com.d.dmusic.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d.dmusic.MainActivity;
import com.d.dmusic.R;
import com.d.dmusic.view.dialog.MenuDialog;

/**
 * TitleLayout
 * Created by D on 2017/5/3.
 */
public class TitleLayout extends LinearLayout implements View.OnClickListener {
    private Context context;
    private int type;
    private ImageView ivBack;
    private ImageView ivMore;
    private MenuDialog menu;

    public TitleLayout(Context context) {
        this(context, null);
    }

    public TitleLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        View root = LayoutInflater.from(context).inflate(R.layout.layout_title, this);
        ivBack = (ImageView) root.findViewById(R.id.iv_title_back);
        ivMore = (ImageView) root.findViewById(R.id.iv_title_more);
        ivBack.setOnClickListener(this);
        ivMore.setOnClickListener(this);
    }

    /**
     * setType
     */
    public void setType(int type) {
        this.type = type;
    }

    public void setVisibility(int resId, int visibility) {
        View v = findViewById(resId);
        if (v != null) {
            v.setVisibility(visibility);
        }
    }

    public void setText(int resId, CharSequence text) {
        View v = findViewById(resId);
        if (v != null && v instanceof TextView) {
            ((TextView) v).setText(text);
        }
    }

    public void setOnClickListener(int resId, final OnClickListener l) {
        View v = findViewById(resId);
        if (v != null) {
            v.setOnClickListener(l);
        }
    }

    public void showMenu() {
        if (menu == null) {
            menu = new MenuDialog(context, type);
        }
        menu.show();
    }

    public void dismissMenu() {
        if (menu != null) {
            menu.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title_back:
                MainActivity.fManger.popBackStack();
                break;
            case R.id.iv_title_more:
                showMenu();
                break;
        }
    }
}
