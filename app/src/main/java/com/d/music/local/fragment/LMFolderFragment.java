package com.d.music.local.fragment;

import com.d.lib.pulllayout.Pullable;
import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.music.R;
import com.d.music.data.database.greendao.db.AppDatabase;
import com.d.music.event.eventbus.MusicModelEvent;
import com.d.music.local.adapter.FolderAdapter;
import com.d.music.local.model.FolderModel;

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
        return new FolderAdapter(mContext, new ArrayList<FolderModel>(), R.layout.module_local_adapter_folder);
    }

    @Override
    protected void initList() {
        super.initList();
        ((Pullable) mPullList).setCanPullDown(false);
        ((Pullable) mPullList).setCanPullUp(false);
    }

    @Override
    protected void onLoad(int page) {
        mPresenter.getFolder();
    }

    @Override
    public void setFolder(List<FolderModel> models) {
        mCommonLoader.loadSuccess(models);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onEvent(MusicModelEvent event) {
        if (event == null || getActivity() == null || getActivity().isFinishing()
                || event.type != AppDatabase.LOCAL_ALL_MUSIC || mPresenter == null || !mIsLazyLoaded) {
            return;
        }
        mPresenter.getFolder();
    }
}