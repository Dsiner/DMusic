package com.d.lib.xrv;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * ARecyclerView
 * Created by D on 2017/4/25.
 */
public abstract class ARecyclerView extends RecyclerView {

    public ARecyclerView(Context context) {
        this(context, null);
    }

    public ARecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ARecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
        installHeader();
        installFooter();
    }

    public abstract void init(Context context);

    public abstract void installHeader();

    public abstract void installFooter();

    /**
     * listview展现形式
     */
    public void showAsList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(layoutManager);
    }
}
