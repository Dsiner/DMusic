package com.d.music.online.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.d.lib.common.component.mvp.base.BaseFragmentActivity;
import com.d.music.R;
import com.d.music.online.fragment.DetailFragment;
import com.d.music.utils.StatusBarCompat;

import cn.feng.skin.manager.loader.SkinManager;

/**
 * DetailActivity
 * Created by D on 2018/8/12.
 */
public class DetailActivity extends BaseFragmentActivity {
    public final static String ARG_TYPE = "type";
    public final static String ARG_CHANNEL = "channel";
    public final static String ARG_TITLE = "title";
    public final static String ARG_COVER = "cover";

    public final static int TYPE_BILL = 0;
    public final static int TYPE_RADIO = 1;

    public static void openActivity(Context context, int type, String... args) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(ARG_TYPE, type);
        if (args != null) {
            if (args.length > 0) {
                intent.putExtra(ARG_CHANNEL, args[0]);
            }
            if (args.length > 1) {
                intent.putExtra(ARG_TITLE, args[1]);
            }
            if (args.length > 2) {
                intent.putExtra(ARG_COVER, args[2]);
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
        int type = getIntent().getIntExtra(ARG_TYPE, TYPE_BILL);
        String channel = getIntent().getStringExtra(ARG_CHANNEL);
        String title = getIntent().getStringExtra(ARG_TITLE);
        String cover = getIntent().getStringExtra(ARG_COVER);

        DetailFragment fragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_TYPE, type);
        bundle.putString(ARG_CHANNEL, channel);
        bundle.putString(ARG_TITLE, title);
        bundle.putString(ARG_COVER, cover);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_content, fragment).commitAllowingStateLoss();
    }
}
