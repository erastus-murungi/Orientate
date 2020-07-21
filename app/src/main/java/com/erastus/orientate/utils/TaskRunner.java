package com.erastus.orientate.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskRunner {
    private static volatile TaskRunner instance;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public static TaskRunner getInstance() {
        if (instance == null) {
            instance = new TaskRunner();
        }
        return instance;
    }

    public interface Callback<R> {
        void onComplete(R result);
    }

    public <R> void executeAsync(Callable<R> callable, Callback<R> callback) {
        executor.execute(() -> {
            final R result;
            try {
                result = callable.call();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            handler.post(() -> callback.onComplete(result));
        });
    }

}