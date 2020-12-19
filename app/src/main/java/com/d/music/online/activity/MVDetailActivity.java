package com.d.music.online.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.d.lib.common.component.mvp.app.v4.BaseFragmentActivity;
import com.d.lib.common.component.statusbarcompat.StatusBarCompat;
import com.d.music.R;
import com.d.music.online.fragment.MVDetailFragment;

import cn.feng.skin.manager.loader.SkinManager;

/**
 * MVDetailActivity
 * Created by D on 2018/8/13.
 */
public class MVDetailActivity extends BaseFragmentActivity {
    public static final String EXTRA_ID = "id";

    private MVDetailFragment mFragment;

    public static void openActivity(Context context, long id) {
        Intent intent = new Intent(context, MVDetailActivity.class);
        intent.putExtra(EXTRA_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.lib_pub_activity_loader_fragment;
    }

    @Override
    protected void init() {
        StatusBarCompat.setStatusBarColor(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_black));
        long id = getIntent().getLongExtra(EXTRA_ID, 0);
        mFragment = new MVDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_ID, id);
        mFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_content, mFragment).commitAllowingStateLoss();
    }

    public void onBackPressed() {
        if (mFragment != null && mFragment.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
