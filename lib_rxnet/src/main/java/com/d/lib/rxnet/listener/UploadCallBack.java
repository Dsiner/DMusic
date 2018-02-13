package com.d.lib.rxnet.listener;

/**
 * UploadCallBack
 * Created by D on 2017/10/24.
 */
public interface UploadCallBack {
    void onProgress(long currentLength, long totalLength);

    void onError(Throwable e);

    void onComplete();
}
