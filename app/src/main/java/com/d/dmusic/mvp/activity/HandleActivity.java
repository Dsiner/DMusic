package com.d.dmusic.mvp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import com.d.commen.base.BaseActivity;
import com.d.commen.mvp.MvpBasePresenter;
import com.d.commen.mvp.MvpView;
import com.d.dmusic.R;
import com.d.dmusic.commen.AlertDialogFactory;
import com.d.dmusic.module.events.MusicModelEvent;
import com.d.dmusic.module.global.MusicCst;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.module.repeatclick.ClickUtil;
import com.d.dmusic.mvp.adapter.HandleAdapter;
import com.d.dmusic.utils.StatusBarCompat;
import com.d.dmusic.view.TitleLayout;
import com.d.dmusic.view.popup.AddToListPopup;
import com.d.lib.xrv.itemtouchhelper.OnStartDragListener;
import com.d.lib.xrv.itemtouchhelper.SimpleItemTouchHelperCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 歌曲列表排序、管理
 * Created by D on 2017/4/28.
 */
public class HandleActivity extends BaseActivity<MvpBasePresenter> implements MvpView, OnStartDragListener {
    @Bind(R.id.tl_title)
    TitleLayout tlTitle;
    @Bind(R.id.tv_title_right)
    TextView tvRight;
    @Bind(R.id.rv_list)
    RecyclerView rvList;

    private int type;
    private String title = "";
    private List<MusicModel> models;
    private HandleAdapter adapter;
    private ItemTouchHelper itemTouchHelper;
    private AlertDialog dialog;

    @OnClick({R.id.iv_title_left, R.id.tv_title_right, R.id.llyt_add_to_list,
            R.id.llyt_delete, R.id.llyt_revoke})
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_title_left:
                finish();
                break;
            case R.id.tv_title_right:
                final boolean isAll = !((boolean) tvRight.getTag());
                tvRight.setTag(isAll);
                int count = 0;
                for (MusicModel model : models) {
                    model.isSortChecked = isAll || !model.isSortChecked;
                    if (model.isSortChecked) {
                        count++;
                    }
                }
                String mark = count > 0 ? " (已选" + count + ")" : "";
                tlTitle.setText(R.id.tv_title_title, title + mark);
                tvRight.setText(isAll ? "反选" : "全选");
                adapter.notifyDataSetChanged();
                break;
            case R.id.llyt_add_to_list:
                List<MusicModel> list = new ArrayList<>();
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
                models.addAll(MusicCst.models);
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
        return R.layout.activity_handle;
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
    protected void init() {
        StatusBarCompat.compat(HandleActivity.this, getResources().getColor(R.color.color_main));//沉浸式状态栏
        initTitle();

        models = new ArrayList<>();
        models.addAll(MusicCst.models);
        adapter = new HandleAdapter(HandleActivity.this, models, R.layout.adapter_handler);
        adapter.setOnStartDragListener(this);
        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(new LinearLayoutManager(this));//为RecyclerView指定布局管理对象
        rvList.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rvList);
    }

    private void initTitle() {
        Intent intent = getIntent();
        if (intent != null) {
            type = intent.getIntExtra("type", 0);
            title = intent.getStringExtra("title");
        }
        tlTitle.setText(R.id.tv_title_title, title);
        tvRight.setTag(false);
        tvRight.setText("全选");
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
        MusicCst.models.clear();
        super.onDestroy();
    }
}