package com.erastus.orientate.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskRunner {
    private static volatile TaskRunner sInstance;
    public static final int DEFAULT_N_THREADS = 2;
    private final Executor mExecutor = Executors.newFixedThreadPool(DEFAULT_N_THREADS);
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public static TaskRunner getInstance() {
        if (sInstance == null) {
            sInstance = new TaskRunner();
        }
        return sInstance;
    }

    public interface Callback<R> {
        void onComplete(R result);
    }

    public <R> void executeAsync(Callable<R> callable, Callback<R> callback) {
        mExecutor.execute(() -> {
            final R result;
            try {
                result = callable.call();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            mHandler.post(() -> callback.onComplete(result));
        });
    }

}
