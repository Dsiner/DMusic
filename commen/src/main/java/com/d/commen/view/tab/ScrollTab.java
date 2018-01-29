package com.d.commen.view.tab;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.d.commen.R;
import com.d.commen.utils.Util;
import com.d.commen.utils.log.ULog;

import java.util.ArrayList;
import java.util.List;

import cn.feng.skin.manager.loader.SkinManager;

/**
 * ScrollTab
 * Created by D on 2017/8/25.
 */
public class ScrollTab extends HorizontalScrollView implements View.OnClickListener, ViewPager.OnPageChangeListener {
    /**
     * TAB类型
     */
    private final int TYPE_VIEW = 0;
    private final int TYPE_VIEW_GROUP = 1;

    /**
     * 指示器类型
     */
    private final int TYPE_INDICATOR_TREND = 0;
    private final int TYPE_INDICATOR_TRANSLATION = 1;
    private final int TYPE_INDICATOR_NONE = 2;

    private int width;
    private int height;

    private Context context;
    private RectF rectF;
    private Paint paint;

    private int type;
    private boolean isAvag;
    private float padding;//item内部左右预留间距
    private String strTitles;
    private int indicatorType;
    private int indicatorColor;
    private float indicatorWidth;
    private float indicatorWeight;
    private float indicatorRadius;
    private float indicatorPadding;

    private ArrayList<TabItem> items;
    private ArrayList<View> tabs;
    private int count;
    private int position = 0;
    private float positionOffset;
    private boolean isFirst = true;
    private ViewPager viewPager;
    private OnTabListener listener;

    public ScrollTab(Context context) {
        this(context, null);
    }

