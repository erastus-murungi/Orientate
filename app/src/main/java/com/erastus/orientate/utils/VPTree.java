package com.erastus.orientate.utils;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ThreadLocalRandom;
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

public class VPTree<T> {

    class VPNode {

        /** A radius value defining the range of the node
         *
         * Once the vp is chosen, we will calculate <i>mu</i>.
         * The <i>mu</i> value represents the radius at which half the data is inside the radius,
         * and half is out.
         */

        private Double mu;

        /** Left subtree
         *  All the left children of a given node are the points inside the circle
         * */
        @Nullable private VPNode left;

        /** right subtree
         *  All the children outside the circle
         * */
        @Nullable private VPNode right;

        /** a vantage point chosen from the data */

        @NonNull
        private T vantagePoint;

        @Nullable List<T> children;


        VPNode(@NonNull List<T> points) {
            /* choosing a vantage point. It can be either be randomly chosen, or be calculated as
             * the node with the largest spread. */

            vantagePoint = selectVantagePoint(points);

            if (points.size() < 1) {
                return;
            }
            if (points.size() <= leafSize) {
                children = points;
            }
            /* Calculate the distance from each point to the vantagePoint
            * O(n) */
            List<Double> distancesToVantagePoint = points
                    .parallelStream()
                    .map((point) -> distanceFunction.apply(vantagePoint, point))
                    .collect(Collectors.toList());

            mu = medianFinder.median(distancesToVantagePoint);

            int initialCapacity = (points.size() >> 1) + 1;
            List<T> leftPoints = new ArrayList<>(initialCapacity);
            List<T> rightPoints = new ArrayList<>(initialCapacity);

            /* O(n) */
            for (int i = 0; i < points.size(); i++) {
                T point = points.get(i);
                Double dist = distancesToVantagePoint.get(i);

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

    public static final int DEFAULT_LEAF_SIZE = 16;

    private MedianLowFinder<Double> medianFinder = new MedianLowFinder<>();

    private static int leafSize;

    VPNode root;

    /**
     * A distance function which satisfies the triangle inequality
     */
    private DistanceFunction<T> distanceFunction;


    VPTree(List<T> items, DistanceFunction<T> distance, Integer leafSize) {
        distanceFunction = distance;
        VPTree.leafSize = leafSize;
        root = new VPNode(items);
    }

    VPTree(List<T> items, DistanceFunction<T> distance) {
        distanceFunction = distance;
        VPTree.leafSize = DEFAULT_LEAF_SIZE;
        root = new VPNode(items);
    }


    private void addNode(PriorityQueue<BPQItem<VPNode>> queue, VPNode node, T q) {
        if (node != null) {
            Double dist = distanceFunction.apply(q, node.vantagePoint);
            queue.add(new BPQItem<>(node, dist));
        }
    }

    /**
     * find the k nearest neighbors of q
     * @param q the item whose nearest neighbors to find
     * @param k the number of neighbors to find
     * @return a list of q's k nearest neighbors
     */

    List<BPQItem<T>> knnSearch(T q, int k) {
        /*
         * buffer for nearest neighbors
         * q is at a distance of 0 from itself x
         */
        BoundedPriorityQueue<BPQItem<T>> neighbors =
                new BoundedPriorityQueue<>(k, new BPQItem<>(q, 0.0d),
                        (v1, v2) -> distanceFunction.apply(v1.item, v2.item));

        /* list of nodes to visit*/
        PriorityQueue<BPQItem<VPNode>> visitStack =
                new PriorityQueue<>((v1, v2) -> v1.dist.compareTo(v2.dist));

        addNode(visitStack, root, q);

        /* Consider a circle of radius tau around the query point that
         * encloses all of its nearest neighbors.
         * Suppose we are searching for k nearest neighbors,
         * then tau would contain the closest k points.
         */

        Double tau = Double.POSITIVE_INFINITY;

        int totalSeen = 0;

        while (visitStack.size() > 0) {
            /* pop of the element with the smallest distance */

            BPQItem<VPNode> tuple = visitStack.poll();
            Double dist = tuple.dist;
            VPNode node = tuple.item;

            totalSeen += 1;

            if (node == null) {
                continue;
            }

            /* node os within area of interest, add node to the results and decrease tau*/
            if (dist < tau) {
                neighbors.push(new BPQItem<>(node.vantagePoint, dist));
                if (neighbors.isfull())
                    tau = neighbors.getLastItem().dist;
            }

            if (node.isLeaf() && node.children != null) {
                totalSeen += node.children.size();
                for (T childP : node.children) {
                    Double d = distanceFunction.apply(q, childP);
                    neighbors.push(new BPQItem<>(childP, d));
                }
                if (neighbors.size() == k) {
                    // shrink the radius of interest
                    tau = neighbors.getLastItem().dist;
                }
                continue;
            }
            if (dist < node.mu) {
                addNode(visitStack, node.left, q);
                if ((node.mu - dist) <= tau) {
                    addNode(visitStack, node.right, q);
                }
            } else {
                addNode(visitStack, node.right, q);
                if (dist - node.mu <= tau) {
                    addNode(visitStack, node.left, q);
                }
            }
        }
        System.out.println(totalSeen);
        return neighbors.getItems();
    }


    T selectVantagePoint(List<T> points) {
        return randomVantagePoint(points);
    }

    T randomVantagePoint(List<T> points) {
        int randomIndex = ThreadLocalRandom.current().nextInt(0, points.size());
        return points.remove(randomIndex);
    }
}
