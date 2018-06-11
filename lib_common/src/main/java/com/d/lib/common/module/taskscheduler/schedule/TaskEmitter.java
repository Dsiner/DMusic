package com.d.lib.common.module.taskscheduler.schedule;

import com.d.lib.common.module.taskscheduler.callback.Task;

/**
 * TaskEmitter
 * Created by D on 2018/5/16.
 */
public class TaskEmitter<T> extends Emitter {
    public Task<T> task;

    public TaskEmitter(Task<T> task, @Schedulers.Scheduler int scheduler) {
        this.task = task;
        this.scheduler = scheduler;
    }
}
