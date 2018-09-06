package com.d.music.local.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.component.mvp.base.BaseActivity;
import com.d.lib.common.component.repeatclick.ClickFast;
import com.d.lib.common.utils.Util;
import com.d.lib.common.view.TitleLayout;
import com.d.lib.xrv.itemtouchhelper.OnStartDragListener;
import com.d.lib.xrv.itemtouchhelper.SimpleItemTouchHelperCallback;
import com.d.music.R;
import com.d.music.common.Constants;
import com.d.music.local.adapter.HandleAdapter;
import com.d.music.component.events.MusicModelEvent;
import com.d.music.component.events.SortTypeEvent;
import com.d.music.component.greendao.bean.MusicModel;
import com.d.music.component.greendao.db.AppDB;
import com.d.music.component.media.SyncManager;
import com.d.music.utils.StatusBarCompat;
import com.d.music.view.popup.AddToListPopup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.feng.skin.manager.loader.SkinManager;

/**
 * 歌曲列表排序、管理
 * Created by D on 2017/4/28.
 */
public class HandleActivity extends BaseActivity<MvpBasePresenter>
        implements MvpView, OnStartDragListener, HandleAdapter.OnChangeListener {
    public final static String ARG_TYPE = "type";
    public final static String ARG_TITLE = "title";

    @BindView(R.id.tl_title)
    TitleLayout tlTitle;
    @BindView(R.id.tv_title_right)
    TextView tvRight;
    @BindView(R.id.rv_list)
    RecyclerView rvList;

    private int type;
    private String title = "";
    private List<MusicModel> models;
    private List<MusicModel> modelsFav;
    private HandleAdapter adapter;
    private ItemTouchHelper itemTouchHelper;

    public static void startActivity(Context context, int type, String title) {
        Intent intent = new Intent(context, HandleActivity.class);
        intent.putExtra(ARG_TYPE, type);
        intent.putExtra(ARG_TITLE, title);
        context.startActivity(intent);
    }

    @OnClick({R.id.iv_title_left, R.id.tv_title_right, R.id.llyt_add_to_list,
            R.id.llyt_delete, R.id.llyt_revoke})
    public void onClick(View v) {
        if (ClickFast.isFastDoubleClick()) {
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
                    model.exIsSortChecked = isAll || !model.exIsSortChecked;
                    if (model.exIsSortChecked) {
                        count++;
                    }
                }
                setCount(count);
                tvRight.setText(isAll ? getResources().getString(R.string.module_common_inverse_selection)
                        : getResources().getString(R.string.module_common_select_all));
                adapter.setCount(count);
                adapter.notifyDataSetChanged();
                break;
            case R.id.llyt_add_to_list:
                if (adapter.getCount() <= 0) {
                    Util.toast(mContext, getResources().getString(R.string.module_common_please_select));
                    return;
                }
                List<MusicModel> list = new ArrayList<>();
                for (MusicModel musicModel : models) {
                    if (musicModel.exIsSortChecked) {
                        list.add(musicModel);
                    }
                }
                new AddToListPopup(this, type, list).show();
                break;
            case R.id.llyt_delete:
                int c = adapter.getCount();
                if (c <= 0) {
                    Util.toast(mContext, getResources().getString(R.string.module_common_please_select));
                    return;
                }
                showLoading();
                for (int i = models.size() - 1; i >= 0; i--) {
                    MusicModel m = models.get(i);
                    if (m.exIsSortChecked) {
                        models.remove(m);
                        if (type == AppDB.COLLECTION_MUSIC) {
                            modelsFav.add(m);
                        }
                        c--;
                    }
                }
                setCount(c);
                adapter.setCount(0);
                adapter.notifyDataSetChanged();
                closeLoading();
                break;
            case R.id.llyt_revoke:
                showLoading();
                models.clear();
                models.addAll(Constants.Heap.models);
                for (MusicModel model : models) {
                    model.exIsSortChecked = false;
                }
                if (type == AppDB.COLLECTION_MUSIC) {
                    modelsFav.clear();
                }
                setCount(0);
                adapter.setCount(0);
                adapter.notifyDataSetChanged();
                closeLoading();
                break;
        }
    }

    private void setCount(int count) {
        String mark = count > 0 ? getResources().getString(R.string.module_common_selected_left)
                + count + getResources().getString(R.string.module_common_selected_right) : "";
        tlTitle.setText(R.id.tv_title_title, title + mark);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.module_local_activity_handle;
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
        StatusBarCompat.compat(HandleActivity.this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
        initTitle();

        models = new ArrayList<>();
        modelsFav = new ArrayList<>();
        models.addAll(Constants.Heap.models);
        adapter = new HandleAdapter(mContext, models, R.layout.module_local_adapter_handler);
        adapter.setOnStartDragListener(this);
        adapter.setOnChangeListener(this);
        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rvList);
    }

    private void initTitle() {
        Intent intent = getIntent();
        if (intent != null) {
            type = intent.getIntExtra(ARG_TYPE, 0);
            title = intent.getStringExtra(ARG_TITLE);
        }
        tlTitle.setText(R.id.tv_title_title, title);
        tvRight.setTag(false);
        tvRight.setText(getResources().getString(R.string.module_common_select_all));
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onDelete(MusicModel model) {
        if (type == AppDB.COLLECTION_MUSIC) {
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
        // 按自定义排序
        EventBus.getDefault().post(new SortTypeEvent(type, AppDB.ORDER_TYPE_CUSTOM));
        if (type == AppDB.COLLECTION_MUSIC) {
            SyncManager.unCollected(getApplicationContext(), modelsFav);
        }
        super.finish();
    }

    @Override
    public void onThemeUpdate() {
        super.onThemeUpdate();
        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
    }

    @Override
    protected void onDestroy() {
        if (Constants.Heap.models != null) {
            Constants.Heap.models.clear();
        }
        super.onDestroy();
    }
}