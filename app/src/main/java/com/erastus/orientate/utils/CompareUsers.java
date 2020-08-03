package com.erastus.orientate.utils;

import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.models.UserInfo;

public class CompareUsers {

    public static final int COUNTRY_WEIGHT = 2;

    Double compare(ExtendedParseUser user1, ExtendedParseUser user2) {
        UserInfo userInfo1, userInfo2;

        userInfo1 = user1.getUserInfo();
        userInfo2 = user2.getUserInfo();

        return null;
    }


    /**
     * @param x String
     * @param y String
     * @return
     */


    private int levenshteinDistance(String x, String y) {
        int nx = x.length();
        int ny = y.length();

        int[][] D = new int[nx + 1][ny + 1];

        // the distance between x and (null) y
        for (int ix = 1; ix <= nx; ix++) {
            D[ix][0] = ix;
        }

        // the distance between y and (null) x
        for (int iy = 1; iy <= ny; iy++) {
            D[iy][0] = iy;
        }

        for (int ix = 1; ix < nx; ix++) {
            for (int iy = 1; iy < ny; iy++) {
                int subCost = x.charAt(ix + 1) == y.charAt(iy + 1) ? 0 : 1;

                D[ix][iy] = minimum(
                        D[ix - 1][iy] + 1,   // delete a character in x
                        D[ix][iy - 1] + 1,   // insert a character in x
                        D[ix - 1][iy - 1] + subCost  // substitute a character in x
                );
            }
        }

        return D[nx][ny];
    }

    private int minimum(int x1, int x2, int x3) {
        return Math.min(Math.min(x1, x2), x3);
    }

}
