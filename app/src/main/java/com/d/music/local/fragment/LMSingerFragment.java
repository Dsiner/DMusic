package com.d.music.local.fragment;

import com.d.lib.pulllayout.Pullable;
import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.music.R;
import com.d.music.data.database.greendao.db.AppDatabase;
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
        ((Pullable) mPullList).setCanPullDown(false);
        ((Pullable) mPullList).setCanPullUp(false);
    }

    @Override
    protected void onLoad(int page) {
        mPresenter.getSinger();
    }

    @Override
    public void setSinger(List<SingerModel> models) {
        mCommonLoader.loadSuccess(models);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onEvent(MusicModelEvent event) {
        if (event == null || getActivity() == null || getActivity().isFinishing()
                || event.type != AppDatabase.LOCAL_ALL_MUSIC || mPresenter == null || !mIsLazyLoaded) {
            return;
        }
        mPresenter.getSinger();
    }
}