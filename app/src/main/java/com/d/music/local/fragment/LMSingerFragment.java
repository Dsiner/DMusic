package com.d.music.local.fragment;

import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.music.R;
import com.d.music.data.database.greendao.db.AppDB;
import com.d.music.event.eventbus.MusicModelEvent;
import com.d.music.local.adapter.SingerAdapter;
import com.d.music.local.model.SingerModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页-本地歌曲-歌手
 * Created by D on 2017/4/29.
 */
public class LMSingerFragment extends AbstractLMFragment<SingerModel> {

    @Override
    protected CommonAdapter<SingerModel> getAdapter() {
        return new SingerAdapter(mContext, new ArrayList<SingerModel>(), R.layout.module_local_adapter_singer);
    }

    @Override
    protected void initList() {
        super.initList();
        xrvList.setCanRefresh(false);
        xrvList.setCanLoadMore(false);
    }

    @Override
    protected void onLoad(int page) {
        mPresenter.getSinger();
    }

    @Override
    public void setSinger(List<SingerModel> models) {
        commonLoader.setData(models);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onEvent(MusicModelEvent event) {
        if (event == null || getActivity() == null || getActivity().isFinishing()
                || event.type != AppDB.LOCAL_ALL_MUSIC || mPresenter == null || !isLazyLoaded) {
            return;
        }
        mPresenter.getSinger();
    }
}