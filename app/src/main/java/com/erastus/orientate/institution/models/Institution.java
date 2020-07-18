package com.erastus.orientate.institution.models;

import com.erastus.orientate.models.GenericUser;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Institution")
public class Institution extends ParseObject {
    public static final String KEY_INSTITUTION_NAME = "institution_name";
    public static final String KEY_INSTITUTION_WEBSITE = "institution_website";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_USER = "user";

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

    public GenericUser getUser() {
        return new GenericUser(getParseUser(KEY_USER));
    }
}
