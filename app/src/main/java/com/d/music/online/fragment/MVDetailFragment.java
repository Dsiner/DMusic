package com.d.music.online.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.d.lib.commenplayer.CommenPlayer;
import com.d.lib.commenplayer.listener.IPlayerListener;
import com.d.lib.commenplayer.listener.IRenderView;
import com.d.lib.commenplayer.listener.OnNetListener;
import com.d.lib.commenplayer.ui.ControlLayout;
import com.d.lib.common.component.loader.AbsFragment;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.component.netstate.NetCompat;
import com.d.lib.common.component.netstate.NetState;
import com.d.lib.common.component.repeatclick.ClickFast;
import com.d.lib.common.utils.Util;
import com.d.lib.common.view.DSLayout;
import com.d.lib.common.view.TitleLayout;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.MultiItemTypeSupport;
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

import butterknife.BindView;
import butterknife.OnClick;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * MVDetailFragment
 * Created by D on 2018/8/12.
 */
public class MVDetailFragment extends AbsFragment<MVDetailModel, MVDetailPresenter> implements IMVDetailView {
    @BindView(R.id.tl_title)
    TitleLayout tlTitle;
    @BindView(R.id.player)
    CommenPlayer player;

    private long id;
    private boolean ignoreNet;
    private int height916;

    @OnClick({R.id.iv_title_left})
    public void onClickListener(View v) {
        if (ClickFast.isFastDoubleClick()) {
            return;
        }
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
    protected void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            id = bundle.getLong(MVDetailActivity.ARG_ID, 0);
        }
        super.init();
        height916 = (int) (Util.getScreenSize(mActivity)[0] * 9f / 16f);
        initPlayer();
    }

    private void initPlayer() {
        ViewGroup.LayoutParams lp = player.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = height916;
        player.setLayoutParams(lp);

        player.setLive(false);
        player.setScaleType(IRenderView.AR_MATCH_PARENT);
        player.setOnNetListener(new OnNetListener() {
            @Override
            public void onIgnoreMobileNet() {
                ignoreNet = true;
            }
        }).setOnPlayerListener(new IPlayerListener() {
            @Override
            public void onLoading() {
                player.getControl().setState(ControlLayout.STATE_LOADING);
            }

            @Override
            public void onCompletion(IMediaPlayer mp) {
                player.getControl().setState(ControlLayout.STATE_COMPLETION);
            }

            @Override
            public void onPrepared(IMediaPlayer mp) {
                if (!ignoreNet && NetCompat.getStatus() == NetState.CONNECTED_MOBILE) {
                    player.pause();
                    player.getControl().setState(ControlLayout.STATE_MOBILE_NET);
                } else {
                    player.getControl().setState(ControlLayout.STATE_PREPARED);
                }
            }

            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                player.getControl().setState(ControlLayout.STATE_ERROR);
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
        xrvList.setCanRefresh(false);
        xrvList.setCanLoadMore(false);
        super.initList();
    }

    @Override
    protected void onLoad(int page) {
        dslDs.setState(DSLayout.GONE);
        if (page == 1) {
            mPresenter.getMvDetailInfo(id);
            mPresenter.getSimilarMV(id);
        }
        mPresenter.getMVComment(id, page);
    }

    @Override
    public void setInfo(MVDetailModel info) {
        dslDs.setState(DSLayout.GONE);
        xrvList.setVisibility(View.VISIBLE);
        commonLoader.addTop(info);
        player.play(MVInfoModel.getUrl((MVInfoModel) info));
    }

    @Override
    public void setSimilar(List<MVDetailModel> similar) {
        if (similar.size() <= 0) {
            return;
        }
        dslDs.setState(DSLayout.GONE);
        xrvList.setVisibility(View.VISIBLE);
        List<MVDetailModel> datas = commonLoader.getDatas();
        if (datas.size() > 0 && datas.get(0) != null && datas.get(0) instanceof MVInfoModel) {
            commonLoader.addData(1, similar);
        } else {
            commonLoader.addData(0, similar);
        }
    }

    @Override
    public void setData(List<MVDetailModel> datas) {
        dslDs.setState(DSLayout.GONE);
        xrvList.setVisibility(View.VISIBLE);
        commonLoader.addData(datas);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
    }

    @Override
    public void onPause() {
        if (player != null) {
            player.onPause();
        }
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ViewGroup.LayoutParams lp = player.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            player.setLayoutParams(lp);
            tlTitle.setVisibility(View.GONE);
        } else {
            lp.height = height916;
            player.setLayoutParams(lp);
            tlTitle.setVisibility(View.VISIBLE);
        }
        if (player != null) {
            player.onConfigurationChanged(newConfig);
        }
    }

    public boolean onBackPressed() {
        return player != null && player.onBackPress();
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.onDestroy();
        }
        super.onDestroy();
    }
}
