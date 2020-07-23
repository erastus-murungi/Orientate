package com.erastus.orientate.utils;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MedianLowFinder<Item extends Comparable<Item>> {

    private final PartitionInfo<Item> info = new PartitionInfo<>();

    public Item median(@NonNull List<Item> items) {
        int n = items.size();
        if (n <= 0) {
            throw new IndexOutOfBoundsException();
        }
        int mid = n >> 1;
        return quickSelect(items, 0, items.size(), mid);
    }

    public Item quickSelect(@NonNull List<Item> items, int lo, int hi, int k) {
        while ((hi - lo) > 0) {
            partition(items, lo, hi);
            if (k < info.nLows) {
                hi = info.nLows - 1;
            } else if (k < (info.nLows + info.nMiddle) || info.allSame && info.nHighs == 0) {
                return info.pivot;
            }
            else {
                k -= (info.nLows + info.nMiddle);
                hi = info.nHighs - 1;
                lo += info.nLows + info.nMiddle;
            }
        }
        return null;
    }

    public int defaultPivotSelector(List<Item> items, int lo, int hi) {
        return randomPivotSelector(lo, hi);
    }

    public int randomPivotSelector(int lo, int hi) {
        return lo + ThreadLocalRandom.current().nextInt(hi - lo);
    }

    void partition(@NonNull List<Item> items, int lo, int hi) {
        int i, j, s, e;
        Item p, lower, higher;
        lower = higher = p = items.get(defaultPivotSelector(items, lo, hi));
        info.pivot = p;

        boolean allSame = true;
        for (i = lo, j = lo, s = lo, e = hi; i <= e; ) {
            allSame = allSame && (items.get(j++) == p);
            if (items.get(i).compareTo(lower) < 0) {
                Collections.swap(items, i++, s++);
            } else if (items.get(i).compareTo(higher) > 0) {
                Collections.swap(items, i, e--);
            } else {
                info.nMiddle++;
                i++;
            }
        }
        info.nLows = i - info.nMiddle;
        info.nHighs = hi - e;
        info.allSame = allSame;
    }


     static class PartitionInfo<T> {
        int nLows;
        int nHighs;
        int nMiddle;
        T pivot;
        boolean allSame;

        PartitionInfo() {}
    }

}
