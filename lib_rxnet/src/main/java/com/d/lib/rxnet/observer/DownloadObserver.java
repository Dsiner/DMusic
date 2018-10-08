package com.d.lib.rxnet.observer;

import com.d.lib.rxnet.callback.DownloadCallback;
import com.d.lib.rxnet.request.DownloadRequest;
import com.d.lib.rxnet.utils.Util;

/**
 * Observer with Download Callback
 * Created by D on 2017/10/26.
 */
public class DownloadObserver extends AbsObserver<DownloadRequest.DownloadModel> {
    private DownloadCallback callback;

    public DownloadObserver(DownloadCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onNext(DownloadRequest.DownloadModel m) {
        Util.printThread("RxNet_theard downloadOnNext");
        callback.onProgress(m.downloadSize, m.totalSize);
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        callback.onError(e);
    }

    @Override
    public void onComplete() {
        callback.onComplete();
    }
}
