package com.d.lib.common.component.compat;

import android.os.Handler;
import android.os.Looper;

import java.lang.ref.WeakReference;

/**
 * TimerCompat
 * Created by D on 2019/8/22.
 */
public class TimerCompat {
    final private Handler mHandler;
    final private TimeTask mTimeTask;
    private TimerTaskCompat mTask;
    private long mDelay = 0;
    private long mPeriod = 0;
    private boolean mIsTimeTaskRunning = false;

    static class TimeTask implements Runnable {
        private final WeakReference<TimerCompat> reference;

        TimeTask(TimerCompat timer) {
            this.reference = new WeakReference<>(timer);
        }

        @Override
        public void run() {
            TimerCompat ref = reference.get();
            if (ref == null || !ref.mIsTimeTaskRunning) {
                return;
            }
            if (ref.mTask != null) {
                ref.mTask.run();
            }
            if (ref.mPeriod <= 0) {
                ref.stopTimeTask();
                return;
            }
            ref.reStartTimeTask(ref.mPeriod);
        }
    }

    private void reStartTimeTask(long delay) {
        stopTimeTask();
        mIsTimeTaskRunning = true;
        mHandler.postDelayed(mTimeTask, delay);
    }

    private void stopTimeTask() {
        mIsTimeTaskRunning = false;
        mHandler.removeCallbacks(mTimeTask);
    }

    public TimerCompat() {
        mHandler = new Handler(Looper.getMainLooper());
        mTimeTask = new TimeTask(this);
    }

    public void schedule(TimerTaskCompat task, long delay) {
        schedule(task, delay, 0);
    }

    public void schedule(TimerTaskCompat task, long delay, long period) {
        this.mTask = task;
        this.mDelay = delay;
        this.mPeriod = period;
        if (delay < 0) {
            throw new IllegalArgumentException("Negative delay.");
        }
        if (period < 0) {
            throw new IllegalArgumentException("Negative period.");
        }
        reStartTimeTask(mDelay);
    }

    public boolean isRunning() {
        return mIsTimeTaskRunning;
    }

    public void cancel() {
        stopTimeTask();
    }
}
