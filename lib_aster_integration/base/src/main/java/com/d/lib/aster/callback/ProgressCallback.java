package com.d.lib.aster.callback;

/**
 * UploadCallback
 * Created by D on 2017/10/24.
 */
public interface ProgressCallback {
    void onStart();

    void onProgress(long currentLength, long totalLength);

    void onSuccess();

    void onError(Throwable e);

    void onCancel();
}
