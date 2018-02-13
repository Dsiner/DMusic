package com.d.lib.rxnet.func;

import com.d.lib.rxnet.exception.ApiException;
import com.d.lib.rxnet.util.RxUtil;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.lang.reflect.Type;

import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * ResponseBody转T
 */
public class ApiFunc<T> implements Function<ResponseBody, T> {
    private Type type;

    public ApiFunc(Type type) {
        this.type = type;
    }

    @Override
    public T apply(ResponseBody responseBody) throws Exception {
        RxUtil.printThread("RxNet_theard gsonFormat: ");
        Gson gson = new Gson();
        String json;
        try {
            json = responseBody.string();
            responseBody.close();
            if (type.equals(String.class)) {
                return (T) json;
            } else {
                return gson.fromJson(json, type);
            }
        } catch (IOException e) {
            if (responseBody != null) {
                responseBody.close();
            }
            throw new ApiException(-1, new JsonParseException("JSON PARSE ERROR!"));
        }
    }
}
