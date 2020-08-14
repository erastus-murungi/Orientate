package com.erastus.orientate.institution.models;

import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.student.chat.conversations.FetchedLazily;
import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Institution")
public class Institution extends ParseObject {
    public static final String KEY_INSTITUTION_NAME = "institution_name";
    public static final String KEY_INSTITUTION_WEBSITE = "institution_website";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_USER = "user";
    public static final String KEY_PROFILE_IMAGE = "profile_image";
    public static final String KEY_PHYSICAL_ADDRESS = "physical_address";

    public Institution() {}

    public String getInstitutionName() {
        return getString(KEY_INSTITUTION_NAME);
    }

    public String getInstitutionWebsiteUrl() {
        return getString(KEY_INSTITUTION_WEBSITE);
    }

    public String getLocation() {
        return getString(KEY_LOCATION);
    }

    @FetchedLazily
    public ExtendedParseUser getUser() {
        return new ExtendedParseUser(getParseUser(KEY_USER));
    }

    public String getProfileImageUrl() {
        return getParseFile(KEY_PROFILE_IMAGE).getUrl();
    }

    public String getPhysicalAddress() {
        return getString(KEY_PHYSICAL_ADDRESS);
    }
}
