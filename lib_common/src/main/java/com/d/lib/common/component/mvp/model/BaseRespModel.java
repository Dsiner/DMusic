package com.d.lib.common.component.mvp.model;

/**
 * Network request parsing base class,
 * Models involving json parsing inherit from this class to avoid obfuscating code.
 */
public class BaseRespModel extends BaseModel {
    public int status;
    public String desc = "";

    @Override
    public String toString() {
        return "status: " + status + "\tdesc: " + desc;
    }
}