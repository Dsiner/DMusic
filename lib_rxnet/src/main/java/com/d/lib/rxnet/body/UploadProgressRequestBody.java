package com.d.lib.rxnet.body;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import com.d.lib.rxnet.callback.UploadCallback;
import com.d.lib.rxnet.exception.ApiException;
import com.d.lib.rxnet.utils.ULog;

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
    private RequestBody requestBody;
    private UploadCallback callback;
    private long lastTime;

    public UploadProgressRequestBody(RequestBody requestBody, UploadCallback callback) {
        if (requestBody == null || callback == null) {
            throw new NullPointerException("This requestBody and callback must not be null.");
        }
        this.requestBody = requestBody;
        this.callback = callback;
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return requestBody.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        CountingSink countingSink = new CountingSink(sink);
        BufferedSink bufferedSink = Okio.buffer(countingSink);
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
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
            if (currentTime - lastTime >= 700 || lastTime == 0 || currentLength == totalLength) {
                lastTime = currentTime;
                Observable.just(currentLength).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        ULog.d("Upload progress currentLength: " + currentLength + " totalLength: " + totalLength);
                        callback.onProgress(currentLength, totalLength);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        callback.onError(new ApiException(-1, throwable));
                    }
                });
            }
        }
    }
}
