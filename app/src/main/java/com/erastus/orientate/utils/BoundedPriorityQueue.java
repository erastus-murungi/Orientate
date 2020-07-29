package com.erastus.orientate.utils;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BoundedPriorityQueue<T, R extends Comparable<R>> {

    class BPQItem {
        T item;
        R dist;

        public BPQItem(T item, R dist) {
            this.item = item;
            this.dist = dist;
        }
    }

    private final T base;
    private final List<BPQItem> bpq;
    private final int k;
    private VPTree.DistanceFunction<T, R> distanceFunction;

    public BoundedPriorityQueue(int k, T base, VPTree.DistanceFunction<T, R> distanceFunction) {
        this.bpq = new ArrayList<>();
        this.k = k;
        this.base = base;
        this.distanceFunction = distanceFunction;
    }

    void push(T point) {
        R dist = distanceFunction.apply(base, point);

        /* if the list still has space or the last item */
        if (bpq.size() < k || dist.compareTo(bpq.get(bpq.size() - 1).dist) < 0) {
            inSort(new BPQItem(point, dist));
            if (bpq.size() > k) {
                bpq.remove(k);
                assert bpq.size() <= k;
            }
        }
    }

    /**
     * @param item the item to insert into the bounded priority queue
     */
    void inSort(BPQItem item) {
        int pos = bisectRight(bpq, item, (o1, o2) -> o1.dist.compareTo(o2.dist), 0, bpq.size());
        bpq.add(pos, item);
    }

    /**
     * Return the index where to insert item x in list a, assuming a is sorted.
     *     The return value i is such that all e in a[:i] have e <= x, and all e in
     *     a[i:] have e > x.  So if x already appears in the list, a.insert(x) will
     *     insert just after the rightmost x already there.
     *     Optional args lo (default 0) and hi (default len(a)) bound the
     *     slice of a to be searched.
     *
     * @param a array of totally ordered items
     * @param x the item we are doing a binary search for
     * @param c a comparator function
     * @param lo starting <b>inclusive</b> index
     * @param hi ending <b>exclusive</b> index
     * @return i is such that all e in a[:i] have e <= x, and all e in
     * a[i:] have e > x
     *
     * @throws IndexOutOfBoundsException if hi < lo or lo < 0
     */
    <K> int bisectRight(List<K> a, K x, Comparator<K> c, int lo, int hi) {
        if (lo < 0 || hi < lo) {
            throw new IndexOutOfBoundsException();
        }

        while (lo < hi) {
            int mid = lo + ((hi - lo) >> 1);
            if (c.compare(a.get(mid), x) > 0) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        return lo;
    }


    void shrink(int k1) {
        if (k1 > k) {
            throw new IllegalArgumentException("New bound " + k1 + " is larger than previous bound " + k);
        }
        if (k1 < k) {
            bpq.subList(k1 - 1, bpq.size()).clear();
        }
    }

    T getLastItem() {
        return bpq.get(bpq.size() - 1).item;
    }

    List<T> getItems() {
        return bpq.parallelStream().map((bpqItem -> bpqItem.item)).collect(Collectors.toList());
    }
}
