package com.d.music.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.d.commen.module.repeatclick.ClickUtil;
import com.d.music.R;
import com.d.music.view.dialog.MenuDialog;

/**
 * TitleLayout
 * Created by D on 2018/1/29.
 */
public class TitleLayout extends com.d.commen.view.TitleLayout implements View.OnClickListener {
    private MenuDialog menu;
    private MenuDialog.OnMenuListener onMenuListener;

    public TitleLayout(Context context) {
        super(context);
    }

    public TitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TitleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        ImageView ivRight = (ImageView) rootView.findViewById(com.d.commen.R.id.iv_title_right);
        ivRight.setOnClickListener(this);
    }

    public void showMenu() {
        if (menu == null) {
            menu = new MenuDialog(context, menuRes);
            menu.setOnMenuListener(onMenuListener);
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
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_title_right:
                showMenu();
                break;
        }
    }

    public void setOnMenuListener(MenuDialog.OnMenuListener listener) {
        onMenuListener = listener;
        if (menu != null) {
            menu.setOnMenuListener(onMenuListener);
        }
    }
}
