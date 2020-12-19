package com.d.lib.common.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.d.lib.common.R;
import com.d.lib.common.util.ViewHelper;
import com.d.lib.common.widget.ClearEditText;

/**
 * EditDialog
 * Created by D on 2018/6/15.
 */
public class EditDialog extends AbstractDialog {
    private TextView tv_title;
    private ClearEditText cet_edit;
    private String mTitle;
    private String mContent;
    private OnEditListener mListener;

    public EditDialog(Context context, String title, String content) {
        super(context, R.style.lib_pub_dialog_style, false, 0, 0, 0);
        this.mTitle = title;
        this.mContent = content;
        bindView(mRootView);
        init();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.lib_pub_dialog_edit;
    }

    @Override
    protected boolean isInitEnabled() {
        return false;
    }

    @Override
    protected void bindView(View rootView) {
        tv_title = ViewHelper.findViewById(rootView, R.id.tv_title);
        cet_edit = ViewHelper.findViewById(rootView, R.id.cet_edit);
        ViewHelper.setOnClickListener(rootView, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int resId = v.getId();
                if (R.id.btn_ok == resId) {
                    dismiss();
                    if (mListener != null) {
                        mListener.onSubmit(EditDialog.this, cet_edit.getText().toString());
                    }
                } else if (R.id.btn_cancel == resId) {
                    dismiss();
                    if (mListener != null) {
                        mListener.onCancel(EditDialog.this);
                    }
                }
            }
        }, R.id.btn_ok, R.id.btn_cancel);
    }

    @Override
    protected void init() {
        cet_edit.setText(!TextUtils.isEmpty(mContent) ? mContent : "");
        if (!TextUtils.isEmpty(mTitle)) {
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(mTitle);
        } else {
            tv_title.setVisibility(View.GONE);
        }
    }

    public interface OnEditListener {

        /**
         * Click ok
         *
         * @param content content
         */
        void onSubmit(Dialog dlg, String content);

        /**
         * Click cancel
         */
        void onCancel(Dialog dlg);
    }

    public void setOnEditListener(OnEditListener listener) {
        this.mListener = listener;
    }
}
