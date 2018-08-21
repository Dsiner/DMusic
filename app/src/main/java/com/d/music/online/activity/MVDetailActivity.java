package com.d.music.online.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.d.lib.common.module.mvp.base.BaseFragmentActivity;
import com.d.music.R;
import com.d.music.online.fragment.MVDetailFragment;
import com.d.music.utils.StatusBarCompat;

import cn.feng.skin.manager.loader.SkinManager;

/**
 * MVDetailActivity
 * Created by D on 2018/8/13.
 */
public class MVDetailActivity extends BaseFragmentActivity {
    public final static String ARG_ID = "id";

    private MVDetailFragment fragment;

    public static void openActivity(Context context, long id) {
        Intent intent = new Intent(context, MVDetailActivity.class);
        intent.putExtra(ARG_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.lib_pub_activity_abs_frg;
    }

    @Override
    protected void init() {
        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_black));
        long id = getIntent().getLongExtra(ARG_ID, 0);
        fragment = new MVDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_ID, id);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_content, fragment).commitAllowingStateLoss();
    }

    public void onBackPressed() {
        if (fragment != null && fragment.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
