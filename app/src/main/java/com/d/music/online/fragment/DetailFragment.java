package com.d.music.online.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.d.lib.common.component.loader.v4.BaseLoaderFragment;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.component.quickclick.QuickClick;
import com.d.lib.common.util.ViewHelper;
import com.d.lib.common.widget.TitleLayout;
import com.d.lib.common.widget.dialog.AlertDialogFactory;
import com.d.lib.pulllayout.Pullable;
import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.lib.pulllayout.util.RefreshableCompat;
import com.d.music.R;
import com.d.music.component.media.controler.MediaControl;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.online.activity.DetailActivity;
import com.d.music.online.adapter.DetailAdapter;
import com.d.music.online.model.BillSongsRespModel;
import com.d.music.online.model.RadioSongsRespModel;
import com.d.music.online.presenter.MusicPresenter;
import com.d.music.online.view.IMusicView;
import com.d.music.transfer.manager.TransferManager;
import com.d.music.widget.SongHeaderView;

import java.util.ArrayList;
import java.util.List;

/**
 * DetailFragment
 * Created by D on 2018/8/12.
 */
public class DetailFragment extends BaseLoaderFragment<MusicModel, MusicPresenter>
        implements IMusicView, View.OnClickListener {
    TitleLayout tl_title;
    ImageView iv_cover;

    private int mType;
    private String mChannel, mTitle, mCover;
    private SongHeaderView songHeaderView;

    @Override
    public void onClick(View v) {
        if (QuickClick.isQuickClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_title_left:
                getActivity().finish();
                break;
        }
    }

    @Override
    public MusicPresenter getPresenter() {
        return new MusicPresenter(getActivity().getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.module_online_fragment_detail;
    }

    @Override
    protected CommonAdapter<MusicModel> getAdapter() {
        return new DetailAdapter(mContext, new ArrayList<MusicModel>(), R.layout.module_online_adapter_music);
    }

    @Override
    protected void bindView(View rootView) {
        super.bindView(rootView);
        tl_title = rootView.findViewById(R.id.tl_title);
        iv_cover = rootView.findViewById(R.id.iv_cover);

        ViewHelper.setOnClickListener(rootView, this, R.id.iv_title_left);
    }

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mType = bundle.getInt(DetailActivity.EXTRA_TYPE, DetailActivity.TYPE_BILL);
            mChannel = bundle.getString(DetailActivity.EXTRA_CHANNEL);
            mTitle = bundle.getString(DetailActivity.EXTRA_TITLE);
            mCover = bundle.getString(DetailActivity.EXTRA_COVER);
        }
        tl_title.setText(R.id.tv_title_title, !TextUtils.isEmpty(mTitle) ? mTitle
                : getResources().getString(R.string.module_common_music));
        super.init();
    }

    @Override
    protected void initList() {
        initHead();
        ((Pullable) mPullList).setCanPullDown(false);
        if (mType == DetailActivity.TYPE_RADIO) {
            ((Pullable) mPullList).setCanPullUp(false);
        }
        RefreshableCompat.addHeaderView(mPullList, songHeaderView);
        super.initList();
    }

    private void initHead() {
        songHeaderView = new SongHeaderView(mContext);
        songHeaderView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.lib_pub_color_bg_sub));
        songHeaderView.setVisibility(R.id.flyt_header_song_download, View.VISIBLE);
        songHeaderView.setVisibility(R.id.flyt_header_song_handler, View.GONE);
        songHeaderView.setVisibility(View.GONE);
        songHeaderView.setOnHeaderListener(new SongHeaderView.OnHeaderListener() {
            @Override
            public void onPlayAll() {
                MediaControl.getInstance(mContext).init(mCommonLoader.getDatas(), 0, true);
            }

            @Override
            public void onHandle() {

            }
        });
        ViewHelper.setOnClickListener(songHeaderView,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final List<MusicModel> datas = mCommonLoader.getDatas();
                        AlertDialogFactory.createFactory(mContext)
                                .getAlertDialog(mContext.getResources().getString(R.string.module_common_tips),
                                        mContext.getResources().getString(R.string.module_common_traffic_prompt),
                                        mContext.getResources().getString(R.string.lib_pub_ok),
                                        mContext.getResources().getString(R.string.lib_pub_cancel),
                                        new AlertDialogFactory.OnClickListener() {
                                            @Override
                                            public void onClick(Dialog dlg, View v) {
                                                TransferManager.getInstance().optSong().add(datas);
                                            }
                                        }, null);
                    }
                }, R.id.flyt_header_song_download);
    }

    @Override
    protected void onLoad(int page) {
        if (mType == DetailActivity.TYPE_ARTIST) {
            mPresenter.getArtistSongs(mChannel, page);
        } else if (mType == DetailActivity.TYPE_BILL) {
            mPresenter.getBillSongs(mChannel, page);
        } else if (mType == DetailActivity.TYPE_RADIO) {
            mPresenter.getRadioSongs(mChannel, page);
        }
    }

    @Override
    public void setInfo(BillSongsRespModel info) {
        if (!TextUtils.isEmpty(mCover)) {
            setCover(mCover);
            return;
        }
        if (info == null || info.billboard == null || info.billboard.pic_s260 == null) {
            return;
        }
        setCover(info.billboard.pic_s260);
    }

    @Override
    public void setInfo(RadioSongsRespModel info) {
        if (!TextUtils.isEmpty(mCover)) {
            setCover(mCover);
            return;
        }
        if (info == null || info.result == null || info.result.songlist == null
                || info.result.songlist.size() <= 0
                || info.result.songlist.get(0) == null) {
            return;
        }
        setCover(info.result.songlist.get(0).thumb);
    }

    @Override
    public void loadSuccess(List<MusicModel> datas) {
        if (mType == DetailActivity.TYPE_ARTIST) {
            setCover(mCover);
        }
        super.loadSuccess(datas);
        notifyDataCountChanged(mCommonLoader.getDatas().size());
    }

    private void setCover(String url) {
        Glide.with(mContext)
                .load(url)
                .apply(new RequestOptions().dontAnimate())
                .into(iv_cover);
    }

    private void notifyDataCountChanged(int count) {
        songHeaderView.setSongCount(count);
        songHeaderView.setVisibility(count <= 0 ? View.GONE : View.VISIBLE);
    }
}
