package com.d.lib.common.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.d.lib.common.R;
import com.d.lib.common.util.ViewHelper;

/**
 * AlertSubDialog
 * Created by D on 2018/6/15.
 */
public class AlertSubDialog extends AbstractDialog {
    private TextView tv_title, tv_content, tv_sub_tips;
    private CheckBox cb_toggle;
    private String mTitle;
    private String mContent;
    private String mSubTips;
    private boolean mIsChecked;
    private OnCheckListener mListener;

    public AlertSubDialog(Context context, String title, String content, String subTips, boolean isChecked) {
        super(context, R.style.lib_pub_dialog_style, true, Gravity.CENTER,
                (int) context.getResources().getDimension(R.dimen.lib_pub_dimen_dialog_width),
                WindowManager.LayoutParams.WRAP_CONTENT);
        this.mTitle = title;
        this.mContent = content;
        this.mSubTips = subTips;
        this.mIsChecked = isChecked;
        bindView(mRootView);
        init();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.lib_pub_dialog_check;
    }

    @Override
    protected boolean isInitEnabled() {
        return false;
    }

    @Override
    protected void bindView(View rootView) {
        tv_title = ViewHelper.findViewById(rootView, R.id.tv_title);
        tv_content = ViewHelper.findViewById(rootView, R.id.tv_content);
        tv_sub_tips = ViewHelper.findViewById(rootView, R.id.tv_sub_tips);
        cb_toggle = ViewHelper.findViewById(rootView, R.id.cb_toggle);

        ViewHelper.setOnClickListener(mRootView, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int resId = v.getId();
                if (R.id.flyt_toggle == resId) {
                    mIsChecked = !cb_toggle.isChecked();
                    cb_toggle.setChecked(mIsChecked);
                } else if (R.id.btn_ok == resId) {
                    dismiss();
                    if (mListener != null) {
                        mListener.onSubmit(AlertSubDialog.this, mIsChecked);
                    }
                } else if (R.id.btn_cancel == resId) {
                    dismiss();
                    if (mListener != null) {
                        mListener.onCancel(AlertSubDialog.this);
                    }
                }
            }
        }, R.id.flyt_toggle, R.id.btn_ok, R.id.btn_cancel);
    }

    @Override
    protected void init() {
        if (!TextUtils.isEmpty(mTitle)) {
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(mTitle);
        } else {
            tv_title.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mContent)) {
            tv_content.setVisibility(View.VISIBLE);
            tv_content.setText(mContent);
        } else {
            tv_content.setVisibility(View.GONE);
        }
        tv_sub_tips.setText(!TextUtils.isEmpty(mSubTips) ? mSubTips : "");
        cb_toggle.setChecked(mIsChecked);
    }

    public interface OnCheckListener {

        /**
         * Click ok
         *
         * @param isChecked isChecked
         */
        void onSubmit(Dialog dlg, boolean isChecked);

        /**
         * Click cancel
         */
        void onCancel(Dialog dlg);
    }

    public void setOnCheckListener(OnCheckListener listener) {
        this.mListener = listener;
    }
}
