package com.d.dmusic.view.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.d.dmusic.R;
import com.d.dmusic.module.global.Cst;
import com.d.dmusic.module.greendao.music.CustomList;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.mvp.adapter.AddToListAdapter;
import com.d.xrv.LRecyclerView;

import java.util.List;

/**
 * Created by D on 2017/4/29.
 */
public class AddToListDialog extends AbstractDialog implements View.OnClickListener {
    private int curIndex;// 列表标识
    private AddToListAdapter adapter;// listview适配器
    private LRecyclerView lrvList;
    private ImageView ivQuit;
    private TextView tvOK;// 确定按钮
    private List<MusicModel> models;// 待插入歌曲队列
    private List<CustomList> cusList;// 除当前列表外的自定义列表队列

    public AddToListDialog(Context context, List<MusicModel> models, int index) {
        super(context, R.style.PopButtomInDialog, true, Gravity.BOTTOM, Cst.SCREEN_WIDTH, (int) (Cst.SCREEN_HEIGHT * 0.382));
        this.curIndex = index;
        this.models = models;
//        cusList = DataSupport.select("listName", "pointerOfDBTable")
//                .where("pointerOfDBTable!=?", String.valueOf(curIndex)).order("seq asc")
//                .find(CustomList.class);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.dialog_add_to_list;
    }

    @Override
    protected void init(View rootView) {
        lrvList = (LRecyclerView) rootView.findViewById(R.id.lrv_list);
        ivQuit = (ImageView) rootView.findViewById(R.id.iv_quit);
        tvOK = (TextView) rootView.findViewById(R.id.tv_ok);

        adapter = new AddToListAdapter(context, cusList, R.layout.adapter_add_to_list);
        lrvList.setAdapter(adapter);
        tvOK.setOnClickListener(this);
        ivQuit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_quit:
                dismiss();
                break;
            case R.id.tv_ok:
//                int position = adapter.getLastSelectedPostion();
//                if (position != -1) {
//                    int index = cusList.get(position).getPointerOfDBTable();// 拿到待插入歌曲列表的表指针
//                    // 插入列表
//                    if (DataBaseUtils.insertCustomMusic(context, models, index)) {
//                        dismiss();
//                    } else {
//                        Util.toast(context.getApplicationContext(), "歌曲已存在！");
//                    }
//                } else {
//                    Util.toast(context.getApplicationContext(), "未选则任何列表！");
//                }
                break;
        }
    }
}
