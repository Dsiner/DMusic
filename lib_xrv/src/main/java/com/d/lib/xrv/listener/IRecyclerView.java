package com.d.lib.xrv.listener;

/**
 * Interface
 * Created by D on 2017/4/26.
 */
public interface IRecyclerView {
    //下面的ItemViewType是保留值(ReservedItemViewType),如果用户的adapter与它们重复将会强制抛出异常。不过为了简化,我们检测到重复时对用户的提示是ItemViewType必须小于10000
    int TYPE_REFRESH_HEADER = 10000;//设置一个很大的数字,尽可能避免和用户的adapter冲突
    int TYPE_FOOTER = 10001;
    int HEADER_INIT_INDEX = 10002;
    float DRAG_RATE = 3;

    interface LoadingListener {
        void onRefresh();

        void onLoadMore();
    }
}
