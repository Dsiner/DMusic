package com.d.lib.common.widget.trans;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Press to change Alpha
 * Created by D on 2018/1/12.
 */
@SuppressLint("AppCompatCustomView")
public class TransTextView extends TextView {
    public TransTextView(Context context) {
        super(context);
    }

    public TransTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TransTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (isPressed() || isFocused()) {
                setAlpha(0.4f);
            } else {
                setAlpha(1.0f);
            }
        }
    }
}
