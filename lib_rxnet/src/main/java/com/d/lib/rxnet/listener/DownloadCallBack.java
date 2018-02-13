package com.d.lib.rxnet.listener;

/**
 * DownloadCallBack
 * Created by D on 2017/10/24.
 */
public interface DownloadCallBack {
    void onProgress(long currentLength, long totalLength);

    void onError(Throwable e);

    void onComplete();
}
