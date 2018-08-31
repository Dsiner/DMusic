package com.d.lib.xrv.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d.lib.xrv.R;
import com.nineoldandroids.animation.ValueAnimator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ArrowRefreshHeader extends LinearLayout implements BaseRefreshHeader {

    /**
     * 显示格式化日期模板
     */
    private final static String DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";

    private LinearLayout mContainer;
    private ImageView imgHeadArrow;
    private LoadingView ldvLoading;
    private TextView tvHeadTip;
    private int mState = ListState.STATE_NORMAL;

    private TextView tvHeadLastUpdateTime;

    private static final int ROTATE_ANIM_DURATION = 180;

    public int mMeasuredHeight;
    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;

    public ArrowRefreshHeader(Context context) {
        super(context);
        initView();
    }

    public ArrowRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        // 初始情况，设置下拉刷新view高度为0
        mContainer = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.lib_xrv_list_head, null);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        this.setLayoutParams(lp);
        this.setPadding(0, 0, 0, 0);

        addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, 0));
        setGravity(Gravity.BOTTOM);

        imgHeadArrow = (ImageView) findViewById(R.id.img_head_arrow);
        tvHeadTip = (TextView) findViewById(R.id.tv_head_tip);

        ldvLoading = (LoadingView) findViewById(R.id.ldv_loading);

        initAnim();

        tvHeadLastUpdateTime = (TextView) findViewById(R.id.tv_head_last_update_time);
        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mMeasuredHeight = getMeasuredHeight();
    }

    private void initAnim() {
        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);
    }

    public void setState(int state) {
        if (state == mState) {
            return;
        }

        if (state == ListState.STATE_REFRESHING) {
            // 显示进度
            imgHeadArrow.clearAnimation();
            imgHeadArrow.setVisibility(View.INVISIBLE);
            ldvLoading.setVisibility(View.VISIBLE);
            smoothScrollTo(mMeasuredHeight);
        } else if (state == ListState.STATE_DONE) {
            imgHeadArrow.setVisibility(View.INVISIBLE);
            ldvLoading.setVisibility(View.INVISIBLE);
        } else {
            // 显示箭头图片
            imgHeadArrow.setVisibility(View.VISIBLE);
            ldvLoading.setVisibility(View.INVISIBLE);
        }

        switch (state) {
            case ListState.STATE_NORMAL:
                if (mState == ListState.STATE_RELEASE_TO_REFRESH) {
                    imgHeadArrow.startAnimation(mRotateDownAnim);
                }
                if (mState == ListState.STATE_REFRESHING) {
                    imgHeadArrow.clearAnimation();
                }
                tvHeadTip.setText(getResources().getString(R.string.lib_xrv_list_header_hint_normal));
                break;
            case ListState.STATE_RELEASE_TO_REFRESH:
                if (mState != ListState.STATE_RELEASE_TO_REFRESH) {
                    imgHeadArrow.clearAnimation();
                    imgHeadArrow.startAnimation(mRotateUpAnim);
                    tvHeadTip.setText(getResources().getString(R.string.lib_xrv_list_header_hint_release));
                }
                break;
            case ListState.STATE_REFRESHING:
                tvHeadTip.setText(getResources().getString(R.string.lib_xrv_list_refreshing));
                break;
            case ListState.STATE_DONE:
                tvHeadTip.setText(getResources().getString(R.string.lib_xrv_list_refresh_done));
                break;
            default:
        }
        mState = state;
    }

    public int getState() {
        return mState;
    }

    @Override
    public void refreshComplete() {
        updateTime();
        setState(ListState.STATE_DONE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                reset();
            }
        }, 100); // 回弹时间
    }

    public void setVisibleHeight(int height) {
        if (height < 0) {
            height = 0;
        }
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    public int getVisibleHeight() {
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        return lp.height;
    }

    @Override
    public void onMove(float delta) {
        if (getVisibleHeight() > 0 || delta > 0) {
            setVisibleHeight((int) delta + getVisibleHeight());
            if (mState <= ListState.STATE_RELEASE_TO_REFRESH) {
                // 未处于刷新状态，更新箭头
                if (getVisibleHeight() > mMeasuredHeight) {
                    setState(ListState.STATE_RELEASE_TO_REFRESH);
                } else {
                    setState(ListState.STATE_NORMAL);
                }
            }
        }
    }

    @Override
    public boolean releaseAction() {
        boolean isOnRefresh = false;
        int height = getVisibleHeight();
        if (height == 0) {
            // not visible.
            isOnRefresh = false;
        }

        if (getVisibleHeight() > mMeasuredHeight && mState < ListState.STATE_REFRESHING) {
            setState(ListState.STATE_REFRESHING);
            isOnRefresh = true;
        }
        // Refreshing and header isn't shown fully. do nothing.
//        if (mState == ListState.STATE_REFRESHING && height <= mMeasuredHeight) {
//            //return;
//        }
        if (mState != ListState.STATE_REFRESHING) {
            smoothScrollTo(0);
        }

        if (mState == ListState.STATE_REFRESHING) {
            int destHeight = mMeasuredHeight;
            smoothScrollTo(destHeight);
        }

        return isOnRefresh;
    }

    public void reset() {
        smoothScrollTo(0);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                setState(ListState.STATE_NORMAL);
            }
        }, 500);
    }

    private void smoothScrollTo(int destHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);
        animator.setDuration(300).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setVisibleHeight((int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    /**
     * 刷新时间
     */
    public void updateTime() {
        if (tvHeadLastUpdateTime != null) {
            tvHeadLastUpdateTime.setText(getResources().getString(R.string.lib_xrv_list_header_last_time)
                    + new SimpleDateFormat(DATE_FORMAT_STR, Locale.CHINA)
                    .format(new Date()));
        }
    }
}