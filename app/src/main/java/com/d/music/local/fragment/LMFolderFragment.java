package com.d.music.local.fragment;

import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.music.R;
import com.d.music.local.adapter.FolderAdapter;
import com.d.music.model.FolderModel;
import com.d.music.module.events.MusicModelEvent;
import com.d.music.module.greendao.db.MusicDB;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页-本地歌曲-文件夹
 * Created by D on 2017/4/29.
 */
public class LMFolderFragment extends AbstractLMFragment<FolderModel> {

    @Override
    protected CommonAdapter<FolderModel> getAdapter() {
        return new FolderAdapter(mContext, new ArrayList<FolderModel>(), R.layout.adapter_folder);
    }

    @Override
    protected void initList() {
        super.initList();
        xrvList.setCanRefresh(false);
        xrvList.setCanLoadMore(false);
    }

    @Override
    protected void onLoad(int page) {
        mPresenter.getFolder();
    }

    @Override
    public void setFolder(List<FolderModel> models) {
        commonLoader.setData(models);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MusicModelEvent event) {
        if (event == null || getActivity() == null || getActivity().isFinishing()
                || event.type != MusicDB.LOCAL_ALL_MUSIC || mPresenter == null || !isLazyLoaded) {
            return;
        }
        mPresenter.getFolder();
    }
}