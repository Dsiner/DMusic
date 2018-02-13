package com.d.commen.module.mvp.model;

/**
 * 网络请求数据解析model基类。让涉及json解析的model继承此类，以便混淆代码
 */
public class BaseRespModel extends BaseModel {
    public int status;
    public String desc = "";

    @Override
    public String toString() {
        return "status: " + status + "\tdesc: " + desc;
    }
}