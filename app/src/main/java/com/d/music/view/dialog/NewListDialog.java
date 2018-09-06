package com.d.music.view.dialog;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.d.lib.common.component.repeatclick.ClickFast;
import com.d.lib.common.utils.Util;
import com.d.lib.common.utils.log.ULog;
import com.d.lib.common.view.dialog.AbstractDialog;
import com.d.music.R;
import com.d.music.module.events.RefreshEvent;
import com.d.music.module.greendao.bean.CustomListModel;
import com.d.music.module.greendao.db.AppDB;
import com.d.music.module.greendao.util.AppDBUtil;

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
        return R.layout.module_local_dialog_new_list;
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
        CustomListModel exist = AppDBUtil.getIns(context).optCustomList().queryByName(name);
        if (exist != null) {
            Util.toast(context.getApplicationContext(), "该歌单已存在！");
            return false;
        }

        List<CustomListModel> list = AppDBUtil.getIns(context).optCustomList().queryAllByPointerAsc();
        int count = list != null ? list.size() : 0;
        if (count < 20) {
            final int seq = AppDBUtil.getIns(context).optCustomList().queryMaxSeq();
            final int pointer = list != null ? getPointer(list) : AppDB.CUSTOM_MUSIC_INDEX;
            CustomListModel customListModel = new CustomListModel();
            customListModel.setName(name);
            customListModel.setCount((long) 0);
            customListModel.setSeq(seq + 1);
            customListModel.setPointer(pointer);
            customListModel.setSortType(AppDB.ORDER_TYPE_TIME);
            AppDBUtil.getIns(context).optCustomList().insertOrReplace(customListModel);
        } else {
            Util.toast(context.getApplicationContext(), "歌单已满！");
            return false;
        }
        return true;
    }

    /**
     * 插入新的歌曲列表--查找未用Table
     */
    private int getPointer(List<CustomListModel> list) {
        int a = AppDB.CUSTOM_MUSIC_INDEX;
        for (CustomListModel custom : list) {
            if (custom.getPointer() != a) {
                return a;
            }
            a++;
        }
        return a;
    }

    @Override
    public void onClick(View v) {
        if (ClickFast.isFastDoubleClick()) {
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
        ULog.d("et:" + s.toString());
        int length = s.toString().trim().length();
        if (length > 0 && length < 40) {
            btnOk.setClickable(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                btnOk.setAlpha(1);
            }
        } else {
            btnOk.setClickable(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                btnOk.setAlpha(0.5f);
            }
        }
    }
}
