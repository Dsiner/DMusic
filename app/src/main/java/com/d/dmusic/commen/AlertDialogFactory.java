package com.d.dmusic.commen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.d.dmusic.R;

/**
 * AlertDialogFactory
 * Created by D on 2017/4/29.
 */

public class AlertDialogFactory {
    private Context context;

    private AlertDialogFactory(Context context) {
        this.context = context;
    }

    public static AlertDialogFactory createFactory(Context context) {
        return new AlertDialogFactory(context);
    }

    /**
     * 默认LoadingDialog
     */
    public AlertDialog getLoadingDialog() {
        return getLoadingDialog(null);
    }

    /**
     * 默认LoadingDialog
     */
    public AlertDialog getLoadingDialog(String text) {
        final AlertDialog dlg = new AlertDialog.Builder(context/*new ContextThemeWrapper(context, R.style.dialog_style)*/).create();
        if (context instanceof Activity && !((Activity) context).isFinishing()) {
            dlg.show();
        }
        dlg.setContentView(R.layout.dialog_loading);
        TextView tips = (TextView) dlg.findViewById(R.id.tv_tips);
        if (text != null)
            tips.setText(text);
        return dlg;
    }

    public AlertDialog getAlertDialog(String title, String content, String btnOkText, String btnCancelText, View.OnClickListener btnOkListener, View.OnClickListener btnCancelListener) {
        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        if (context instanceof Activity && !((Activity) context).isFinishing()) {
            dlg.show();
        }
        dlg.setContentView(R.layout.dialog);
        if (!TextUtils.isEmpty(title)) {
            TextView tv_title = (TextView) dlg.findViewById(R.id.tv_title);
            tv_title.setText(title);
        }
        if (!TextUtils.isEmpty(content)) {
            TextView tv_content = (TextView) dlg.findViewById(R.id.tv_content);
            tv_content.setText(content);
        }
        if (TextUtils.isEmpty(btnOkText)) {
            btnOkText = "确定";
        }
        if (TextUtils.isEmpty(btnCancelText)) {
            btnCancelText = "取消";
        }
        Button btnOk = (Button) dlg.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) dlg.findViewById(R.id.btn_cancel);
        btnOk.setText(btnOkText);
        btnCancel.setText(btnCancelText);
        btnOk.setOnClickListener(btnOkListener);
        if (btnCancelListener == null) {
            btnCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dlg.dismiss();
                }
            });
        } else {
            btnCancel.setOnClickListener(btnCancelListener);
        }
        return dlg;
    }
}
