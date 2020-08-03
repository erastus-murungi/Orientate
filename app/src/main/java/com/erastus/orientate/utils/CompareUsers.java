package com.erastus.orientate.utils;

import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.models.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;

public class CompareUsers {

    public static final float COUNTRY_WEIGHT = 2.0f;

    public static final float MAJOR_WEIGHT = 2.5f;

    public static final float HOBBY_WEIGHT = 1.8f;

    public static final float INTERESTS_WEIGHT = 3.0f;


    Double compare(ExtendedParseUser user1, ExtendedParseUser user2) {
        UserInfo userInfo1, userInfo2;

        userInfo1 = user1.getUserInfo();
        userInfo2 = user2.getUserInfo();

        return null;
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

    private String[] toStringArray(JSONArray jsonStringArray) throws JSONException {
        String[] output = new String[jsonStringArray.length()];
        for (int i = 0; i < jsonStringArray.length(); i++) {
            output[i] = jsonStringArray.getString(i);
        }
        return output;
    }
}
