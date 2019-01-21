package com.d.lib.aster.interceptor;

import java.io.IOException;

/**
 * Interceptor
 * Created by D on 2018/12/4.
 **/
public interface IInterceptor<Chain, Response> {
    Response intercept(Chain chain) throws IOException;
}
