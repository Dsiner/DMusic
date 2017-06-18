package com.d.music.view.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.d.music.R;
import com.d.music.module.events.RefreshEvent;
import com.d.music.module.greendao.db.MusicDB;
import com.d.music.module.greendao.music.CustomList;
import com.d.music.module.greendao.util.MusicDBUtil;
import com.d.music.module.repeatclick.ClickUtil;
import com.d.music.utils.Util;
import com.d.music.utils.log.ULog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * NewListDialog
 * Created by D on 2017/4/29.
 */
public class NewListDialog extends AbstractDialog implements View.OnClickListener, TextWatcher {
    private EditText etName;
    private Button btnCancel;
    private Button btnOk;

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
        btnOk = (Button) rootView.findViewById(R.id.btn_ok);
        btnCancel = (Button) rootView.findViewById(R.id.btn_cancel);
        btnOk.setClickable(false);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        etName.addTextChangedListener(this);
    }

    /**
     * 插入新的歌曲列表--返回：是否成功
     */
    private boolean insertNewList(String name) {
        if (TextUtils.isEmpty(name)) {
            Util.toast(context.getApplicationContext(), "请输入歌单名！");
            return false;
        }
        name = name.trim();
        CustomList exist = MusicDBUtil.getInstance(context).queryCustomListByName(name);
        if (exist != null) {
            Util.toast(context.getApplicationContext(), "该歌单已存在！");
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
            customList.setSortBy(MusicDB.ORDER_TYPE_TIME);
            MusicDBUtil.getInstance(context).insertOrReplaceCustomList(customList);
        } else {
            Util.toast(context.getApplicationContext(), "歌单已满！");
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
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_ok:
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
        int length = s.toString().trim().length();
        if (length > 0 && length < 40) {
            btnOk.setClickable(true);
            btnOk.setAlpha(1);
        } else {
            btnOk.setClickable(false);
            btnOk.setAlpha(0.5f);
        }
    }
}
