package com.d.music.view.popup;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.d.lib.common.module.repeatclick.ClickUtil;
import com.d.lib.common.utils.Util;
import com.d.lib.common.view.loading.LoadingLayout;
import com.d.lib.common.view.popup.AbstractPopup;
import com.d.lib.xrv.LRecyclerView;
import com.d.music.R;
import com.d.music.module.events.RefreshEvent;
import com.d.music.module.greendao.bean.CustomListModel;
import com.d.music.module.greendao.bean.MusicModel;
import com.d.music.module.greendao.db.AppDB;
import com.d.music.module.greendao.util.AppDBUtil;
import com.d.music.play.adapter.AddToListAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * AddToListPopup
 * Created by D on 2017/4/29.
 */
public class AddToListPopup extends AbstractPopup implements View.OnClickListener {
    private LoadingLayout ldlLoading;
    private AddToListAdapter adapter;

    /**
     * 待插入歌曲队列
     */
    private List<MusicModel> models;

    public AddToListPopup(Context context, int type,List<MusicModel> models) {
        super(context, R.layout.module_play_dialog_add_to_list);
        this.models = models;
        queryListNot(type);
    }

    @Override
    protected void init() {
        RelativeLayout rlytList = (RelativeLayout) rootView.findViewById(R.id.rlyt_add_to_list);
        ViewGroup.LayoutParams lp = rlytList.getLayoutParams();
        lp.height = (int) (Util.getScreenSize((Activity) context)[1] * 0.382f);
        rlytList.setLayoutParams(lp);

        ldlLoading = (LoadingLayout) rootView.findViewById(R.id.ldl_loading);
        LRecyclerView lrvList = (LRecyclerView) rootView.findViewById(R.id.lrv_list);
        adapter = new AddToListAdapter(context, new ArrayList<CustomListModel>(), R.layout.module_play_adapter_add_to_list);
        lrvList.setAdapter(adapter);

        rootView.findViewById(R.id.tv_ok).setOnClickListener(this);
        rootView.findViewById(R.id.quit).setOnClickListener(this);
        rootView.findViewById(R.id.v_blank).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.v_blank:
            case R.id.quit:
                dismiss();
                break;
            case R.id.tv_ok:
                addTo();
                break;
        }
    }

    @Override
    public void show() {
        if (!isShowing() && context != null && !((Activity) context).isFinishing()) {
            showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
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
        Observable.create(new ObservableOnSubscribe<List<CustomListModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<CustomListModel>> e) throws Exception {
                List<CustomListModel> list = AppDBUtil.getIns(context).optCustomList().queryAllNot(notType);
                if (list == null) {
                    list = new ArrayList<>();
                }
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<CustomListModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<CustomListModel> list) {
                        if (context == null || ((Activity) context).isFinishing() || adapter == null) {
                            return;
                        }
                        closeLoading();
                        adapter.setDatas(list);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void addTo() {
        showLoading();
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                Boolean isEmpty = true;
                List<CustomListModel> list = adapter.getDatas();// 除当前列表外的自定义列表队列
                if (list != null) {
                    for (CustomListModel b : list) {
                        if (!b.exIsChecked) {
                            continue;
                        }
                        isEmpty = false;
                        AppDBUtil.getIns(context).optMusic().insertOrReplaceInTx(b.pointer, models);

                        // 更新首页自定义列表歌曲数
                        final int index = b.pointer - AppDB.CUSTOM_MUSIC_INDEX;
                        if (index >= 0 && index < AppDB.CUSTOM_MUSIC_COUNT) {
                            Cursor cursor = AppDBUtil.getIns(context).optMusic().queryBySQL("SELECT COUNT(*) FROM CUSTOM_MUSIC" + index);
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
                            AppDBUtil.getIns(context).optCustomList().updateCount(b.pointer, count);
                        }
                    }
                }
                e.onNext(isEmpty);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean isEmpty) {
                        if (context == null || ((Activity) context).isFinishing()) {
                            return;
                        }
                        closeLoading();
                        if (isEmpty) {
                            Util.toast(context, "请先选择");
                        } else {
                            Util.toast(context, "成功添加");
                            // 更新首页自定义列表
                            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_INVALID, RefreshEvent.SYNC_CUSTOM_LIST));
                            dismiss();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
