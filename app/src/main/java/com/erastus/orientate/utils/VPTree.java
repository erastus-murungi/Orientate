package com.erastus.orientate.utils;



import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * @author Erastus Murungi
 *
 *  Vantage Point Tree @link <a href=https://en.wikipedia.org/wiki/Vantage-point_tree />
 *
 *  Like the KD-Tree, each VP-Tree Node cuts the space, but rather than using coordinate values,
 *  It employs distance from a selected vantage point. Near points make up the left/inside
 *  subspace while the right/outside subspace consists of far points.
 *  Proceeding recursively, a binary tree is formed.
 *  Randomized algorithms for vp-tree construction execute in O(n*log n) time and the resulting
 *  tree occupies O(n) space.
 *
 *  */

public class VPTree<T, R extends Comparable<R>> {

    class VPNode {

        /** A radius value defining the range of the node */
        private R mu;

        /** Left subtree
         *  All the left children of a given node are the points inside the circle
         * */
        @Nullable private VPNode left;

        /** right subtree
         *  All the children outside the circle
         * */
        @Nullable private VPNode right;

        /** a vantage point chosen from the data */

        @NonNull private T vantagePoint;

        @NonNull List<T> data;

        @Nullable List<T> children;


        VPNode(@NonNull List<T> points) {
            data = points;
            vantagePoint = selectVantagePoint(points);

            if (points.size() < 1) {
                return;
            }
            if (points.size() <= leafSize) {
                children = points;
            }

            List<R> distancesToVantagePoint = points
                    .parallelStream()
                    .map((point) -> distanceFunction.apply(vantagePoint, point))
                    .collect(Collectors.toList());
            mu = medianFinder.nLogNMedian(distancesToVantagePoint);

            int initialCapacity = (points.size() >> 1) + 1;
            List<T> leftPoints = new ArrayList<>(initialCapacity);
            List<T> rightPoints = new ArrayList<>(initialCapacity);

            for (int i = 0; i < points.size(); i++) {
                T point = points.get(i);
                R dist = distancesToVantagePoint.get(i);

                if (dist.compareTo(mu) < 0) {
                    leftPoints.add(point);
                } else {
                    rightPoints.add(point);
                }
            }
            if (leftPoints.size() > 0) {
                left = new VPNode(leftPoints);
            }
            if (rightPoints.size() > 0) {
                right = new VPNode(rightPoints);
            }
        }

        boolean isLeaf() {
            return left == null && right == null;
        }
    }

    public interface DistanceFunction<T, R> {
        R apply(T a, T b);
    }

    public static final int DEFAULT_LEAF_SIZE = 16;

    public static final int MIN_NUMBER_OF_POINTS_TO_SAMPLE = 10;

    private MedianLowFinder<R> medianFinder = new MedianLowFinder<>();

    private static int leafSize;

    VPNode root;

    /**
     * A distance function which satisfies the triangle inequality
     */
    private DistanceFunction<T, R> distanceFunction;


    VPTree(List<T> items, DistanceFunction<T, R> distance, Integer leafSize) {
        distanceFunction = distance;
        VPTree.leafSize = leafSize;
        root = new VPNode(items);
    }

    List<T> knnSearch(int k) {
        return null;
    }


    T selectVantagePoint(List<T> points) {
        return randomVantagePoint(points);
    }

    T randomVantagePoint(List<T> points) {
        int randomIndex = ThreadLocalRandom.current().nextInt(0, points.size());
        return points.remove(randomIndex);
    }

    // picks the point with the largest spread
//    VPNode optimalVantagePoint(List<T> items) {
//        int numSamples, numTests;
//        numSamples = Math.max(MIN_NUMBER_OF_POINTS_TO_SAMPLE, items.size() / 1000);
//        numTests = numSamples;
//        Collection<T> sampledPoints = pickSample(items, numSamples,
//                ThreadLocalRandom.current());
//        for (T point: sampledPoints) {
//            List<T> randPoints = pickSample(items, numTests, ThreadLocalRandom.current());
//            List<R> distances = randPoints
//                    .parallelStream()
//                    .map((randPoint) -> distanceFunction.apply(point, randPoint))
//                    .collect(Collectors.toList());
//        }
//
//        return null;
//
//    }

//    @SuppressWarnings("unchecked")
//    public static <T> List<T> pickSample(List<T> population, int nSamplesNeeded, Random r) {
//        T[] samples = (T[]) Array.newInstance(Objects.requireNonNull(population.getClass().getComponentType()),
//                nSamplesNeeded);
//        int nPicked = 0, i = 0, nLeft = population.size();
//        while (nSamplesNeeded > 0) {
//            int rand = r.nextInt(nLeft);
//            if (rand < nSamplesNeeded) {
//                samples[nPicked++] = population.get(i);
//                nSamplesNeeded--;
//            }
//            nLeft--;
//            i++;
//        }
//        return List.of(samples);
//    }
}
