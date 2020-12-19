package com.d.music.online.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.d.lib.common.component.mvp.app.v4.BaseFragmentActivity;
import com.d.lib.common.component.statusbarcompat.StatusBarCompat;
import com.d.music.R;
import com.d.music.online.fragment.DetailFragment;

import cn.feng.skin.manager.loader.SkinManager;

/**
 * DetailActivity
 * Created by D on 2018/8/12.
 */
public class DetailActivity extends BaseFragmentActivity {
    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_CHANNEL = "channel";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_COVER = "cover";

    public static final int TYPE_ARTIST = 0;
    public static final int TYPE_BILL = 1;
    public static final int TYPE_RADIO = 2;

    public static void openActivity(Context context, int type, String... args) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_TYPE, type);
        if (args != null) {
            if (args.length > 0) {
                intent.putExtra(EXTRA_CHANNEL, args[0]);
            }
            if (args.length > 1) {
                intent.putExtra(EXTRA_TITLE, args[1]);
            }
            if (args.length > 2) {
                intent.putExtra(EXTRA_COVER, args[2]);
            }
        }
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.lib_pub_activity_loader_fragment;
    }

    @Override
    protected void init() {
        StatusBarCompat.setStatusBarColor(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            findViewById(R.id.fragment_content).setFitsSystemWindows(true);
        }
        int type = getIntent().getIntExtra(EXTRA_TYPE, TYPE_BILL);
        String channel = getIntent().getStringExtra(EXTRA_CHANNEL);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String cover = getIntent().getStringExtra(EXTRA_COVER);

        DetailFragment fragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_TYPE, type);
        bundle.putString(EXTRA_CHANNEL, channel);
        bundle.putString(EXTRA_TITLE, title);
        bundle.putString(EXTRA_COVER, cover);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_content, fragment).commitAllowingStateLoss();
    }
}
