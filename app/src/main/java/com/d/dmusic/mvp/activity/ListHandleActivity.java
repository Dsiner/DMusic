package com.d.dmusic.mvp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.d.dmusic.module.events.MusicModelEvent;
import com.d.dmusic.module.global.MusciCst;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.module.itemtouchhelper.OnStartDragListener;
import com.d.dmusic.module.itemtouchhelper.SimpleItemTouchHelperCallback;
import com.d.dmusic.mvp.adapter.HandlerAdapter;
import com.d.dmusic.utils.StatusBarCompat;
import com.d.dmusic.view.popup.AddToListPopup;

import org.greenrobot.eventbus.EventBus;

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
    @Bind(R.id.llyt_revoke)
    LinearLayout llytRevoke;
    @Bind(R.id.rv_list)
    RecyclerView rvList;

    private int type;
    private List<MusicModel> models;
    private HandlerAdapter adapter;
    private ItemTouchHelper itemTouchHelper;
    private AlertDialog dialog;

    @OnClick({R.id.iv_title_back, R.id.tv_title_select_all, R.id.llyt_add_to_list,
            R.id.llyt_delete, R.id.llyt_revoke})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.tv_title_select_all:
                final boolean isAll = !((boolean) ivSelectAll.getTag());
                ivSelectAll.setTag(isAll);
                for (MusicModel model : models) {
                    model.isSortChecked = isAll || !model.isSortChecked;
                }
                ivSelectAll.setText(isAll ? "反选" : "全选");
                adapter.notifyDataSetChanged();
                break;
            case R.id.llyt_add_to_list:
                List<MusicModel> list = new ArrayList<MusicModel>();
                for (MusicModel musicModel : models) {
                    if (musicModel.isSortChecked) {
                        list.add(musicModel);
                    }
                }
                new AddToListPopup(this, list, type).show();
                break;
            case R.id.llyt_delete:
                dialog = AlertDialogFactory.createFactory(this).getLoadingDialog();
                for (int i = models.size() - 1; i >= 0; i--) {
                    if (models.get(i).isSortChecked) {
                        models.remove(i);
                    }
                }
                dialog.dismiss();
                adapter.notifyDataSetChanged();
                break;
            case R.id.llyt_revoke:
                dialog = AlertDialogFactory.createFactory(this).getLoadingDialog();
                models.clear();
                models.addAll(MusciCst.models);
                for (MusicModel model : models) {
                    model.isSortChecked = false;
                }
                dialog.dismiss();
                adapter.notifyDataSetChanged();
                break;
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
        ivSelectAll.setText("全选");

        models = new ArrayList<>();
        models.addAll(MusciCst.models);

        adapter = new HandlerAdapter(this, models, R.layout.adapter_handler, this);
        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(new LinearLayoutManager(this));//为RecyclerView指定布局管理对象
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
    public void finish() {
        MusicModelEvent event = new MusicModelEvent(type, models);
        EventBus.getDefault().post(event);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        MusciCst.models.clear();
        super.onDestroy();
    }
}