package com.d.lib.rxnet.body;

import android.support.annotation.NonNull;

import com.d.lib.rxnet.exception.ApiException;
import com.d.lib.rxnet.listener.UploadCallBack;
import com.d.lib.rxnet.util.RxLog;

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
 * 上传进度请求实体类
 */
public class UploadProgressRequestBody extends RequestBody {
    private RequestBody requestBody;
    private UploadCallBack callback;
    private long lastTime;

    public UploadProgressRequestBody(RequestBody requestBody, UploadCallBack callback) {
        if (requestBody == null || callback == null) {
            throw new NullPointerException("this requestBody and callback must not null.");
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
        //当前字节长度
        private long currentLength = 0L;
        //总字节长度，避免多次调用contentLength()方法
        private long totalLength = 0L;

        CountingSink(Sink sink) {
            super(sink);
        }

        @Override
        public void write(@NonNull Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            //增加当前写入的字节数
            currentLength += byteCount;
            //获得contentLength的值，后续不再调用
            if (totalLength == 0) {
                totalLength = contentLength();
            }
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime >= 700 || lastTime == 0 || currentLength == totalLength) {
                lastTime = currentTime;
                Observable.just(currentLength).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        RxLog.d("upload progress currentLength:" + currentLength + ",totalLength:" + totalLength);
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
