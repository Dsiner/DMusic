package com.d.lib.rxnet.base;

import java.util.Iterator;
import java.util.LinkedHashMap;

public class Headers extends LinkedHashMap<String, String> {
    public Headers() {
        super();
    }

    public void addParam(String key, String value) {
        put(key, value == null ? "" : value);
    }

    public String getParam(String key) {
        return get(key);
    }

    @Override
    public String toString() {
        return getParamsString();
    }

    public String getParamsString() {
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
