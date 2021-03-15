package com.d.music.widget.dialog;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.d.lib.common.component.quickclick.QuickClick;
import com.d.lib.common.util.ToastUtils;
import com.d.lib.common.widget.dialog.AbstractDialog;
import com.d.music.App;
import com.d.music.R;
import com.d.music.component.service.MusicService;

/**
 * TimingDialog
 * Created by D on 2017/4/29.
 */
public class TimingDialog extends AbstractDialog implements View.OnClickListener {
    private EditText etTime;
    private Button btnOk;
    private OnTimingListener listener;

    public TimingDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.module_setting_dialog_timing;
    }

    @Override
    protected void init() {
        etTime = (EditText) mRootView.findViewById(R.id.et_time);
        btnOk = (Button) mRootView.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) mRootView.findViewById(R.id.btn_cancel);
        btnOk.setClickable(false);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        etTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.toString().trim().length();
                if (length > 0) {
                    btnOk.setClickable(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        btnOk.setAlpha(1);
                    }
                    if (length > 6) {
                        etTime.setText("666666");
                    }
                } else {
                    btnOk.setClickable(false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        btnOk.setAlpha(0.5f);
                    }
                }
            }
        });
    }

    private int verify(String time) {
        if (TextUtils.isEmpty(time)) {
            ToastUtils.toast(mContext, "请输入睡眠时间");
            return -1;
        }
        return Integer.valueOf(time);
    }

    @Override
    public void onClick(View v) {
        if (QuickClick.isQuickClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_cancel:
                dismiss();
                if (listener != null) {
                    listener.onCancel();
                }
                break;
            case R.id.btn_ok:
                long minute = verify(etTime.getText().toString().trim());
                if (minute <= 0) {
                    App.Companion.exit();
                } else {
                    MusicService.startService(mContext.getApplicationContext());
                    MusicService.timing(mContext.getApplicationContext(), false, 0);
                    MusicService.timing(mContext.getApplicationContext(), true, minute * 60 * 1000);
                    dismiss();
                    if (listener != null) {
                        listener.onSubmit(minute);
                    }
                }
                break;
        }
    }

    public void setOnTimingListener(OnTimingListener l) {
        this.listener = l;
    }

    public interface OnTimingListener {
        void onSubmit(long time);

        void onCancel();
    }
}
