package com.d.lib.aster.scheduler.callback;

/**
 * Task
 * Created by D on 2018/5/15.
 */
public abstract class Task<T> {
    public abstract T run() throws Exception;
}
