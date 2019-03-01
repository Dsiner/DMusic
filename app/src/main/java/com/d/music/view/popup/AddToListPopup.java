package com.d.music.view.popup;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.d.lib.common.component.repeatclick.ClickFast;
import com.d.lib.common.utils.Util;
import com.d.lib.common.view.loading.LoadingLayout;
import com.d.lib.common.view.popup.AbstractPopup;
import com.d.lib.xrv.LRecyclerView;
import com.d.music.R;
import com.d.music.data.database.greendao.bean.CustomListModel;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.db.AppDB;
import com.d.music.data.database.greendao.util.AppDBUtil;
import com.d.music.event.eventbus.RefreshEvent;
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

    public AddToListPopup(Context context, int type, List<MusicModel> models) {
        super(context, R.layout.module_play_dialog_add_to_list);
        this.models = models;
        queryListNot(type);
    }

    @Override
    protected void init() {
        RelativeLayout rlytList = (RelativeLayout) mRootView.findViewById(R.id.rlyt_add_to_list);
        ViewGroup.LayoutParams lp = rlytList.getLayoutParams();
        lp.height = (int) (Util.getScreenSize((Activity) mContext)[1] * 0.382f);
        rlytList.setLayoutParams(lp);

        ldlLoading = (LoadingLayout) mRootView.findViewById(R.id.ldl_loading);
        LRecyclerView lrvList = (LRecyclerView) mRootView.findViewById(R.id.lrv_list);
        adapter = new AddToListAdapter(mContext, new ArrayList<CustomListModel>(), R.layout.module_play_adapter_add_to_list);
        lrvList.setAdapter(adapter);

        mRootView.findViewById(R.id.tv_ok).setOnClickListener(this);
        mRootView.findViewById(R.id.quit).setOnClickListener(this);
        mRootView.findViewById(R.id.v_blank).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (ClickFast.isFastDoubleClick()) {
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
        if (!isShowing() && mContext != null && !((Activity) mContext).isFinishing()) {
            showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
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
                List<CustomListModel> list = AppDBUtil.getIns(mContext).optCustomList().queryAllNot(notType);
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
                        if (mContext == null || ((Activity) mContext).isFinishing() || adapter == null) {
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
                        AppDBUtil.getIns(mContext).optMusic().insertOrReplaceInTx(b.pointer, models);

                        // 更新首页自定义列表歌曲数
                        final int index = b.pointer - AppDB.CUSTOM_MUSIC_INDEX;
                        if (index >= 0 && index < AppDB.CUSTOM_MUSIC_COUNT) {
                            Cursor cursor = AppDBUtil.getIns(mContext).optMusic().queryBySQL("SELECT COUNT(*) FROM CUSTOM_MUSIC" + index);
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
                            AppDBUtil.getIns(mContext).optCustomList().updateCount(b.pointer, count);
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
                        if (mContext == null || ((Activity) mContext).isFinishing()) {
                            return;
                        }
                        closeLoading();
                        if (isEmpty) {
                            Util.toast(mContext, mContext.getResources().getString(R.string.module_common_please_select));
                        } else {
                            Util.toast(mContext, mContext.getResources().getString(R.string.module_common_add_success));
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
