package com.d.music.view.popup;

import android.content.Context;

import com.d.lib.common.view.popup.AbstractPopup;
import com.d.music.R;

/**
 * Created by D on 2017/5/2.
 */
public class MenuPopup extends AbstractPopup {

    public MenuPopup(Context context) {
        super(context, R.layout.dialog_more, R.style.PopTopInDialog);
    }

    @Override
    protected void init() {

    }
}
