package com.d.dmusic.view.dialog;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.d.dmusic.R;
import com.d.dmusic.module.events.RefreshEvent;
import com.d.dmusic.module.greendao.db.MusicDB;
import com.d.dmusic.module.greendao.music.CustomList;
import com.d.dmusic.module.greendao.util.MusicDBUtil;
import com.d.dmusic.module.repeatclick.ClickUtil;
import com.d.dmusic.utils.Util;
import com.d.dmusic.utils.log.ULog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * NewListDialog
 * Created by D on 2017/4/29.
 */
public class NewListDialog extends AbstractDialog implements View.OnClickListener, TextWatcher {
    private EditText etName;
    private TextView tvCancel;
    private TextView tvOk;

    public NewListDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.dialog_new_list;
    }

    @Override
    protected void init(View rootView) {
        etName = (EditText) rootView.findViewById(R.id.et_name);
        tvOk = (TextView) rootView.findViewById(R.id.tv_ok);
        tvCancel = (TextView) rootView.findViewById(R.id.tv_cancel);
        tvOk.setClickable(false);
        tvOk.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        etName.addTextChangedListener(this);
    }

    /**
     * 插入新的歌曲列表--返回：是否成功
     */
    private boolean insertNewList(String name) {
        if (TextUtils.isEmpty(name)) {
            Util.toast(context.getApplicationContext(), "请输入列表名！");
            return false;
        }
        CustomList exist = MusicDBUtil.getInstance(context).queryCustomListByName(name);
        if (exist != null) {
            Util.toast(context.getApplicationContext(), "该列表已存在！");
            return false;
        }

        List<CustomList> list = MusicDBUtil.getInstance(context).queryAllCustomListByPointerAsc();
        int count = list != null ? list.size() : 0;
        if (count < 20) {
            final int seq = MusicDBUtil.getInstance(context).queryCustomListMaxSeq();
            final int pointer = list != null ? getPointer(list) : MusicDB.CUSTOM_MUSIC_INDEX;
            CustomList customList = new CustomList();
            customList.setListName(name);
            customList.setSongCount((long) 0);
            customList.setSeq(seq + 1);
            customList.setPointer(pointer);
            MusicDBUtil.getInstance(context).insertOrReplaceCustomList(customList);
        } else {
            Util.toast(context.getApplicationContext(), "列表已满！");
            return false;
        }
        return true;
    }

    /**
     * 插入新的歌曲列表--查找未用Table
     */
    private int getPointer(List<CustomList> list) {
        int a = MusicDB.CUSTOM_MUSIC_INDEX;
        for (CustomList custom : list) {
            if (custom.getPointer() != a) {
                return a;
            }
            a++;
        }
        return a;
    }

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_ok:
                if (insertNewList(etName.getText().toString())) {
                    EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_INVALID, RefreshEvent.SYNC_CUSTOM_LIST));
                    dismiss();
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
        ULog.v("et:" + s.toString());
        if (s.toString().length() > 0 && s.toString().length() < 40) {
            tvOk.setClickable(true);
            tvOk.setTextColor(Color.parseColor("#FFA523"));
        } else {
            tvOk.setClickable(false);
            tvOk.setTextColor(Color.parseColor("#77FFA523"));
        }
    }
}
