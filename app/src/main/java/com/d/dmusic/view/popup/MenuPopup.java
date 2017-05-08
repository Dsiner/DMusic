package com.d.dmusic.view.popup;

import android.content.Context;

import com.d.dmusic.R;

/**
 * Created by D on 2017/5/2.
 */

public class MenuPopup extends AbstractPopup {

    public MenuPopup(Context context) {
        super(context, R.style.PopTopInDialog);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.dialog_more;
    }

    @Override
    protected void init() {

    }
}
