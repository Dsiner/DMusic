package com.d.dmusic.mvp.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.d.commen.base.BaseActivity;
import com.d.commen.mvp.MvpBasePresenter;
import com.d.commen.mvp.MvpView;
import com.d.dmusic.R;
import com.d.dmusic.commen.Preferences;
import com.d.dmusic.module.repeatclick.ClickUtil;
import com.d.dmusic.mvp.adapter.SkinAdapter;
import com.d.dmusic.mvp.model.RadioModel;
import com.d.dmusic.utils.StatusBarCompat;
import com.d.dmusic.utils.Util;
import com.d.dmusic.view.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * SkinActivity
 * Created by D on 2017/6/13.
 */
public class SkinActivity extends BaseActivity<MvpBasePresenter> implements MvpView {
    @Bind(R.id.rv_list)
    RecyclerView rvList;

    private Preferences p;
    private SkinAdapter adapter;
    private List<RadioModel> datas;
    private int index;
    private List<String> skins;
    private int count = 19;

    @OnClick({R.id.iv_title_left, R.id.tv_title_right})
    public void onClickListener(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_title_left:
                finish();
                break;
            case R.id.tv_title_right:
                int i = adapter.getIndex();
                if (i >= 0 && i < skins.size()) {
                    p.putSkin(i);
                }
                finish();
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_skin;
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
        StatusBarCompat.compat(this, getResources().getColor(R.color.color_main));//沉浸式状态栏
        p = Preferences.getInstance(getApplicationContext());
        index = p.getSkin();
        skins = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            skins.add("skin" + i);
        }
        adapter = new SkinAdapter(this, getDatas(), R.layout.adapter_skin);
        adapter.setIndex(index);
        rvList.setLayoutManager(new GridLayoutManager(this, 3));
        rvList.addItemDecoration(new SpaceItemDecoration(Util.dip2px(this, 6)));
        rvList.setAdapter(adapter);
    }

    private List<RadioModel> getDatas() {
        datas = new ArrayList<>();
        for (int i = 0; i < 19; i++) {
            RadioModel model = new RadioModel();
            model.color = getC(i);
            model.isChecked = index == i;
            datas.add(model);
        }
        return datas;
    }

    private int getC(int i) {
        switch (i) {
            case 1:
                return R.color.color_main_skin1;
            case 2:
                return R.color.color_main_skin2;
            case 3:
                return R.color.color_main_skin3;
            case 4:
                return R.color.color_main_skin4;
            case 5:
                return R.color.color_main_skin5;
            case 6:
                return R.color.color_main_skin6;
            case 7:
                return R.color.color_main_skin7;
            case 8:
                return R.color.color_main_skin8;
            case 9:
                return R.color.color_main_skin9;
            case 10:
                return R.color.color_main_skin10;
            case 11:
                return R.color.color_main_skin11;
            case 12:
                return R.color.color_main_skin12;
            case 13:
                return R.color.color_main_skin13;
            case 14:
                return R.color.color_main_skin14;
            case 15:
                return R.color.color_main_skin15;
            case 16:
                return R.color.color_main_skin16;
            case 17:
                return R.color.color_main_skin17;
            case 18:
                return R.color.color_main_skin18;
            default:
                return R.color.color_main_skin0;
        }
    }
}
