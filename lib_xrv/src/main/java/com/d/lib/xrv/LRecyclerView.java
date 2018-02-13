package com.d.lib.xrv;

import android.content.Context;
import android.util.AttributeSet;

/**
 * LRecyclerView-listview
 * Created by D on 2017/4/25.
 */

public class LRecyclerView extends ARecyclerView {
    public LRecyclerView(Context context) {
        this(context, null);
    }

    public LRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context) {
        showAsList();
    }

    @Override
    public void installHeader() {

    }

    @Override
    public void installFooter() {

    }

}
