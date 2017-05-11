package com.d.dmusic.view.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.d.dmusic.R;
import com.d.dmusic.module.global.Cst;
import com.d.dmusic.module.greendao.db.MusicDB;
import com.d.dmusic.mvp.activity.ScanActivity;

/**
 * MenuDialog
 * Created by D on 2017/4/29.
 */
public class MenuDialog extends AbstractDialog implements View.OnClickListener {
    private LinearLayout llytBlank;
    private View vBlank;
    private LinearLayout llytSortByName;// 按名称排序
    private LinearLayout llytSortByTime;// 按时间排序
    private LinearLayout llytSortByCustom;// 自定义排序
    private LinearLayout llytScanMore;
    private int type;

    public MenuDialog(Context context, int type) {
        super(context, R.style.PopTopInDialog, true, Gravity.TOP, Cst.SCREEN_WIDTH, Cst.SCREEN_HEIGHT);
        this.type = type;
        initType(type);
    }

    private void initType(int type) {
        switch (type) {
            case MusicDB.LOCAL_ALL_MUSIC:
                break;
            case MusicDB.COLLECTION_MUSIC:
                llytScanMore.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.dialog_more;
    }

    @Override
    protected void init(View rootView) {
        llytBlank = (LinearLayout) rootView.findViewById(R.id.llyt_more);
        vBlank = rootView.findViewById(R.id.v_blank);
        llytSortByName = (LinearLayout) rootView.findViewById(R.id.llyt_sort_by_name);
        llytSortByTime = (LinearLayout) rootView.findViewById(R.id.llyt_sort_by_time);
        llytSortByCustom = (LinearLayout) rootView.findViewById(R.id.llyt_sort_by_custom);
        llytScanMore = (LinearLayout) rootView.findViewById(R.id.llyt_scan_more);

        llytBlank.setOnClickListener(this);
        vBlank.setOnClickListener(this);
        llytSortByName.setOnClickListener(this);
        llytSortByTime.setOnClickListener(this);
        llytSortByCustom.setOnClickListener(this);
        llytScanMore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llyt_more:
            case R.id.v_blank:
                dismiss();
                break;
            // 按名称查询
            case R.id.llyt_sort_by_name:
//                sortBy = 1;
//                mPresenter.sortBy(sortBy, id);
                break;
            // 按时间查询
            case R.id.llyt_sort_by_time:
//                sortBy = 2;
//                mPresenter.sortBy(sortBy, id);
                break;
            // 按时间查询
            case R.id.llyt_sort_by_custom:
//                sortBy = 3;
//                mPresenter.sortBy(sortBy, id);
                break;
            case R.id.llyt_scan_more:
                Intent intent = new Intent(context, ScanActivity.class);
                intent.putExtra("type", type);
                context.startActivity(intent);
                dismiss();
                break;
        }
    }
}
