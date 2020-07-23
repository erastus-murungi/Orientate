package com.erastus.orientate.utils;



import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * @author Erastus Murungi
 *
 *  Vantage Point Tree @link <a href=https://en.wikipedia.org/wiki/Vantage-point_tree /> */

public class VPTree<T, R extends Comparable<R>> {

    public interface DistanceFunction<T, R> {
        R apply(T a, T b);
    }

    public static final int DEFAULT_LEAF_SIZE = 16;
    public static final int MIN_NUMBER_OF_POINTS_TO_SAMPLE = 10;
    static {

    }

    private static int leafSize;

    private int mu;

    @Nullable private VPTree<T, R> left;

    @Nullable private VPTree<T, R> right;

    private VPTree<T, R> vantagePoint;

    private DistanceFunction<T, R> distanceFunction;



    static class VPNode {
    }

    VPTree(Collection<T> items, DistanceFunction<T, R> distance, Integer leafSize) {
        distanceFunction = distance;
        VPTree.leafSize = leafSize;
    }


    T selectVantagePoint() {
        return null;
    }

    // picks the point with the largest spread
    T optimalVantagePoint(List<T> items) {
        int numSamples, numTests;
        numSamples = Math.max(MIN_NUMBER_OF_POINTS_TO_SAMPLE, items.size() / 1000);
        numTests = numSamples;
        Collection<T> sampledPoints = pickSample(items, numSamples,
                ThreadLocalRandom.current());
        for (T point: sampledPoints) {
            List<T> randPoints = pickSample(items, numTests, ThreadLocalRandom.current());
            List<R> distances = randPoints
                    .parallelStream()
                    .map((randPoint) -> distanceFunction.apply(point, randPoint))
                    .collect(Collectors.toList());
        }

        return null;

    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> pickSample(List<T> population, int nSamplesNeeded, Random r) {
        T[] samples = (T[]) Array.newInstance(Objects.requireNonNull(population.getClass().getComponentType()),
                nSamplesNeeded);
        int nPicked = 0, i = 0, nLeft = population.size();
        while (nSamplesNeeded > 0) {
            int rand = r.nextInt(nLeft);
            if (rand < nSamplesNeeded) {
                samples[nPicked++] = population.get(i);
                nSamplesNeeded--;
            }
            nLeft--;
            i++;
        }
        return List.of(samples);
    }
}
