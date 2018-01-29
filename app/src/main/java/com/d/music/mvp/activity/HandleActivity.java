package com.d.music.mvp.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import com.d.commen.base.BaseActivity;
import com.d.commen.mvp.MvpBasePresenter;
import com.d.commen.mvp.MvpView;
import com.d.lib.xrv.itemtouchhelper.OnStartDragListener;
import com.d.lib.xrv.itemtouchhelper.SimpleItemTouchHelperCallback;
import com.d.music.R;
import com.d.commen.commen.AlertDialogFactory;
import com.d.music.module.events.MusicModelEvent;
import com.d.music.module.events.SortTypeEvent;
import com.d.music.module.global.MusicCst;
import com.d.music.module.greendao.db.MusicDB;
import com.d.music.module.greendao.music.base.MusicModel;
import com.d.music.module.media.SyncUtil;
import com.d.commen.module.repeatclick.ClickUtil;
import com.d.music.mvp.adapter.HandleAdapter;
import com.d.music.utils.StatusBarCompat;
import com.d.commen.utils.Util;
import com.d.commen.view.TitleLayout;
import com.d.music.view.popup.AddToListPopup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.feng.skin.manager.loader.SkinManager;

/**
 * 歌曲列表排序、管理
 * Created by D on 2017/4/28.
 */
public class HandleActivity extends BaseActivity<MvpBasePresenter> implements MvpView, OnStartDragListener, HandleAdapter.OnChangeListener {
    @Bind(R.id.tl_title)
    TitleLayout tlTitle;
    @Bind(R.id.tv_title_right)
    TextView tvRight;
    @Bind(R.id.rv_list)
    RecyclerView rvList;

    private Context context;
    private int type;
    private String title = "";
    private List<MusicModel> models;
    private List<MusicModel> modelsFav;
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
                setCount(count);
                tvRight.setText(isAll ? "反选" : "全选");
                adapter.setCount(count);
                adapter.notifyDataSetChanged();
                break;
            case R.id.llyt_add_to_list:
                if (adapter.getCount() <= 0) {
                    Util.toast(context, "请先选择");
                    return;
                }
                List<MusicModel> list = new ArrayList<>();
                for (MusicModel musicModel : models) {
                    if (musicModel.isSortChecked) {
                        list.add(musicModel);
                    }
                }
                new AddToListPopup(this, list, type).show();
                break;
            case R.id.llyt_delete:
                int c = adapter.getCount();
                if (c <= 0) {
                    Util.toast(context, "请先选择");
                    return;
                }
                dialog = AlertDialogFactory.createFactory(this).getLoadingDialog();
                for (int i = models.size() - 1; i >= 0; i--) {
                    MusicModel m = models.get(i);
                    if (m.isSortChecked) {
                        models.remove(m);
                        if (type == MusicDB.COLLECTION_MUSIC) {
                            modelsFav.add(m);
                        }
                        c--;
                    }
                }
                setCount(c);
                adapter.setCount(0);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
                break;
            case R.id.llyt_revoke:
                dialog = AlertDialogFactory.createFactory(this).getLoadingDialog();
                models.clear();
                models.addAll(MusicCst.models);
                for (MusicModel model : models) {
                    model.isSortChecked = false;
                }
                if (type == MusicDB.COLLECTION_MUSIC) {
                    modelsFav.clear();
                }
                setCount(0);
                adapter.setCount(0);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
                break;
        }
    }

    private void setCount(int count) {
        String mark = count > 0 ? " (已选" + count + ")" : "";
        tlTitle.setText(R.id.tv_title_title, title + mark);
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
        StatusBarCompat.compat(HandleActivity.this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));//沉浸式状态栏
        context = this;
        initTitle();

        models = new ArrayList<>();
        modelsFav = new ArrayList<>();
        models.addAll(MusicCst.models);
        adapter = new HandleAdapter(context, models, R.layout.adapter_handler);
        adapter.setOnStartDragListener(this);
        adapter.setOnChangeListener(this);
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
    public void onDelete(MusicModel model) {
        if (type == MusicDB.COLLECTION_MUSIC) {
            modelsFav.add(model);
        }
    }

    @Override
    public void onCountChange(int count) {
        setCount(count);
    }

    @Override
    public void finish() {
        EventBus.getDefault().post(new MusicModelEvent(type, models));
        EventBus.getDefault().post(new SortTypeEvent(type, MusicDB.ORDER_TYPE_CUSTOM));//按自定义排序
        if (type == MusicDB.COLLECTION_MUSIC) {
            SyncUtil.unCollected(getApplicationContext(), modelsFav);
        }
        super.finish();
    }

    @Override
    public void onThemeUpdate() {
        super.onThemeUpdate();
        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));//沉浸式状态栏
    }

    @Override
    protected void onDestroy() {
        if (MusicCst.models != null) {
            MusicCst.models.clear();
        }
        super.onDestroy();
    }
}