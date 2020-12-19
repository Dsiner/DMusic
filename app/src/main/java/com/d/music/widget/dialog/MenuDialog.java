package com.d.music.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.d.lib.common.util.ScreenUtils;
import com.d.music.R;

/**
 * MenuDialog
 * Created by D on 2017/4/29.
 */
public class MenuDialog implements View.OnClickListener {
    private Dialog dialog;
    private LinearLayout rootView;
    private OnMenuListener listener;

    public MenuDialog(Context context, int layoutRes) {
        rootView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.module_common_dialog_more, null);
        rootView.setOnClickListener(this);
        initMenu(context, layoutRes);

        dialog = new Dialog(context, R.style.PopTopInDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        Window dialogWindow = dialog.getWindow();
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.TOP);
            WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            p.width = ScreenUtils.getScreenSize((Activity) context)[0]; //宽度设置
            p.height = ScreenUtils.getScreenSize((Activity) context)[1]; //高度设置
            dialogWindow.setAttributes(p);
        }
        dialog.setContentView(rootView);
    }

    private void initMenu(Context context, int layoutRes) {
        if (layoutRes == -1) {
            return;
        }
        LayoutInflater.from(context).inflate(layoutRes, rootView);
        if (rootView.getChildCount() < 2) {
            return;
        }
        View m = rootView.getChildAt(1);
        if (!(m instanceof ViewGroup)) {
            m.setOnClickListener(this);
            return;
        }
        ViewGroup menu = (ViewGroup) rootView.getChildAt(1);
        int count = menu.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = menu.getChildAt(i);
            v.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null) {
            listener.onClick(v);
        }
    }

    /**
     * 显示dialog
     */
    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
        if (listener != null && rootView != null) {
            listener.onRefresh(rootView);
        }
    }

    /**
     * 隐藏dialog
     */
    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void setOnMenuListener(OnMenuListener listener) {
        this.listener = listener;
    }

    public interface OnMenuListener {
        void onRefresh(View v);

        void onClick(View v);
    }
}
