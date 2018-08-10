package com.d.lib.common.module.taskscheduler;

import com.d.lib.common.module.taskscheduler.callback.Function;
import com.d.lib.common.module.taskscheduler.callback.Observer;
import com.d.lib.common.module.taskscheduler.callback.Task;
import com.d.lib.common.module.taskscheduler.schedule.FunctionEmitter;
import com.d.lib.common.module.taskscheduler.schedule.Schedulers;
import com.d.lib.common.module.taskscheduler.schedule.TaskEmitter;

import java.util.ArrayList;
import java.util.List;

/**
 * TaskScheduler
 * Created by D on 2018/5/15.
 */
public class TaskScheduler<T> {
    private Task<T> task;
    private int subscribeScheduler = Schedulers.defaultThread();

    /**
     * Causes the Runnable r to be added to the message queue.
     * The runnable will be run in the main thread
     */
    public static boolean postMain(Runnable r) {
        return TaskManager.getIns().postMain(r);
    }

    /**
     * Causes the Runnable r to be added to the message queue.
     * The runnable will be run in the main thread
     */
    public static boolean postMainDelayed(Runnable r, long delayMillis) {
        return TaskManager.getIns().postMainDelayed(r, delayMillis);
    }

    /**
     * Execute sync task in the main thread
     */
    public static void executeMain(Runnable r) {
        TaskManager.getIns().executeMain(r);
    }

    /**
     * Execute async task in the cached thread pool
     */
    public static void executeTask(Runnable r) {
        TaskManager.getIns().executeTask(r);
    }

    /**
     * Execute async task in the single thread pool
     */
    public static void executeSingle(Runnable r) {
        TaskManager.getIns().executeSingle(r);
    }

    /**
     * Execute async task in a new thread
     */
    public static void executeNew(Runnable r) {
        TaskManager.getIns().executeNew(r);
    }

    /**
     * Create task
     */
    public static <T> TaskScheduler<T> create(final Task<T> task) {
        TaskScheduler<T> schedulers = new TaskScheduler<T>();
        schedulers.task = task;
        return schedulers;
    }

    private TaskScheduler() {
    }

    public TaskObserve<T> subscribeOn(@Schedulers.Scheduler int scheduler) {
        this.subscribeScheduler = scheduler;
        return new TaskObserve<T>(new TaskEmitter<T>(task, subscribeScheduler));
    }

    public static class TaskObserve<T> {
        private TaskEmitter taskEmitter;
        private List<FunctionEmitter> emitters;
        private int observeOnScheduler = Schedulers.defaultThread();

        private TaskObserve() {
        }

        TaskObserve(TaskEmitter<T> taskEmitter) {
            this.taskEmitter = taskEmitter;
            this.emitters = new ArrayList<>();
        }

        TaskObserve(TaskObserve middle) {
            this.taskEmitter = middle.taskEmitter;
            this.observeOnScheduler = middle.observeOnScheduler;
            this.emitters = middle.emitters;
        }

        public TaskObserve<T> observeOn(@Schedulers.Scheduler int scheduler) {
            this.observeOnScheduler = scheduler;
            return this;
        }

        public <TR> TaskObserve<TR> map(Function<? super T, ? extends TR> f) {
            this.emitters.add(new FunctionEmitter<T, TR>(f, observeOnScheduler));
            return new TaskObserve<TR>(this);
        }

        public void subscribe() {
            subscribe(null);
        }

        public void subscribe(final Observer<T> callback) {
            Schedulers.switchThread(taskEmitter.scheduler, new Runnable() {
                @Override
                public void run() {
                    try {
                        Object t = taskEmitter.task.run();
                        if (assertInterrupt(t)) {
                            submit(t, callback);
                            return;
                        }
                        apply(t, emitters, callback);
                    } catch (Throwable e) {
                        error(e, callback);
                    }
                }
            });
        }

        private <E, F> void apply(final E o, final List<FunctionEmitter> emitters, final Observer<F> callback) {
            final FunctionEmitter<E, F> f = emitters.get(0);
            emitters.remove(f);
            Schedulers.switchThread(f.scheduler, new Runnable() {
                @Override
                public void run() {
                    try {
                        Object emitter = f.function.apply(o);
                        if (assertInterrupt(emitter)) {
                            submit(emitter, callback);
                            return;
                        }
                        apply(emitter, emitters, callback);
                    } catch (Throwable e) {
                        error(e, callback);
                    }
                }
            });
        }

        private boolean assertInterrupt(Object emitter) throws Exception {
            if (emitter == null) {
                throw new RuntimeException("Apply output must not be null!");
            }
            return emitters.size() <= 0;
        }

        private <S> void submit(final Object result, final Observer<S> callback) {
            Schedulers.switchThread(observeOnScheduler, new Runnable() {
                @Override
                public void run() {
                    try {
                        if (callback != null) {
                            callback.onNext((S) result);
                        }
                    } catch (Throwable e) {
                        error(e, callback);
                    }
                }
            });
        }

        private <S> void error(final Throwable e, final Observer<S> callback) {
            Schedulers.switchThread(observeOnScheduler, new Runnable() {
                @Override
                public void run() {
                    if (callback != null) {
                        callback.onError(e);
                    }
                }
            });
        }
    }
}
