package com.erastus.orientate.utils;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ParallelVpTree<T> {
    public static final int PARALLEL_CUTOFF = 10_000;

    private final static Object mutex = new Object();

    private List<VPTree<T>> trees;

    public ParallelVpTree(List<T> points, int numTrees, DistanceFunction<T> distanceFunction, int minLeaf) {
        int n = points.size();

        trees = new ArrayList<>();
        // singleton tree
        if (n < PARALLEL_CUTOFF) {
            VPTree<T> t = new VPTree<>(points, distanceFunction, minLeaf);
            trees.add(t);
        }
        else {
            int treeSize = (n / numTrees) + 1;
            for (int i = 0; i < numTrees; i++) {
                int s = treeSize * i;
                int e = treeSize * (i + 1);
                List<T> subset = new ArrayList<T>(points.subList(s, e));
                trees.add(new VPTree<>(subset, distanceFunction, minLeaf));
            }
        }
    }


    List<BPQItem<T>> getNearestNeighbors(T q, int k) {
        class C implements Callable<List<BPQItem<T>>> {
            VPTree<T> tree;
            C(VPTree<T> tree) {
              this.tree = tree;
            }
            @Override
            public List<BPQItem<T>> call() {
                return tree.knnSearch(q, k);
            }
        }

        List<BPQItem<T>> results = new ArrayList<>();

        for (VPTree<T> tree: trees) {
            TaskRunner.getInstance().executeAsync(new C(tree), (data) -> {
                synchronized (mutex) {
                    results.addAll(data);
                }
            });
        }
        results.sort((a, b) -> a.dist.compareTo(b.dist));
        return results;
    }

    public static class TaskRunner {
        public static final int DEFAULT_N_THREADS = Runtime.getRuntime().availableProcessors();
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

        public <R> void executeAsync(Callable<R> callable, @NonNull TaskRunner.Callback<R> callback) {
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
}
