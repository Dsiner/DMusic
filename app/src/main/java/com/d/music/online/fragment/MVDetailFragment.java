package com.d.music.online.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.d.lib.commenplayer.CommenPlayer;
import com.d.lib.commenplayer.listener.IPlayerListener;
import com.d.lib.commenplayer.listener.IRenderView;
import com.d.lib.commenplayer.listener.OnNetworkListener;
import com.d.lib.commenplayer.widget.ControlLayout;
import com.d.lib.common.component.loader.v4.BaseLoaderFragment;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.component.network.NetworkCompat;
import com.d.lib.common.component.quickclick.QuickClick;
import com.d.lib.common.util.ScreenUtils;
import com.d.lib.common.util.ViewHelper;
import com.d.lib.common.widget.DSLayout;
import com.d.lib.common.widget.TitleLayout;
import com.d.lib.pulllayout.Pullable;
import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.lib.pulllayout.rv.adapter.MultiItemTypeSupport;
import com.d.music.R;
import com.d.music.online.activity.MVDetailActivity;
import com.d.music.online.adapter.MVDetailAdapter;
import com.d.music.online.model.MVDetailModel;
import com.d.music.online.model.MVInfoModel;
import com.d.music.online.model.MVSimilarModel;
import com.d.music.online.presenter.MVDetailPresenter;
import com.d.music.online.view.IMVDetailView;

import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * MVDetailFragment
 * Created by D on 2018/8/12.
 */
public class MVDetailFragment extends BaseLoaderFragment<MVDetailModel, MVDetailPresenter>
        implements IMVDetailView {
    TitleLayout tl_title;
    CommenPlayer mPlayer;

    private long mId;
    private boolean mIgnoreMobileData;
    private int mHeight916;

    @Override
    public void onClick(View v) {
        if (QuickClick.isQuickClick()) {
            return;
        }
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_title_left:
                getActivity().finish();
                break;
        }
    }

    @Override
    public MVDetailPresenter getPresenter() {
        return new MVDetailPresenter(getActivity().getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.module_online_activity_mv_detail;
    }

    @Override
    protected CommonAdapter<MVDetailModel> getAdapter() {
        return new MVDetailAdapter(mContext, new ArrayList<MVDetailModel>(), new MultiItemTypeSupport<MVDetailModel>() {
            @Override
            public int getLayoutId(int viewType) {
                switch (viewType) {
                    case MVDetailModel.TYPE_INFO:
                        return R.layout.module_online_adapter_mv_detail_info;

                    case MVDetailModel.TYPE_SIMILAR_HEAD:
                        return R.layout.module_online_adapter_mv_detail_similar_head;

                    case MVDetailModel.TYPE_SIMILAR:
                        return R.layout.module_online_adapter_mv_detail_similar;

                    case MVDetailModel.TYPE_COMMENT_HEAD:
                        return R.layout.module_online_adapter_mv_detail_comment_head;

                    default:
                        return R.layout.module_online_adapter_mv_detail_comment;
                }
            }

            @Override
            public int getItemViewType(int position, MVDetailModel model) {
                if (model instanceof MVInfoModel) {
                    return MVDetailModel.TYPE_INFO;
                } else if (model instanceof MVSimilarModel) {
                    return MVDetailModel.TYPE_SIMILAR;
                }
                return model.view_type;
            }
        });
    }

    @Override
    protected void bindView(View rootView) {
        super.bindView(rootView);
        tl_title = rootView.findViewById(R.id.tl_title);
        mPlayer = rootView.findViewById(R.id.player);

        ViewHelper.setOnClickListener(rootView, this, R.id.iv_title_left);
    }

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mId = bundle.getLong(MVDetailActivity.EXTRA_ID, 0);
        }
        super.init();
        mHeight916 = (int) (ScreenUtils.getScreenSize(mActivity)[0] * 9f / 16f);
        initPlayer();
    }

    private void initPlayer() {
        ViewGroup.LayoutParams lp = mPlayer.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = mHeight916;
        mPlayer.setLayoutParams(lp);

        mPlayer.setLive(false);
        mPlayer.setScaleType(IRenderView.AR_MATCH_PARENT);
        mPlayer.setOnNetListener(new OnNetworkListener() {
            @Override
            public void onIgnoreMobileData() {
                mIgnoreMobileData = true;
            }
        }).setOnPlayerListener(new IPlayerListener() {
            @Override
            public void onLoading() {
                mPlayer.getControl().setState(ControlLayout.STATE_LOADING);
            }

            @Override
            public void onCompletion(IMediaPlayer mp) {
                mPlayer.getControl().setState(ControlLayout.STATE_COMPLETION);
            }

            @Override
            public void onPrepared(IMediaPlayer mp) {
                if (!mIgnoreMobileData
                        && NetworkCompat.isMobileDataType(NetworkCompat.getType())) {
                    mPlayer.pause();
                    mPlayer.getControl().setState(ControlLayout.STATE_MOBILE_NET);
                } else {
                    mPlayer.getControl().setState(ControlLayout.STATE_PREPARED);
                }
            }

            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                mPlayer.getControl().setState(ControlLayout.STATE_ERROR);
                return false;
            }

            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                return false;
            }

            @Override
            public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {

            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void initList() {
        ((Pullable) mPullList).setCanPullDown(false);
        ((Pullable) mPullList).setCanPullUp(false);
        super.initList();
    }

    @Override
    protected void onLoad(int page) {
        mDslDs.setState(DSLayout.GONE);
        if (page == 1) {
            mPresenter.getMvDetailInfo(mId);
            mPresenter.getSimilarMV(mId);
        }
        mPresenter.getMVComment(mId, page);
    }

    @Override
    public void setInfo(MVDetailModel info) {
        mDslDs.setState(DSLayout.GONE);
        mPullList.setVisibility(View.VISIBLE);
        mCommonLoader.add(0, info);
        mPlayer.play(MVInfoModel.getUrl((MVInfoModel) info));
    }

    @Override
    public void setSimilar(List<MVDetailModel> similar) {
        if (similar.size() <= 0) {
            return;
        }
        mDslDs.setState(DSLayout.GONE);
        mPullList.setVisibility(View.VISIBLE);
        List<MVDetailModel> datas = mCommonLoader.getDatas();
        if (datas.size() > 0 && datas.get(0) != null && datas.get(0) instanceof MVInfoModel) {
            mCommonLoader.addAll(1, similar);
        } else {
            mCommonLoader.addAll(0, similar);
        }
    }

    @Override
    public void loadSuccess(List<MVDetailModel> datas) {
        mDslDs.setState(DSLayout.GONE);
        mPullList.setVisibility(View.VISIBLE);
        mCommonLoader.addAll(datas);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPlayer != null) {
            mPlayer.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mPlayer != null) {
            mPlayer.onPause();
        }
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ViewGroup.LayoutParams lp = mPlayer.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mPlayer.setLayoutParams(lp);
            tl_title.setVisibility(View.GONE);
        } else {
            lp.height = mHeight916;
            mPlayer.setLayoutParams(lp);
            tl_title.setVisibility(View.VISIBLE);
        }
        if (mPlayer != null) {
            mPlayer.onConfigurationChanged(newConfig);
        }
    }

    public boolean onBackPressed() {
        return mPlayer != null && mPlayer.onBackPress();
    }

    @Override
    public void onDestroy() {
        if (mPlayer != null) {
            mPlayer.onDestroy();
        }
        super.onDestroy();
    }
}
