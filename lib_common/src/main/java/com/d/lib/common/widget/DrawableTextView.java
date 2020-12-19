package com.d.lib.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.d.lib.common.R;

/**
 * DrawableTextView
 * Created by D on 2019/9/26.
 */
@SuppressLint("AppCompatCustomView")
public class DrawableTextView extends TextView {

    private float[] mDrawableLeft = new float[2];
    private float[] mDrawableTop = new float[2];
    private float[] mDrawableRight = new float[2];
    private float[] mDrawableBottom = new float[2];
    private boolean mCenter;

    public DrawableTextView(Context context) {
        this(context, null);
    }

    public DrawableTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init(context);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.lib_pub_DrawableTextView);
        mCenter = typedArray.getBoolean(R.styleable.lib_pub_DrawableTextView_lib_pub_DrawableTextView_center, false);
        mDrawableLeft[0] = typedArray.getDimension(R.styleable.lib_pub_DrawableTextView_lib_pub_DrawableTextView_leftWidth, -1);
        mDrawableLeft[1] = typedArray.getDimension(R.styleable.lib_pub_DrawableTextView_lib_pub_DrawableTextView_leftHeight, -1);
        mDrawableTop[0] = typedArray.getDimension(R.styleable.lib_pub_DrawableTextView_lib_pub_DrawableTextView_topWidth, -1);
        mDrawableTop[1] = typedArray.getDimension(R.styleable.lib_pub_DrawableTextView_lib_pub_DrawableTextView_topHeight, -1);
        mDrawableRight[0] = typedArray.getDimension(R.styleable.lib_pub_DrawableTextView_lib_pub_DrawableTextView_rightWidth, -1);
        mDrawableRight[1] = typedArray.getDimension(R.styleable.lib_pub_DrawableTextView_lib_pub_DrawableTextView_rightHeight, -1);
        mDrawableBottom[0] = typedArray.getDimension(R.styleable.lib_pub_DrawableTextView_lib_pub_DrawableTextView_bottomWidth, -1);
        mDrawableBottom[1] = typedArray.getDimension(R.styleable.lib_pub_DrawableTextView_lib_pub_DrawableTextView_bottomHeight, -1);
        typedArray.recycle();
    }

    private void init(Context context) {
        Drawable[] drawables = getCompoundDrawables();
        setBounds(drawables[0], mDrawableLeft);
        setBounds(drawables[1], mDrawableTop);
        setBounds(drawables[2], mDrawableRight);
        setBounds(drawables[3], mDrawableBottom);
        setCompoundDrawables(drawables[0],
                drawables[1],
                drawables[2],
                drawables[3]);
    }

    private void setBounds(Drawable drawable, float[] dimensions) {
        if (drawable != null && dimensions[0] != -1 && dimensions[1] != -1) {
            drawable.setBounds(0, 0, (int) dimensions[0], (int) dimensions[1]);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCenter) {
            Drawable[] drawables = getCompoundDrawables();
            Drawable left = drawables[0];
            if (left != null) {
                int leftDrawableWidth = left.getBounds().right;
                int drawablePadding = getCompoundDrawablePadding();
                int textWidth = (int) getPaint().measureText(getText().toString().trim());
                int bodyWidth = leftDrawableWidth + drawablePadding + textWidth;
                canvas.save();
                canvas.translate((getWidth() - bodyWidth) / 2f, 0);
            }
        }
        super.onDraw(canvas);
    }
}
