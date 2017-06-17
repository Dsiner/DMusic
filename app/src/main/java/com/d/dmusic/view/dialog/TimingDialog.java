package com.d.dmusic.view.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.d.dmusic.R;
import com.d.dmusic.application.SysApplication;
import com.d.dmusic.commen.Preferences;
import com.d.dmusic.module.events.SleepFinishEvent;
import com.d.dmusic.module.repeatclick.ClickUtil;
import com.d.dmusic.module.service.MusicService;
import com.d.dmusic.utils.Util;

import org.greenrobot.eventbus.EventBus;

/**
 * TimingDialog
 * Created by D on 2017/4/29.
 */
public class TimingDialog extends AbstractDialog implements View.OnClickListener, TextWatcher {
    private EditText etTime;
    private Button btnCancel;
    private Button btnOk;

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
        btnCancel = (Button) rootView.findViewById(R.id.btn_cancel);
        btnOk.setClickable(false);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        etTime.addTextChangedListener(this);
    }

    private int verify(String time) {
        if (TextUtils.isEmpty(time)) {
            Util.toast(context, "请输入睡眠时间");
            return -1;
        }
        int t = Integer.valueOf(time);
        if (t <= 0) {
            SysApplication.exit(context);
        }
        return t;
    }

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_ok:
                int minute = verify(etTime.getText().toString().trim());
                if (minute > 0) {
                    MusicService.startService(context.getApplicationContext());
                    MusicService.timing(context.getApplicationContext(), false, 0);
                    MusicService.timing(context.getApplicationContext(), true, minute * 60 * 1000);
                    Preferences.getInstance(context.getApplicationContext()).putSleepType(6);
                    dismiss();
                    EventBus.getDefault().post(new SleepFinishEvent());
                }
                break;
        }
    }

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
            btnOk.setAlpha(1);
            if (length > 6) {
                etTime.setText("666666");
            }
        } else {
            btnOk.setClickable(false);
            btnOk.setAlpha(0.5f);
        }
    }
}
