package com.erastus.orientate.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Naming convention: Given_Preconditions_When_StateUnderTest_Then_ExpectedBehavior
     * Uses <b>PDD</b> Behavior Driven Development
     * Principles of BDD
     * Test-driven development is a software-development methodology
     * which essentially states that for each unit of software, a software developer must:
     *
     * define a test set for the unit first;
     * make the tests fail;
     * then implement the unit;
     * finally verify that the implementation of the unit makes the tests succeed.
 */

class BoundedPriorityQueueTest {

    BoundedPriorityQueue<Double> queue = new BoundedPriorityQueue<>(4, 1.0d, BoundedPriorityQueueTest::minkowski);

    private static Double minkowski(Double t, Double t1) {
        return Math.sqrt(t*t + t1*t1);
    }

    @Test
    void inSort() {

    }

    @Test
    void Given_SortedDoublesArray_When_ItemBeingSearchedIsTheLargest_Then_ReturnWillBeSizeOfArray() {
        List<Double> a = Arrays.asList(0.0d, 1.0d, 1.1d, 2.1d, 9.0d, 9.1d);
        final int expected = a.size();
        Double x = 10.0d;
        int pos = queue.bisectRight(a, x, Double::compareTo, 0, a.size());
        assertEquals(expected, pos);
    }

    @Test
    void Given_SortedDoublesArray_When_ItemBeingSearchedIsSmalled_Then_ReturnZero() {
        List<Double> a1 = Arrays.asList(0.1d, 1.0d, 1.1d, 2.1d, 9.0d, 9.1d);
        final int expected = 0;
        Double x1 = 0.0d;
        int pos = queue.bisectRight(a1, x1, Double::compareTo, 0, a1.size());
        assertEquals(expected, pos);
    }

    @Test
    void Given_SortedDoublesArray_When_ItemBeingSearchedIsFourthSmallest_Then_ReturnThree() {
        List<Double> a = Arrays.asList(0.1d, 0.2d, 0.4d, 0.6d);
        final int expected = 3;
        Double x = 0.5d;
        int pos = queue.bisectRight(a, x, Double::compareTo, 0, a.size());
        assertEquals(expected, pos);
    }

    @Test
    void Given_EmptyDoublesArray_When_Then_ReturnZero() {
        List<Double> a = Collections.emptyList();
        final int expected = 0;
        Double x = 0.5d;
        int pos = queue.bisectRight(a, x, Double::compareTo, 0, a.size());
        assertEquals(expected, pos);
    }

    @Test
    void Given_SortedDoublesArrayWithSimilarElements_WhenItemBeingSearchedIsTheSameAsThem_Then_ReturnSizeOfTheArray() {
        List<Double> a = Arrays.asList(0.1d, 0.1d, 0.1d, 0.1d, 0.1d, 0.1d, 0.1d, 0.1d);
        final int expected = a.size();
        Double x = 0.1d;
        int pos = queue.bisectRight(a, x, Double::compareTo, 0, a.size());
        assertEquals(expected, pos);
    }

    @Test
    void Given_SortedDoublesArray_When_ItemIsVeryLarge_Then_ReturnTheSizeOfTheArray() {
        List<Double> a = Arrays.asList(0.1d, 0.2d, 0.3d, 0.4d, 0.5d, 0.6d, 0.7d, 0.8d, 0.9d);
        final int expected = a.size();
        Double x = 10000.0d;
        int pos = queue.bisectRight(a, x, Double::compareTo, 0, a.size());
        assertEquals(expected, pos);
    }

    @Test
    void Given_SortedDoublesArray_When_ItemIsVerySmall_Then_ReturnZero() {
        List<Double> a = Arrays.asList(0.1d, 0.2d, 0.3d, 0.4d, 0.5d, 0.6d, 0.7d, 0.8d, 0.9d);
        final int expected = 0;
        Double x = -0000000.1d;
        int pos = queue.bisectRight(a, x, Double::compareTo, 0, a.size());
        assertEquals(expected, pos);
    }

    @Test
    void Given_PriorityQueue_WhenItIsEmpty_Then_InsertItemsSuccessfully() {
        queue.push(0.1d);
        queue.push(0.0d);
        assertEquals(2, queue.size());
    }

    @Test
    void Given_PriorityQueue_When_ItemsInserted_Then_PeekReturnSmallestItem() {
        queue.clear();
        queue.push(0.3d);
        queue.push(0.0d);
        queue.push(0.6d);
        assertEquals((Double) 0.0d, queue.peek());
    }

    @Test
    void Given_PriorityQueue_When_KPlusOneItemsInserted_Then_ReturnSizeQueueIsK() {
        queue.clear();
        queue.push(0.3d);
        queue.push(0.0d);
        queue.push(0.6d);
        queue.push(0.6d);
        queue.push(0.6d);
        queue.push(0.6d);
        assertEquals(4, queue.size());
    }

    @Test
    void Given_PriorityQueue_When_KPlusOneItemsInserted_Then_PollMakesListSizeEqualK() {
        queue.clear();
        queue.push(0.3d);
        queue.push(0.0d);
        queue.push(0.6d);
        queue.push(0.6d);
        queue.push(0.6d);
        queue.push(0.6d);
        assertEquals((Double) 0.0d, queue.poll());
        assertEquals(3, queue.size());
    }

    @Test
    void Given_PriorityQueue_When_ListIsUnsorted_Then_() {
        List<Double> as = Arrays.asList(1d, 0d, 10d, 3d, 6d, 15d, 18d, 2d, 20d, 5d,
                13d, 5d, 1d, 2d, 12d, 18d, 0d, 17d, 3d, 11d);
        for (Double a: as) {
            queue.push(a);
        }
        assertArrayEquals(new Double[] {0d, 0d, 1d, 1d}, queue.getItems().toArray());
        System.out.println(queue);
    }
}