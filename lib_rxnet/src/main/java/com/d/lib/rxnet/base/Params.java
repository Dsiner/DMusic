package com.d.lib.rxnet.base;

import java.util.Iterator;
import java.util.LinkedHashMap;

public class Params extends LinkedHashMap<String, String> {
    private String rtp = "";

    public Params() {
        super();
    }

    public Params(String rtp) {
        super();
        this.rtp = rtp;
    }

    public void addParam(String key, String value) {
        // Retrofit2's FeildMap does not allow value to be null
        put(key, value == null ? "" : value);
    }

    public String getParam(String key) {
        return get(key);
    }

    @Override
    public String toString() {
        return getRequestParamsString();
    }

    public String getRequestParamsString() {
        StringBuilder param = new StringBuilder();
        if (size() > 0) {
            Iterator ite = entrySet().iterator();
            while (ite.hasNext()) {
                Entry<String, String> entry = (Entry) ite.next();
                String key = entry.getKey();
                String value = entry.getValue();
                param.append(key + "=" + value + "&");
            }
            param.deleteCharAt(param.length() - 1);
        }
        return param.toString();
    }
}
