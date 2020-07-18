package com.erastus.orientate.institution.models;

import com.erastus.orientate.models.GenericUser;

public class LocalInstitution {
    private String mInstitutionName;
    private String mInstitutionLocation;
    private String mInstitutionWebsiteUrl;
    private GenericUser mUser;
    private String mLocation;

    public String getInstitutionName() {
        return mInstitutionName;
    }

    public String getInstitutionLocation() {
        return mInstitutionLocation;
    }

    public String getInstitutionWebsiteUrl() {
        return mInstitutionWebsiteUrl;
    }

    public GenericUser getUser() {
        return mUser;
    }

    public static LocalInstitution localInstitutionFromParseInstitution(Institution institution) {
        LocalInstitution localInstitution = new LocalInstitution();
        localInstitution.mInstitutionLocation = institution.getLocation();
        localInstitution.mInstitutionName = institution.getInstitutionName();
        localInstitution.mInstitutionWebsiteUrl = institution.getInstitutionWebsiteUrl();
        localInstitution.mUser = institution.getUser();
        localInstitution.mLocation = institution.getLocation();
        return localInstitution;
    }

    public String getLocation() {
        return mLocation;
    }
}
