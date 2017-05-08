package com.d.dmusic.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.d.dmusic.R;

/**
 * AbstractDialog
 * Created by D on 2017/4/29.
 */
public abstract class AbstractDialog {
    protected Context context;
    protected Dialog dialog;
    protected View rootView;

    protected AbstractDialog(Context context) {
        this(context, R.style.dialog_style, false, 0, 0, 0);
    }

    protected AbstractDialog(Context context, int themeResId) {
        this(context, themeResId, false, 0, 0, 0);
    }

    protected AbstractDialog(Context context, int themeResId, boolean isSetWin, int gravity, int width, int heith) {
        this.context = context;
        rootView = LayoutInflater.from(context).inflate(getLayoutRes(), null);
        dialog = new Dialog(context, themeResId);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        if (isSetWin) {
            Window dialogWindow = dialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setGravity(gravity);
                WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
                p.width = width; //宽度设置
                p.height = heith; //高度设置
                dialogWindow.setAttributes(p);
            }
        }
        dialog.setContentView(rootView);
        init(rootView);
    }

    /**
     * 显示dialog
     */
    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
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

    protected abstract int getLayoutRes();

    protected abstract void init(View rootView);
}
