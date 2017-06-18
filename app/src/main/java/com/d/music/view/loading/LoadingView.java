package com.d.music.view.loading;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.d.music.R;

import cn.feng.skin.manager.loader.SkinManager;

/**
 * Loading
 * Created by Administrator on 2016/8/27.
 */
public class LoadingView extends View {
    private float width;
    private float height;
    private Paint paint;
    private int count = 12;
    private RectF tempRct;
    private int minAlpha;
    private float rectHeight;
    private float rectWidth;
    private float radias;
    private int j;
    private Handler handler;
    private Runnable runnable;
    private long daration;
    private float widthRate;
    private float heightRate;
    private boolean isFirst;
    private int type = 0;
    private int color;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        isFirst = true;
        color = SkinManager.getInstance().getColor(R.color.color_main);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        minAlpha = 50;
        daration = 1000;
        widthRate = 1f / 3;
        heightRate = 1f / 2;
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                postInvalidate();
                handler.postDelayed(runnable, daration / count);
            }
        };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (width == 0 || height == 0 || tempRct == null) {
            return;
        }
        canvas.translate(width / 2, height / 2);
        j++;
        j %= count;
        int alpha;
        for (int i = 0; i < count; i++) {
            canvas.rotate(360f / count);
            alpha = (i - j + count) % count;
            alpha = (int) (((alpha) * (255f - minAlpha) / count + minAlpha));
            paint.setAlpha(alpha);
            switch (type) {
                case 0:
                    /**菊花旋转**/
                    canvas.drawRoundRect(tempRct, rectWidth / 2, rectWidth / 2, paint);
                    break;
                case 1:
                    /**圆点旋转**/
                    canvas.drawCircle((tempRct.left + tempRct.right) / 2, (tempRct.top + tempRct.bottom) / 2, rectWidth * 2 / 3, paint);
                    break;
            }
        }
        if (isFirst) {
            isFirst = false;
            handler.postDelayed(runnable, daration / count);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        refreshField();
    }

    private void refreshField() {
        float h = width > height ? height : width;
        rectHeight = h * heightRate / 2;
        rectWidth = rectHeight * widthRate;
        radias = h * (1 - heightRate / 2) / 2;
        tempRct = new RectF(-rectWidth / 2, -(radias + rectHeight / 2), rectWidth / 2, -(radias - rectHeight / 2));
    }

    @Override
    public void setVisibility(int visibility) {
        switch (visibility) {
            case VISIBLE:
                restart();
                break;
            case GONE:
                stop();
                break;
            case INVISIBLE:
                stop();
                break;
        }
        super.setVisibility(visibility);
    }

    @Override
    protected void onAttachedToWindow() {
        if (!isFirst) {
            restart();
        }
        color = SkinManager.getInstance().getColor(R.color.color_main);
        if (paint != null) {
            paint.setColor(color);
        }
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        stop();
        super.onDetachedFromWindow();
    }

    /**
     * 重新开始
     */
    public void restart() {
        stop();
        handler.post(runnable);
    }

    /**
     * 停止
     */
    public void stop() {
        isFirst = false;
        handler.removeCallbacks(runnable);
    }
}
