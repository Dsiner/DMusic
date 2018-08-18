package com.d.music.online.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.d.lib.common.module.mvp.base.BaseFragmentActivity;
import com.d.music.R;
import com.d.music.online.fragment.DetailFragment;
import com.d.music.utils.StatusBarCompat;

import cn.feng.skin.manager.loader.SkinManager;

/**
 * DetailActivity
 * Created by D on 2018/8/12.
 */
public class DetailActivity extends BaseFragmentActivity {
    public final static int TYPE_BILL = 0;
    public final static int TYPE_RADIO = 1;

    public static void openActivity(Context context, int type, String... args) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("type", type);
        if (args != null) {
            if (args.length > 0) {
                intent.putExtra("args", args[0]);
            }
            if (args.length > 1) {
                intent.putExtra("title", args[1]);
            }
        }
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.lib_pub_activity_abs_frg;
    }

    @Override
    protected void init() {
        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            findViewById(R.id.fragment_content).setFitsSystemWindows(true);
        }
        int type = getIntent().getIntExtra("type", 0);
        String args = getIntent().getStringExtra("args");
        String title = getIntent().getStringExtra("title");

        DetailFragment fragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putString("args", args);
        bundle.putString("title", title);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_content, fragment).commitAllowingStateLoss();
    }
}
