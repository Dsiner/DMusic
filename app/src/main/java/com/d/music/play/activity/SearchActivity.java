package com.d.music.play.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.d.lib.common.component.mvp.app.v4.BaseFragmentActivity;
import com.d.music.R;
import com.d.music.play.fragment.SearchFragment;
import com.d.music.utils.StatusBarCompat;

import cn.feng.skin.manager.loader.SkinManager;

/**
 * SearchActivity
 * Created by D on 2018/8/13.
 */
public class SearchActivity extends BaseFragmentActivity {
    public final static String ARG_ID = "id";

    private SearchFragment fragment;

    public static void openActivity(Context context, long id) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(ARG_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.lib_pub_activity_abs_frg;
    }

    @Override
    protected void init() {
        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
        long id = getIntent().getLongExtra(ARG_ID, 0);
        fragment = new SearchFragment();
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
