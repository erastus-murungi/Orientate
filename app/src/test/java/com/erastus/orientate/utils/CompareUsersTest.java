package com.erastus.orientate.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompareUsersTest {


    @Test
    public void GivenTwoStrings_When_Empty_Then_ReturnZero() {
        final int expected = 0;
        final int actual = CompareUsers.damerauLevenshtein("", "");
        assertEquals(expected, actual);
    }

    @Test
    public void GivenTwoStrings_When_FirstStringIsEmpty_Then_LengthOfNonEmptySecondString() {
        final String sy = "Erastus";
        final int expected = sy.length();
        final int actual = CompareUsers.damerauLevenshtein("", sy);
        assertEquals(expected, actual);
    }

    @Test
    public void GivenTwoStrings_When_SecondStringIsEmpty_Then_LengthOfNonEmptyFirstString() {
        final String sx = "Erastus";
        final int expected = sx.length();
        final int actual = CompareUsers.damerauLevenshtein(sx, "");
        assertEquals(expected, actual);
    }

    @Test
    public void GivenTwoStrings_When_EditDistanceIsThree_Then_ReturnCorrectAnswer() {
        final String sx = "kitten";
        final String sy = "sitting";
        final int expected = 3;
        final int actual = CompareUsers.damerauLevenshtein(sx, sy);
        assertEquals(expected, actual);
    }

    @Test
    public void GivenTwoStrings_When_EditDistanceIsOne_Then_ReturnCorrectAnswer() {
        final String sx = "kitten";
        final String sy = "kittens";
        final int expected = 1;
        final int actual = CompareUsers.damerauLevenshtein(sx, sy);
        assertEquals(expected, actual);
    }

    @Test
    public void Given_TwoStrings_ThenReturnCorrectAnswer() {
        assertEquals(4, CompareUsers.damerauLevenshtein("", "test"));
        assertEquals(4, CompareUsers.damerauLevenshtein("test", ""));
        assertEquals(0, CompareUsers.damerauLevenshtein("", ""));
        assertEquals(0, CompareUsers.damerauLevenshtein(" ", " "));
        assertEquals(1, CompareUsers.damerauLevenshtein("", " "));
        assertEquals(1, CompareUsers.damerauLevenshtein(" ", ""));
        assertEquals(0, CompareUsers.damerauLevenshtein("test", "test"));
        assertEquals(1, CompareUsers.damerauLevenshtein("Test", "test"));
        assertEquals(1, CompareUsers.damerauLevenshtein("test", "testy"));
        assertEquals(1, CompareUsers.damerauLevenshtein("testy", "test"));
        assertEquals(3, CompareUsers.damerauLevenshtein("test", "testing"));
        assertEquals(1, CompareUsers.damerauLevenshtein("test", "tets"));
        assertEquals(2, CompareUsers.damerauLevenshtein("test", "tsety"));
        assertEquals(3, CompareUsers.damerauLevenshtein("Test", "tsety"));
        assertEquals(1, CompareUsers.damerauLevenshtein("test", "test "));
        assertEquals(4, CompareUsers.damerauLevenshtein("file", "test"));
        assertEquals(5, CompareUsers.damerauLevenshtein("file", "testy"));
    }

    @Test
    public void testSourceNull() {
        assertThrows(IllegalArgumentException.class, () ->
        CompareUsers.damerauLevenshtein(null, ""));
    }
    @Test
    public void testTargetNull() {
        assertThrows(IllegalArgumentException.class, () ->
        CompareUsers.damerauLevenshtein("", null));
    }

    @Test
    public void testSourceAndTargetNull() {
        assertThrows(IllegalArgumentException.class, () ->
        CompareUsers.damerauLevenshtein(null, null));
    }
}