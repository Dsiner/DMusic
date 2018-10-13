package com.d.lib.rxnet.body;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import com.d.lib.rxnet.callback.ProgressCallback;
import com.d.lib.rxnet.utils.ULog;
import com.d.lib.rxnet.utils.Util;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Upload progress request entity class
 */
public class UploadProgressRequestBody extends RequestBody {
    // The two progress update intervals cannot be less than 1000ms
    private static final int MIN_DELAY_TIME = 1000;

    private RequestBody mRequestBody;
    private ProgressCallback mCallback;
    private long mLastTime;

    public UploadProgressRequestBody(@NonNull RequestBody requestBody, @NonNull ProgressCallback callback) {
        this.mRequestBody = requestBody;
        this.mCallback = callback;
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return mRequestBody.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        Util.executeMain(new Runnable() {
            @Override
            public void run() {
                mCallback.onStart();
            }
        });
        BufferedSink bufferedSink;
        try {
            bufferedSink = Okio.buffer(new CountingSink(sink));
            mRequestBody.writeTo(bufferedSink);
            bufferedSink.flush();
            Util.executeMain(new Runnable() {
                @Override
                public void run() {
                    mCallback.onSuccess();
                }
            });
        } catch (final Throwable e) {
            e.printStackTrace();
            Util.executeMain(new Runnable() {
                @Override
                public void run() {
                    mCallback.onError(e);
                }
            });
            throw e;
        }
    }

    private final class CountingSink extends ForwardingSink {
        // Current byte length
        private long currentLength = 0L;
        // Total byte length, avoid calling the contentLength() method multiple times
        private long totalLength = 0L;

        CountingSink(Sink sink) {
            super(sink);
        }

        @SuppressLint("CheckResult")
        @Override
        public void write(@NonNull Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            // Increase the number of bytes currently written
            currentLength += byteCount;
            // Get the value of contentLength, no longer call later
            if (totalLength == 0) {
                totalLength = contentLength();
            }
            long currentTime = System.currentTimeMillis();
            if (currentTime - mLastTime >= MIN_DELAY_TIME || mLastTime == 0 || currentLength == totalLength) {
                mLastTime = currentTime;
                Observable.just(currentLength).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        ULog.d("Upload progress currentLength: " + currentLength + " totalLength: " + totalLength);
                        mCallback.onProgress(currentLength, totalLength);
                    }
                });
            }
        }
    }
}
