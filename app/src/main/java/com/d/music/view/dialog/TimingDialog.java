package com.d.music.view.dialog;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.d.lib.common.module.repeatclick.ClickUtil;
import com.d.lib.common.utils.Util;
import com.d.lib.common.view.dialog.AbstractDialog;
import com.d.music.App;
import com.d.music.R;
import com.d.music.module.service.MusicService;

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
        return R.layout.dialog_timing;
    }

    @Override
    protected void init(View rootView) {
        etTime = (EditText) rootView.findViewById(R.id.et_time);
        btnOk = (Button) rootView.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) rootView.findViewById(R.id.btn_cancel);
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
            Util.toast(context, "请输入睡眠时间");
            return -1;
        }
        return Integer.valueOf(time);
    }

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick()) {
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
                    App.exit(context);
                } else {
                    MusicService.startService(context.getApplicationContext());
                    MusicService.timing(context.getApplicationContext(), false, 0);
                    MusicService.timing(context.getApplicationContext(), true, minute * 60 * 1000);
                    dismiss();
                    if (listener != null) {
                        listener.onSubmit(minute);
                    }
                }
                break;
        }
    }

    public interface OnTimingListener {
        void onSubmit(long time);

        void onCancel();
    }

    public void setOnTimingListener(OnTimingListener l) {
        this.listener = l;
    }
}