    public ScrollTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTypedArray(context, attrs);
        init(context);
    }

    private void initTypedArray(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.lib_pub_ScrollTab);
        type = typedArray.getInt(R.styleable.lib_pub_ScrollTab_lib_pub_stab_type, TYPE_VIEW);
        isAvag = typedArray.getBoolean(R.styleable.lib_pub_ScrollTab_lib_pub_stab_avag, false);
        padding = typedArray.getDimension(R.styleable.lib_pub_ScrollTab_lib_pub_stab_padding, Util.dip2px(context, 12));
        strTitles = typedArray.getString(R.styleable.lib_pub_ScrollTab_lib_pub_stab_titles);
        indicatorType = typedArray.getInt(R.styleable.lib_pub_ScrollTab_lib_pub_stab_indicator_type, TYPE_INDICATOR_TREND);
        indicatorWidth = typedArray.getDimension(R.styleable.lib_pub_ScrollTab_lib_pub_stab_indicator_width, 0);
        indicatorWeight = typedArray.getDimension(R.styleable.lib_pub_ScrollTab_lib_pub_stab_indicator_weight, Util.dip2px(context, 1));
        indicatorRadius = typedArray.getDimension(R.styleable.lib_pub_ScrollTab_lib_pub_stab_indicator_radius, Util.dip2px(context, 0.5f));
        indicatorPadding = typedArray.getDimension(R.styleable.lib_pub_ScrollTab_lib_pub_stab_indicator_padding, Util.dip2px(context, 5));
        typedArray.recycle();
    }

    private void init(Context context) {
        this.context = context;
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        setOverScrollMode(OVER_SCROLL_NEVER);
        setFillViewport(isAvag);
        indicatorColor = SkinManager.getInstance().getColor(R.color.lib_pub_color_main);
        rectF = new RectF();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(indicatorColor);

        tabs = new ArrayList<>();
        items = new ArrayList<>();
        if (!TextUtils.isEmpty(strTitles)) {
            String[] strs = strTitles.split(";");
            for (String t : strs) {
                items.add(new TabItem(t, ""));
            }
        }
    }

    /**
     * 设置titles
     */
    public void setTitles(List<String> ts) {
        if (this.items != null && ts != null) {
            this.items.clear();
            for (String t : ts) {
                this.items.add(new TabItem(t, ""));
            }
            if (!isFirst) {
                resetTab();
                invalidate();
            }
        }
    }

    private void resetTab() {
        if (items == null || items.size() <= 0 || width <= 0) {
            return;
        }
        isFirst = false;
        count = items.size();
        tabs.clear();
        removeAllViews();
        LinearLayout parent = new LinearLayout(context);
        LayoutParams lp = new LayoutParams(isAvag ? LayoutParams.MATCH_PARENT : LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        parent.setOrientation(LinearLayout.HORIZONTAL);
        parent.setLayoutParams(lp);
        for (int i = 0; i < count; i++) {
            View child = getTabView(i);
            parent.addView(child);
            tabs.add(child);
        }
        addView(parent);
    }

    private View getTabView(int i) {
        View child;
        if (type == TYPE_VIEW) {
            child = new TabTextView(context);
        } else {
            child = new TabViewGroup(context);
        }
        ((TabView) child).setText(items.get(i).title);
        ((TabView) child).setNumber(items.get(i).text, TextUtils.isEmpty(items.get(i).text) ? GONE : VISIBLE);
        if (!isAvag) {
            ((TabView) child).setPadding((int) padding);
        }
        ((TabView) child).notifyData(i == position);
        child.setLayoutParams(new LinearLayout.LayoutParams(isAvag ? width / (count > 0 ? count : 1) : ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        child.setTag(i);
        child.setOnClickListener(this);
        return child;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode() || count <= 0 || position < 0 || position > count - 1) {
            return;
        }
        if (indicatorType == TYPE_INDICATOR_TREND) {
            float left = tabs.get(position).getLeft() + indicatorPadding;
            float right = tabs.get(position).getRight() - indicatorPadding;
            if (position < count - 1) {
                float nextLeft = tabs.get(position + 1).getLeft() + indicatorPadding;
                float nextRight = tabs.get(position + 1).getRight() - indicatorPadding;
                if (positionOffset < 0.5) {
                    right = right + (nextRight - right) * positionOffset * 2;
                } else {
                    left = left + (nextLeft - left) * (positionOffset - 0.5f) * 2;
                    right = nextRight;
                }
            }
            rectF.set(left, height - indicatorWeight, right, height);
        } else if (indicatorType == TYPE_INDICATOR_TRANSLATION) {
            float left = tabs.get(position).getLeft();
            float right = tabs.get(position).getRight();
            float middle = left + (right - left) / 2;
            if (position < count - 1) {
                float nextLeft = tabs.get(position + 1).getLeft();
                float nextRight = tabs.get(position + 1).getRight();
                float nextMiddle = nextLeft + (nextRight - nextLeft) / 2;
                middle = middle + (nextMiddle - middle) * positionOffset;
            }
            float indicatorWidthAdj = indicatorWidth == 0 ? (right - left) : indicatorWidth;
            left = middle - indicatorWidthAdj / 2;
            right = middle + indicatorWidthAdj / 2;
            rectF.set(left, height - indicatorWeight, right, height);
        } else {
            float left = tabs.get(position).getLeft();
            float right = tabs.get(position).getRight();
            float middle = left + (right - left) / 2;
            float indicatorWidthAdj = indicatorWidth == 0 ? (right - left) : indicatorWidth;
            left = middle - indicatorWidthAdj / 2;
            right = middle + indicatorWidthAdj / 2;
            rectF.set(left, height - indicatorWeight, right, height);
        }
        canvas.drawRoundRect(rectF, indicatorRadius, indicatorRadius, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        if (isFirst) {
            resetTab();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onClick(View v) {
        int index = (int) v.getTag();
        if (viewPager == null) {
            position = index;
            positionOffset = 0;
            onChange(index);
            adjustScrollY(index);
        }
        if (listener != null) {
            listener.onChange(index, v);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (hasWindowFocus) {
            indicatorColor = SkinManager.getInstance().getColor(R.color.lib_pub_color_main);
            if (paint != null) {
                paint.setColor(indicatorColor);
            }
        }
        super.onWindowFocusChanged(hasWindowFocus);
    }

    private void onChange(int position) {
        for (int i = 0; i < count; i++) {
            TabView view = (TabView) tabs.get(i);
            view.notifyData(i == position);
        }
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        viewPager.addOnPageChangeListener(this);
    }

    /**
     * 设置红点
     */
    public void setNumber(int position, String text, int visibility) {
        if (position < 0 || position > items.size() - 1) {
            return;
        }
        items.get(position).text = text;
        if (position < 0 || position > count - 1) {
            return;
        }
        TabView view = (TabView) tabs.get(position);
        view.setNumber(text, visibility);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        ULog.d("dsiner_onPageScrolled: position: " + position + " Offset: " + positionOffset);
        if (indicatorType != TYPE_INDICATOR_NONE) {
            this.position = position;
            this.positionOffset = positionOffset;
            invalidate();
        }
    }

    @Override
    public void onPageSelected(int position) {
        ULog.d("dsiner_onPageSelected: position: " + position + " Offset: " + positionOffset);
        onChange(position);
        adjustScrollY(position);
        if (indicatorType == TYPE_INDICATOR_NONE) {
            this.position = position;
            invalidate();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        ULog.d("dsiner_onPageScrollStateChanged: state: " + state);
    }

    private void adjustScrollY(int position) {
        if (isAvag) {
            return;
        }
        View v = tabs.get(position);
        int dr = v.getRight() - (width + getScrollX());
        int dl = getScrollX() - v.getLeft();
        if (dr > 0) {
            smoothScrollBy(dr, 0);
        } else if (dl > 0) {
            smoothScrollBy(-dl, 0);
        }
    }

    public interface OnTabListener {
        void onChange(int position, View v);
    }

    public void setOnTabListener(OnTabListener l) {
        this.listener = l;
    }
}
