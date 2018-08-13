package com.d.music.online.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.d.commenplayer.CommenPlayer;
import com.d.commenplayer.listener.IPlayerListener;
import com.d.commenplayer.listener.IRenderView;
import com.d.commenplayer.listener.OnNetListener;
import com.d.commenplayer.ui.ControlLayout;
import com.d.lib.common.module.loader.AbsFragment;
import com.d.lib.common.module.mvp.MvpView;
import com.d.lib.common.module.netstate.NetCompat;
import com.d.lib.common.module.netstate.NetState;
import com.d.lib.common.module.repeatclick.ClickUtil;
import com.d.lib.common.utils.Util;
import com.d.lib.common.view.DSLayout;
import com.d.lib.common.view.TitleLayout;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.MultiItemTypeSupport;
import com.d.music.R;
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

    @OnClick({R.id.iv_title_left})
    public void onClickListener(View v) {
        if (ClickUtil.isFastDoubleClick()) {
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
                    case 1:
                        return R.layout.module_online_adapter_mv_detail_info;
                    case 2:
                        return R.layout.module_online_adapter_mv_detail_similar;
                    default:
                        return R.layout.module_online_adapter_mv_detail_comment;
                }
            }

            @Override
            public int getItemViewType(int position, MVDetailModel model) {
                if (model instanceof MVInfoModel) {
                    return 1;
                } else if (model instanceof MVSimilarModel) {
                    return 2;
                }
                return 0;
            }
        });
    }

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            id = bundle.getLong("id", 0);
        }
        super.init();
        initPlayer();
    }

    private void initPlayer() {
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
        commonLoader.addToTop(info);

        MVInfoModel model = (MVInfoModel) info;
        String url = model.brs._$480;
        if (TextUtils.isEmpty(url)) {
            url = model.brs._$720;
        }
        if (TextUtils.isEmpty(url)) {
            url = model.brs._$1080;
        }
        if (TextUtils.isEmpty(url)) {
            url = model.brs._$240;
        }
        player.play(url);
    }

    @Override
    public void setSimilar(List<MVDetailModel> similar) {
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
        } else {
            lp.height = Util.dip2px(mContext, 210);
            player.setLayoutParams(lp);
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
