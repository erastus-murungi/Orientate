package com.erastus.orientate.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

class MedianLowFinderTest {
    @Test
    public void randomPivotSelector_isCorrect() {} {
        int[] A =  {9, 38, 99, 29, 13,  5, 19,  7, 57, 16, 1};
        MedianLowFinder<Integer> mA = new MedianLowFinder<>();
        List<Integer> integerList = Arrays.stream(A).boxed().collect(Collectors.toList());
        int med = mA.quickSelectMedian(integerList);
        assertEquals(16, med);
    }

}