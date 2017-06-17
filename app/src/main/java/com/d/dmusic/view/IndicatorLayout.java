package com.d.dmusic.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d.dmusic.R;
import com.d.dmusic.module.global.Cst;
import com.d.dmusic.utils.Util;

import java.util.List;

import cn.feng.skin.manager.loader.SkinManager;

/**
 * IndicatorLayout
 * Created by D on 2017/4/28.
 */
public class IndicatorLayout extends LinearLayout {
    private static final float RADIO_TRIANGLE_WIDTH = 1 / 6F;
    private static final int COUNT_DEFAULT_TAB = 4;
    private final int DIMENSION_TRIANGLE_WIDTH_MAX = Cst.SCREEN_WIDTH / 3;//底边的最大宽度

    private Context context;
    private float width;
    private float height;
    private Paint paint;
    private Path path;
    private int triangleWidth;
    private int triangleHeight;

    private int mInitTranslationX;
    private int mTranslationX;
    private int count;
    private int color;
    private int textColor;

    private List<String> titles;
    private ViewPager viewPage;
    public PageOnchangeListener listener;

    public IndicatorLayout(Context context) {
        this(context, null);
    }

    public IndicatorLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IndicatorLayout);
        count = a.getInt(R.styleable.IndicatorLayout_tab_count, COUNT_DEFAULT_TAB);//获取可见Tab的数量
//        color = a.getInt(R.styleable.IndicatorLayout_indicator_color, R.color.color_main);
        color = SkinManager.getInstance().getColor(R.color.color_main);
        textColor = a.getInt(R.styleable.IndicatorLayout_text_color, R.color.color_gray);
        if (count < 0) {
            count = COUNT_DEFAULT_TAB;
        }
        a.recycle();
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Style.FILL);
        paint.setPathEffect(new CornerPathEffect(3));
    }

    @Override
    protected void onAttachedToWindow() {
        color = SkinManager.getInstance().getColor(R.color.color_main);
        if (paint != null) {
            paint.setColor(color);
        }
        super.onAttachedToWindow();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mInitTranslationX + mTranslationX, getHeight());
        canvas.drawPath(path, paint);
        canvas.restore();
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        triangleWidth = w / count;
        triangleWidth = Math.min(triangleWidth, DIMENSION_TRIANGLE_WIDTH_MAX);
        mInitTranslationX = w / count / 2 - triangleWidth / 2;
        initTriangle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int count = getChildCount();
        if (count == 0) {
            return;
        }
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.weight = 0;
            lp.width = Cst.SCREEN_WIDTH / this.count;
            view.setLayoutParams(lp);
        }
        setClickListener();
    }

    /**
     * 初始化
     */
    private void initTriangle() {
        triangleHeight = Util.dip2px(getContext(), 2f);
        path = new Path();
        path.moveTo(0, 0);
        path.lineTo(triangleWidth, 0);
        path.lineTo(triangleWidth, -triangleHeight);
        path.lineTo(0, -triangleHeight);
        path.close();
    }

    /**
     * 指示器跟随手指进行滚动
     */
    public void scroll(int position, float offset) {
        int tabWidth = getWidth() / count;
        mTranslationX = (int) (tabWidth * (offset + position));
        // 容器移动，在tab处于移动至最后一个时
        if (position >= (count - 2) && offset > 0
                && getChildCount() > count) {
            if (count != 1) {
                Log.e("TAG", ((position - (count - 2)) * tabWidth + (int) (tabWidth * offset)) + "");
                this.scrollTo((position - (count - 2)) * tabWidth + (int) (tabWidth * offset), 0);
            } else {
                this.scrollTo(position * tabWidth + (int) (tabWidth * offset), 0);
            }
        }
        invalidate();
    }

    public void setTitles(List<String> titles) {
        if (titles != null && titles.size() > 0) {
            this.removeAllViews();
            this.titles = titles;
            for (String title : this.titles) {
                addView(generateTextView(title));
            }
            setClickListener();
        }
    }

    /**
     * 设置可见的Tab数量
     */
    public void setVisibleTabCount(int count) {
        this.count = count;
    }

    /**
     * 根据title创建Tab
     */
    private View generateTextView(String title) {
        TextView tv = new TextView(getContext());
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.width = Cst.SCREEN_WIDTH / count;
        tv.setText(title);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setTextColor(textColor);
        tv.setLayoutParams(lp);
        return tv;
    }

    /**
     * 设置关联的ViewPager
     */
    public void setViewPager(ViewPager viewPager, int pos) {
        viewPage = viewPager;
        viewPage.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (listener != null) {
                    listener.onPageSelected(position);
                }
                highLightTextView(position);
                // 极端情况的Bug修复
                if (position <= (count - 2))
                    scrollTo(0, 0);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                scroll(position, positionOffset);
                if (listener != null) {
                    listener.onPageScrolled(position, positionOffset,
                            positionOffsetPixels);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (listener != null) {
                    listener.onPageScrollStateChanged(state);
                }
            }
        });
        viewPage.setCurrentItem(pos);
        highLightTextView(pos);
    }

    /**
     * 重置TAB文本颜色
     */
    private void resetTextColor() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(textColor);
            }
        }
    }

    /**
     * 高亮某个Tab的文本
     */
    private void highLightTextView(int pos) {
        resetTextColor();
        View view = getChildAt(pos);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(color);
        }
    }

    /**
     * 设置Tab的点击事件
     */
    private void setClickListener() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPage.setCurrentItem(j);
                }
            });
        }
    }

    public interface PageOnchangeListener {
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);
    }

    public void setOnPageChangeListener(PageOnchangeListener listener) {
        this.listener = listener;
    }
}
