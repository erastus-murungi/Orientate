package com.erastus.orientate.utils;

import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.models.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.Objects;

public class CompareUsers {

    public static final double COUNTRY_WEIGHT = 2.0d;

    public static final double MAJOR_WEIGHT = 2.5d;

    public static final double HOBBY_WEIGHT = 1.8d;

    public static final double INTERESTS_WEIGHT = 3.0d;

    public static final double MOVIE_WEIGHT = 2.0d;

    public static final double TV_WEIGHT = 2.0d;

    public Double compare(ExtendedParseUser user1, ExtendedParseUser user2) throws JSONException {
        double result = 0.0d;

        UserInfo userInfo1, userInfo2;

        userInfo1 = user1.getUserInfo();
        userInfo2 = user2.getUserInfo();

        result += factorMovies(userInfo1, userInfo2);
        result += factorTvShows(userInfo1, userInfo2);
        result += factorInterests(userInfo1, userInfo2);
        result += factorHobbies(userInfo1, userInfo2);

        result += factorCountryOfResidence(userInfo1, userInfo2);


        return result;
    }

    private double factorCountryOfResidence(UserInfo userInfo1, UserInfo userInfo2) {
        String s1 = userInfo1.getCountry();
        String s2 = userInfo2.getCountry();

        double d = damerauLevenshtein(s1, s2);

        return COUNTRY_WEIGHT * (d / Math.max(s1.length(), s2.length()));
    }

    private double factorHobbies(UserInfo userInfo1, UserInfo userInfo2) throws JSONException {
        String[] m1 = toStringArray(
                Objects.requireNonNull(
                        userInfo1.getHobbies()));

        Arrays.sort(m1);
        String sm1 = String.join("", m1);

        String[] m2 = toStringArray(
                Objects.requireNonNull(
                        userInfo2.getHobbies()));

        Arrays.sort(m2);
        String sm2 = String.join("", m2);

        double d = damerauLevenshtein(sm1, sm2);

        return HOBBY_WEIGHT * (d / Math.max(sm1.length(), sm2.length()));
    }

    private double factorInterests(UserInfo u1, UserInfo u2) throws JSONException {
        String[] m1 = toStringArray(
                Objects.requireNonNull(
                        u1.getJSONArray(
                                UserInfo.KEY_INTERESTS
                        )));

        Arrays.sort(m1);
        String sm1 = String.join("", m1);

        String[] m2 = toStringArray(
                Objects.requireNonNull(
                        u2.getJSONArray(
                                UserInfo.KEY_INTERESTS
                        )));

        Arrays.sort(m2);
        String sm2 = String.join("", m2);

        double d = damerauLevenshtein(sm1, sm2);

        // normalized
        return INTERESTS_WEIGHT * (d / Math.max(sm1.length(), sm2.length()));
    }

    private double factorTvShows(UserInfo u1, UserInfo u2) throws JSONException {
        String[] m1 = toStringArray(
                Objects.requireNonNull(
                        u1.getJSONArray(
                                UserInfo.KEY_TV_SHOWS
                        )));

        Arrays.sort(m1);
        String sm1 = String.join("", m1);

        String[] m2 = toStringArray(
                Objects.requireNonNull(
                        u2.getJSONArray(
                                UserInfo.KEY_TV_SHOWS
                        )));

        Arrays.sort(m2);
        String sm2 = String.join("", m2);

        double d = damerauLevenshtein(sm1, sm2);

        // normalized
        return TV_WEIGHT * (d / Math.max(sm1.length(), sm2.length()));
    }

    private double factorMovies(UserInfo u1, UserInfo u2) throws JSONException {
        String[] m1 = toStringArray(
                Objects.requireNonNull(
                        u1.getJSONArray(
                                UserInfo.KEY_MOVIES
                        )));

        Arrays.sort(m1);
        String sm1 = String.join("", m1);

        String[] m2 = toStringArray(
                Objects.requireNonNull(
                        u2.getJSONArray(
                                UserInfo.KEY_MOVIES
                        )));

        Arrays.sort(m2);
        String sm2 = String.join("", m2);
        double d = damerauLevenshtein(sm1, sm2);

        // normalized
        return MOVIE_WEIGHT * (d / Math.max(sm1.length(), sm2.length()));
    }

    /**
     * Calculates the string distance between source and target strings using
     * the Damerau-Levenshtein algorithm. The distance is case-sensitive.
     *
     * @param source The source String.
     * @param target The target String.
     * @return The distance between source and target strings.
     * @throws IllegalArgumentException If either source or target is null.
     */
    public static int damerauLevenshtein(CharSequence source, CharSequence target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Parameter must not be null");
        }
        int sourceLength = source.length();
        int targetLength = target.length();

        if (sourceLength == 0)
            return targetLength;
        if (targetLength == 0)
            return sourceLength;

        int[][] dist = new int[sourceLength + 1][targetLength + 1];
        for (int i = 1; i < sourceLength + 1; i++) {
            dist[i][0] = i;
        }
        for (int j = 1; j < targetLength + 1; j++) {
            dist[0][j] = j;
        }

        for (int i = 1; i < sourceLength + 1; i++) {
            for (int j = 1; j < targetLength + 1; j++) {
                // the substitution cost is 0 if the last characters of prefixes
                // are the the same else 1
                int subCost = source.charAt(i - 1) == target.charAt(j - 1) ? 0 : 1;

                dist[i][j] = minimum(
                        dist[i - 1][j] + 1,       // deletion
                        dist[i][j - 1] + 1,            // insertion
                        dist[i - 1][j - 1] + subCost); // substitution

                // swapping of adjacent characters
                if (i > 1 && j > 1 &&
                        source.charAt(i - 1) == target.charAt(j - 2) &&
                        source.charAt(i - 2) == target.charAt(j - 1)) {
                    dist[i][j] = minimum(dist[i][j],
                            dist[i - 2][j - 2] + subCost);
                }
            }
        }
        return dist[sourceLength][targetLength];
    }

    public static int minimum(int... xs) {
        if (xs.length == 0) {
            throw new IllegalArgumentException("minimum of null elements is undefined");
        }
        int minX = Integer.MAX_VALUE;
        for (int x : xs) {
            minX = Math.min(x, minX);
        }
        return minX;
    }

    public static String[] toStringArray(JSONArray jsonStringArray) throws JSONException {
        String[] output = new String[jsonStringArray.length()];
        for (int i = 0; i < jsonStringArray.length(); i++) {
            output[i] = jsonStringArray.getString(i);
        }
        return output;
    }
}
