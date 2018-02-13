package com.d.lib.rxnet;

import com.d.lib.rxnet.base.HttpConfig;
import com.d.lib.rxnet.base.RetrofitClient;
import com.d.lib.rxnet.request.DeleteRequest;
import com.d.lib.rxnet.request.DownloadRequest;
import com.d.lib.rxnet.request.GetRequest;
import com.d.lib.rxnet.request.HeadRequest;
import com.d.lib.rxnet.request.OptionRequest;
import com.d.lib.rxnet.request.PatchRequest;
import com.d.lib.rxnet.request.PostRequest;
import com.d.lib.rxnet.request.PutRequest;
import com.d.lib.rxnet.request.UploadRequest;

import java.util.Map;

import retrofit2.Retrofit;

/**
 * New
 * Created by D on 2017/10/24.
 */
public class RxNet {

    public static RxNetIns getInstance() {
        return new RxNetIns();
    }

    public static HttpConfig.Build init() {
        return new HttpConfig.Build();
    }

    public static Retrofit getRetrofit() {
        return RetrofitClient.getInstance();
    }

    public RxNet() {
    }

    public static GetRequest.GetRequestF get(String url) {
        return new GetRequest.GetRequestF(url);
    }

    public static GetRequest.GetRequestF get(String url, Map<String, String> params) {
        return new GetRequest.GetRequestF(url, params);
    }

    public static PostRequest.PostRequestF post(String url) {
        return new PostRequest.PostRequestF(url);
    }

    public static PostRequest.PostRequestF post(String url, Map<String, String> params) {
        return new PostRequest.PostRequestF(url, params);
    }

    public static HeadRequest.HeadRequestF head(String url, Map<String, String> params) {
        return new HeadRequest.HeadRequestF(url, params);
    }

    public static OptionRequest.OptionRequestF options(String url, Map<String, String> params) {
        return new OptionRequest.OptionRequestF(url, params);
    }

    public static PutRequest.PutRequestF put(String url, Map<String, String> params) {
        return new PutRequest.PutRequestF(url, params);
    }

    public static PatchRequest.PatchRequestF patch(String url, Map<String, String> params) {
        return new PatchRequest.PatchRequestF(url, params);
    }

    public static DeleteRequest.DeleteRequestF delete(String url, Map<String, String> params) {
        return new DeleteRequest.DeleteRequestF(url, params);
    }

    public static DownloadRequest.DownloadRequestF download(String url) {
        return new DownloadRequest.DownloadRequestF(url);
    }

    public static DownloadRequest.DownloadRequestF download(String url, Map<String, String> params) {
        return new DownloadRequest.DownloadRequestF(url, params);
    }

    public static UploadRequest.UploadRequestF upload(String url) {
        return new UploadRequest.UploadRequestF(url);
    }

    public static class RxNetIns {

        RxNetIns() {
        }

        public GetRequest get(String url) {
            return new GetRequest(url);
        }

        public GetRequest get(String url, Map<String, String> params) {
            return new GetRequest(url, params);
        }

        public PostRequest post(String url) {
            return new PostRequest(url);
        }

        public PostRequest post(String url, Map<String, String> params) {
            return new PostRequest(url, params);
        }

        public HeadRequest head(String url, Map<String, String> params) {
            return new HeadRequest(url, params);
        }

        public OptionRequest options(String url, Map<String, String> params) {
            return new OptionRequest(url, params);
        }

        public PutRequest put(String url, Map<String, String> params) {
            return new PutRequest(url, params);
        }

        public PatchRequest patch(String url, Map<String, String> params) {
            return new PatchRequest(url, params);
        }

        public DeleteRequest delete(String url, Map<String, String> params) {
            return new DeleteRequest(url, params);
        }

        public DownloadRequest download(String url) {
            return new DownloadRequest(url);
        }

        public DownloadRequest download(String url, Map<String, String> params) {
            return new DownloadRequest(url, params);
        }

        public UploadRequest upload(String url) {
            return new UploadRequest(url);
        }
    }
}
