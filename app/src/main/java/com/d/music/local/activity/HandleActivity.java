package com.d.music.local.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.component.mvp.app.BaseActivity;
import com.d.lib.common.component.quickclick.QuickClick;
import com.d.lib.common.component.statusbarcompat.StatusBarCompat;
import com.d.lib.common.util.ToastUtils;
import com.d.lib.common.util.ViewHelper;
import com.d.lib.common.widget.TitleLayout;
import com.d.lib.pulllayout.rv.itemtouchhelper.OnStartDragListener;
import com.d.lib.pulllayout.rv.itemtouchhelper.SimpleItemTouchHelperCallback;
import com.d.music.R;
import com.d.music.component.media.SyncManager;
import com.d.music.data.Constants;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.db.AppDatabase;
import com.d.music.event.eventbus.MusicModelEvent;
import com.d.music.event.eventbus.SortTypeEvent;
import com.d.music.local.adapter.HandleAdapter;
import com.d.music.widget.popup.AddToListPopup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.feng.skin.manager.loader.SkinManager;

/**
 * 歌曲列表排序、管理
 * Created by D on 2017/4/28.
 */
public class HandleActivity extends BaseActivity<MvpBasePresenter>
        implements MvpView, View.OnClickListener, OnStartDragListener {

    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_TITLE = "title";

    TitleLayout tl_title;
    TextView tv_title_right;
    RecyclerView rv_list;

    private int mType;
    private String mTitle = "";
    private List<MusicModel> mModelsFav;
    private HandleAdapter mHandleAdapter;
    private ItemTouchHelper mItemTouchHelper;

    public static void openActivity(Context context, int type, String title) {
        Intent intent = new Intent(context, HandleActivity.class);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_TITLE, title);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (QuickClick.isQuickClick()) {
            return;
        }

        final List<MusicModel> datas = new ArrayList<>(mHandleAdapter.getDatas());
        switch (v.getId()) {
            case R.id.iv_title_left:
                finish();
                break;

            case R.id.tv_title_right:
                final boolean isAll = !((boolean) tv_title_right.getTag());
                tv_title_right.setTag(isAll);
                int count = 0;
                for (MusicModel model : datas) {
                    model.exIsSortChecked = isAll || !model.exIsSortChecked;
                    if (model.exIsSortChecked) {
                        count++;
                    }
                }
                setCount(count);
                tv_title_right.setText(isAll ? getResources().getString(R.string.module_common_inverse_selection)
                        : getResources().getString(R.string.module_common_select_all));
                mHandleAdapter.setCount(count);
                mHandleAdapter.setDatas(datas);
                mHandleAdapter.notifyDataSetChanged();
                break;

            case R.id.llyt_add_to_list:
                if (mHandleAdapter.getCount() <= 0) {
                    ToastUtils.toast(mContext, getResources().getString(R.string.module_common_please_select));
                    return;
                }
                final List<MusicModel> list = new ArrayList<>();
                for (MusicModel musicModel : datas) {
                    if (musicModel.exIsSortChecked) {
                        list.add(musicModel);
                    }
                }
                new AddToListPopup(this, mType, list).show();
                break;

            case R.id.llyt_delete:
                int c = mHandleAdapter.getCount();
                if (c <= 0) {
                    ToastUtils.toast(mContext, getResources().getString(R.string.module_common_please_select));
                    return;
                }
                showLoadingDialog();
                for (int i = datas.size() - 1; i >= 0; i--) {
                    MusicModel m = datas.get(i);
                    if (m.exIsSortChecked) {
                        datas.remove(m);
                        if (mType == AppDatabase.COLLECTION_MUSIC) {
                            mModelsFav.add(m);
                        }
                        c--;
                    }
                }
                setCount(c);
                mHandleAdapter.setCount(0);
                mHandleAdapter.setDatas(datas);
                mHandleAdapter.notifyDataSetChanged();
                dismissLoadingDialog();
                break;

            case R.id.llyt_revoke:
                showLoadingDialog();
                datas.clear();
                datas.addAll(Constants.Heap.sModels);
                for (MusicModel model : datas) {
                    model.exIsSortChecked = false;
                }
                if (mType == AppDatabase.COLLECTION_MUSIC) {
                    mModelsFav.clear();
                }
                setCount(0);
                mHandleAdapter.setCount(0);
                mHandleAdapter.setDatas(datas);
                mHandleAdapter.notifyDataSetChanged();
                dismissLoadingDialog();
                break;
        }
    }

    private void setCount(int count) {
        String mark = count > 0 ? String.format(mContext.getResources().getString(
                R.string.module_common_selected_with_parentheses), count) : "";
        tl_title.setText(R.id.tv_title_title, mTitle + mark);
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
    protected void bindView() {
        tl_title = findViewById(R.id.tl_title);
        tv_title_right = findViewById(R.id.tv_title_right);
        rv_list = findViewById(R.id.rv_list);

        ViewHelper.setOnClickListener(this, this,
                R.id.iv_title_left,
                R.id.tv_title_right,
                R.id.llyt_add_to_list,
                R.id.llyt_delete,
                R.id.llyt_revoke);
    }

    @Override
    protected void init() {
        StatusBarCompat.setStatusBarColor(HandleActivity.this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
        initTitle();

        mModelsFav = new ArrayList<>();
        mHandleAdapter = new HandleAdapter(mContext,
                new ArrayList<>(Constants.Heap.sModels),
                R.layout.module_local_adapter_handler);
        mHandleAdapter.setOnStartDragListener(this);
        mHandleAdapter.setOnChangeListener(new HandleAdapter.OnChangeListener() {
            @Override
            public void onDelete(MusicModel model) {
                if (mType == AppDatabase.COLLECTION_MUSIC) {
                    mModelsFav.add(model);
                }
            }

            @Override
            public void onCountChange(int count) {
                setCount(count);
            }
        });

        rv_list.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_list.setLayoutManager(layoutManager);
        rv_list.setAdapter(mHandleAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mHandleAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rv_list);
    }

    private void initTitle() {
        Intent intent = getIntent();
        if (intent != null) {
            mType = intent.getIntExtra(EXTRA_TYPE, 0);
            mTitle = intent.getStringExtra(EXTRA_TITLE);
        }
        tl_title.setText(R.id.tv_title_title, mTitle);
        tv_title_right.setTag(false);
        tv_title_right.setText(getResources().getString(R.string.module_common_select_all));
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void finish() {
        EventBus.getDefault().post(new MusicModelEvent(mType,
                new ArrayList<>(mHandleAdapter.getDatas())));
        // 按自定义排序
        EventBus.getDefault().post(new SortTypeEvent(mType, AppDatabase.ORDER_TYPE_CUSTOM));
        if (mType == AppDatabase.COLLECTION_MUSIC) {
            SyncManager.unCollected(getApplicationContext(), mModelsFav);
        }
        super.finish();
    }

//    @Override
//    public void onThemeUpdate() {
//        super.onThemeUpdate();
//        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
//    }

    @Override
    protected void onDestroy() {
        if (Constants.Heap.sModels != null) {
            Constants.Heap.sModels.clear();
        }
        super.onDestroy();
    }
}