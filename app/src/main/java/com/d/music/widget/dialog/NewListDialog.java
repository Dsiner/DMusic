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
import com.d.lib.common.util.log.ULog;
import com.d.lib.common.widget.dialog.AbstractDialog;
import com.d.music.R;
import com.d.music.data.database.greendao.DBManager;
import com.d.music.data.database.greendao.bean.CustomListModel;
import com.d.music.data.database.greendao.db.AppDatabase;
import com.d.music.event.eventbus.RefreshEvent;

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

    /**
     * 插入新的歌曲列表
     *
     * @return 是否成功
     */
    public static boolean insertNewList(Context context, String name, boolean isTip) {
        if (TextUtils.isEmpty(name)) {
            if (isTip) {
                ToastUtils.toast(context.getApplicationContext(), "请输入歌单名！");
            }
            return false;
        }
        name = name.trim();
        CustomListModel exist = DBManager.getInstance(context).optCustomList().queryByName(name);
        if (exist != null) {
            if (isTip) {
                ToastUtils.toast(context.getApplicationContext(), "该歌单已存在！");
            }
            return false;
        }

        List<CustomListModel> list = DBManager.getInstance(context).optCustomList().queryAllByPointerAsc();
        int count = list != null ? list.size() : 0;
        if (count < 20) {
            final int seq = DBManager.getInstance(context).optCustomList().queryMaxSeq();
            final int pointer = list != null ? getPointer(list) : AppDatabase.CUSTOM_MUSIC_INDEX;
            CustomListModel customListModel = new CustomListModel();
            customListModel.setName(name);
            customListModel.setCount((long) 0);
            customListModel.setSeq(seq + 1);
            customListModel.setPointer(pointer);
            customListModel.setSortType(AppDatabase.ORDER_TYPE_TIME);
            DBManager.getInstance(context).optCustomList().insertOrReplace(customListModel);
        } else {
            if (isTip) {
                ToastUtils.toast(context.getApplicationContext(), "歌单已满！");
            }
            return false;
        }
        return true;
    }

    /**
     * 插入新的歌曲列表 - 查找未用Table
     */
    private static int getPointer(List<CustomListModel> list) {
        int a = AppDatabase.CUSTOM_MUSIC_INDEX;
        for (CustomListModel custom : list) {
            if (custom.getPointer() != a) {
                return a;
            }
            a++;
        }
        return a;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.module_local_dialog_new_list;
    }

    @Override
    protected void init() {
        etName = (EditText) mRootView.findViewById(R.id.et_name);
        btnOk = (Button) mRootView.findViewById(R.id.btn_ok);
        btnCancel = (Button) mRootView.findViewById(R.id.btn_cancel);
        btnOk.setClickable(false);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        etName.addTextChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        if (QuickClick.isQuickClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_ok:
                if (insertNewList(mContext, etName.getText().toString(), true)) {
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
