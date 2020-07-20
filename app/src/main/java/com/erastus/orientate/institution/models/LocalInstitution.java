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

    public LocalInstitution(Institution institution) {
        this.mInstitutionLocation = institution.getLocation();
        this.mInstitutionName = institution.getInstitutionName();
        this.mInstitutionWebsiteUrl = institution.getInstitutionWebsiteUrl();
        this.mUser = institution.getUser();
        this.mLocation = institution.getLocation();
    }

    public String getLocation() {
        return mLocation;
    }
}
