package com.d.dmusic.mvp.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d.commen.base.BaseActivity;
import com.d.commen.mvp.MvpBasePresenter;
import com.d.commen.mvp.MvpView;
import com.d.dmusic.R;
import com.d.dmusic.commen.AlertDialogFactory;
import com.d.dmusic.module.global.MusciCst;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.module.itemtouchhelper.OnStartDragListener;
import com.d.dmusic.module.itemtouchhelper.SimpleItemTouchHelperCallback;
import com.d.dmusic.mvp.adapter.HandlerAdapter;
import com.d.dmusic.utils.StatusBarCompat;
import com.d.dmusic.view.dialog.AddToListDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 音乐列表管理、排序
 * Created by D on 2017/4/28.
 */
public class ListHandleActivity extends BaseActivity<MvpBasePresenter> implements MvpView, OnStartDragListener {
    @Bind(R.id.iv_title_back)
    ImageView ivBack;
    @Bind(R.id.tv_title_title)
    TextView tvTitle;
    @Bind(R.id.tv_title_select_all)
    TextView ivSelectAll;
    @Bind(R.id.llyt_add_to_list)
    LinearLayout llytAddToList;
    @Bind(R.id.llyt_delete)
    LinearLayout llytDelete;
    @Bind(R.id.llyt_submit)
    LinearLayout llytSubmit;
    @Bind(R.id.rv_list)
    RecyclerView rvList;

    private int type;
    private List<MusicModel> models;
    private HandlerAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ItemTouchHelper itemTouchHelper;
    private WeakHandler handler = new WeakHandler(this);
    private AlertDialog dialog;

    @OnClick({R.id.iv_title_back, R.id.tv_title_select_all, R.id.llyt_add_to_list,
            R.id.llyt_delete, R.id.llyt_submit})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.tv_title_select_all:
                if ((boolean) ivSelectAll.getTag()) {
                    for (MusicModel model : models) {
                        model.isChecked = !model.isChecked;
                        adapter.notifyDataSetChanged();
                    }
                    ivSelectAll.setTag(false);
                    ivSelectAll.setText("全选");
                } else {
                    for (MusicModel musicModel : models) {
                        musicModel.isChecked = true;
                        adapter.notifyDataSetChanged();
                    }
                    ivSelectAll.setTag(true);
                    ivSelectAll.setText("反选");
                }
                break;
            case R.id.llyt_add_to_list:
                List<MusicModel> mlm = new ArrayList<MusicModel>();
                for (MusicModel musicModel : models) {
                    if (musicModel.isChecked) {
                        mlm.add(musicModel);
                    }
                }
                new AddToListDialog(this, mlm, type).show();
                break;
            case R.id.llyt_delete:
                dialog = AlertDialogFactory.createFactory(this).getLoadingDialog();
                for (int i = models.size() - 1; i >= 0; i--) {
                    if (models.get(i).isChecked) {
                        models.remove(i);
                    }
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
                break;
            case R.id.llyt_submit:
                dialog = AlertDialogFactory.createFactory(this).getLoadingDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ContentValues valuesCustom = new ContentValues();
                        valuesCustom.put("sortby", 3);
//                        DataSupport.update(CustomList.class, valuesCustom, id);// 更新当前歌曲列表排序方式
//                        CustomMusicFragment.sortBy = 3;// 以自定义方式排序
                        handler.sendEmptyMessage(1);
                    }
                }).start();
                break;
        }
    }

    static class WeakHandler extends Handler {
        WeakReference<ListHandleActivity> weakReference;

        public WeakHandler(ListHandleActivity activity) {
            weakReference = new WeakReference<ListHandleActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ListHandleActivity theActivity = weakReference.get();
            if (theActivity != null && !theActivity.isFinishing()) {
                switch (msg.what) {
                    case 1:
                        theActivity.dialog.dismiss();
                        theActivity.finish();
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_list_handle;
    }

    @Override
    public MvpBasePresenter getPresenter() {
        return new MvpBasePresenter(getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(ListHandleActivity.this, 0xffFD8D22);//沉浸式状态栏
        Intent intent = getIntent();
        if (intent != null) {
            type = intent.getIntExtra("type", 0);
        }
    }

    @Override
    protected void init() {
        ivSelectAll.setTag(false);

        /*********************** split line ****************************/
        models = MusciCst.models;
        adapter = new HandlerAdapter(this, models, R.layout.adapter_handler, this);
        layoutManager = new LinearLayoutManager(this);//创建线性布局管理器（默认是垂直方向）
        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(layoutManager);//为RecyclerView指定布局管理对象
        rvList.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rvList);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    @Override
    protected void onDestroy() {
        MusciCst.models.clear();
        super.onDestroy();
    }
}