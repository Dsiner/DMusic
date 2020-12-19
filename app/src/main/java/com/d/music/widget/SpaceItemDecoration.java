package com.d.music.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * SpaceItemDecoration
 * Created by D on 2016/8/6.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int mSpace;

    public SpaceItemDecoration(int space) {
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = mSpace;
        outRect.right = mSpace;
        outRect.top = mSpace;
        outRect.bottom = mSpace;
    }
}