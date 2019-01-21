package com.d.lib.aster.interceptor;

import java.util.Map;

/**
 * Request header interception
 */
public abstract class IHeadersInterceptor {
    protected Map<String, String> mHeaders;
    protected OnHeadInterceptor mOnHeadInterceptor;

    protected IHeadersInterceptor(Map<String, String> headers) {
        this.mHeaders = headers;
    }

    public interface OnHeadInterceptor {

        /**
         * Some parameters may be dynamic, such as tokens, etc. You shoule override here
         * heads.put("token", "");
         */
        void intercept(Map<String, String> heads);
    }

    public IHeadersInterceptor setOnHeadInterceptor(OnHeadInterceptor onHeadInterceptor) {
        this.mOnHeadInterceptor = onHeadInterceptor;
        return this;
    }
}
