package com.d.lib.rxnet.callback;

/**
 * DownloadCallback
 * Created by D on 2017/10/24.
 */
public interface DownloadCallback {
    void onProgress(long currentLength, long totalLength);

    void onError(Throwable e);

    void onComplete();
}
