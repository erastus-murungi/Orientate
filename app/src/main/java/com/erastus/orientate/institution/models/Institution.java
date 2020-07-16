package com.erastus.orientate.institution.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Institution")
public class Institution extends ParseObject {
    public static final String KEY_INSTITUTION_NAME = "institution_name";
    public static final String KEY_INSTITUTION_WEBSITE = "institution_website";

    public String getInstitutionName() {
        return getString(KEY_INSTITUTION_NAME);
    }

    public String getKeyInstitutionWebsite() {
        return getString(KEY_INSTITUTION_WEBSITE);
    }
}
