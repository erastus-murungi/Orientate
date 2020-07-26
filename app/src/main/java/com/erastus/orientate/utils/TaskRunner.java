package com.erastus.orientate.utils;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskRunner {
    public static final int DEFAULT_N_THREADS = 4;
    private static volatile TaskRunner sInstance;
    private final Executor mExecutor = Executors.newFixedThreadPool(DEFAULT_N_THREADS);
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public synchronized static TaskRunner getInstance() {
        if (sInstance == null) {
            sInstance = new TaskRunner();
        }
        return sInstance;
    }

    public interface Callback<R> {
        void onComplete(R result);
    }

    public <R> void executeAsync(Callable<R> callable, @NonNull Callback<R> callback) {
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
