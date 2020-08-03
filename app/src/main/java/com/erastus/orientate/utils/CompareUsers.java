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
}
