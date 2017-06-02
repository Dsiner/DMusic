package com.d.dmusic.view.popup;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.d.dmusic.R;
import com.d.dmusic.module.events.RefreshEvent;
import com.d.dmusic.module.global.Cst;
import com.d.dmusic.module.greendao.db.MusicDB;
import com.d.dmusic.module.greendao.music.CustomList;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.module.greendao.util.MusicDBUtil;
import com.d.dmusic.module.repeatclick.ClickUtil;
import com.d.dmusic.mvp.adapter.AddToListAdapter;
import com.d.dmusic.utils.Util;
import com.d.dmusic.view.loading.LoadingLayout;
import com.d.lib.xrv.LRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * AddToListPopup
 * Created by D on 2017/4/29.
 */
public class AddToListPopup extends AbstractPopup implements View.OnClickListener {
    private LoadingLayout ldlLoading;
    private AddToListAdapter adapter;
    private List<MusicModel> models;//待插入歌曲队列

    public AddToListPopup(Context context, List<MusicModel> models, int type) {
        super(context);
        this.models = models;
        queryListNot(type);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.dialog_add_to_list;
    }

    @Override
    protected void init() {
        RelativeLayout rlytList = (RelativeLayout) rootView.findViewById(R.id.rlyt_add_to_list);
        ViewGroup.LayoutParams lp = rlytList.getLayoutParams();
        lp.height = (int) (Cst.SCREEN_HEIGHT * 0.382f);
        rlytList.setLayoutParams(lp);

        ldlLoading = (LoadingLayout) rootView.findViewById(R.id.ldl_loading);
        LRecyclerView lrvList = (LRecyclerView) rootView.findViewById(R.id.lrv_list);
        adapter = new AddToListAdapter(context, new ArrayList<CustomList>(), R.layout.adapter_add_to_list);
        lrvList.setAdapter(adapter);

        rootView.findViewById(R.id.tv_ok).setOnClickListener(this);
        rootView.findViewById(R.id.iv_quit).setOnClickListener(this);
        rootView.findViewById(R.id.v_blank).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.v_blank:
            case R.id.iv_quit:
                dismiss();
                break;
            case R.id.tv_ok:
                addTo();
                break;
        }
    }

    @Override
    public void show() {
        if (popupWindow != null && !popupWindow.isShowing() && context != null && !((Activity) context).isFinishing()) {
            popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        }
    }

    private void showLoading() {
        if (ldlLoading != null) {
            ldlLoading.setVisibility(View.VISIBLE);
        }
    }

    private void closeLoading() {
        if (ldlLoading != null) {
            ldlLoading.setVisibility(View.GONE);
        }
    }

    private void queryListNot(final int notType) {
        showLoading();
        Observable.create(new ObservableOnSubscribe<List<CustomList>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<CustomList>> e) throws Exception {
                List<CustomList> list = MusicDBUtil.getInstance(context).queryAllCustomList(notType);
                if (list == null) {
                    list = new ArrayList<>();
                }
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<CustomList>>() {
                    @Override
                    public void accept(@NonNull List<CustomList> list) throws Exception {
                        if (context == null || ((Activity) context).isFinishing() || adapter == null) {
                            return;
                        }
                        closeLoading();
                        adapter.setDatas(list);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void addTo() {
        showLoading();
        Observable.create(new ObservableOnSubscribe<List<CustomList>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<CustomList>> e) throws Exception {
                List<CustomList> list = adapter.getDatas();// 除当前列表外的自定义列表队列
                if (list != null) {
                    for (CustomList b : list) {
                        if (!b.isChecked) {
                            continue;
                        }
                        MusicDBUtil.getInstance(context).insertOrReplaceMusicInTx(MusicModel.clone(models, b.pointer), b.pointer);

                        //更新首页自定义列表歌曲数
                        final int index = b.pointer - MusicDB.CUSTOM_MUSIC_INDEX;
                        if (index >= 0 && index < MusicDB.CUSTOM_MUSIC_COUNT) {
                            Cursor cursor = MusicDBUtil.getInstance(context).queryBySQL("SELECT COUNT(*) FROM CUSTOM_MUSIC" + index);
                            Integer count = 0;
                            if (cursor != null && cursor.moveToFirst()) {
                                int indexCount = cursor.getColumnIndex("COUNT(*)");
                                if (indexCount != -1) {
                                    count = cursor.getInt(indexCount);
                                }
                            }
                            if (cursor != null) {
                                cursor.close();
                            }
                            MusicDBUtil.getInstance(context).updateCusListCount(b.pointer, count);
                        }
                    }
                } else {
                    list = new ArrayList<>();
                }
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<CustomList>>() {
                    @Override
                    public void accept(@NonNull List<CustomList> list) throws Exception {
                        if (context == null || ((Activity) context).isFinishing()) {
                            return;
                        }
                        Util.toast(context, "成功添加");

                        //更新首页自定义列表
                        EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_INVALID, RefreshEvent.SYNC_CUSTOM_LIST));
                        dismiss();
                    }
                });
    }
}
