package com.erastus.orientate.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Objects;

@ParseClassName("User")
public class GenericUser extends ParseObject {
    public static final String KEY_PROFILE_IMAGE = "profile_image";
    public static final String KEY_IS_STUDENT = "is_student";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_EMAIL_VERIFIED = "emailVerified";

    public String getProfileImage() {
        return Objects.requireNonNull(getParseFile(KEY_PROFILE_IMAGE)).getUrl();
    }

    public boolean getIsStudent() {
        return getBoolean(KEY_IS_STUDENT);
    }

    public String getEmail() {
        return getString(KEY_EMAIL);
    }

    public boolean getIsEmailVerified() {
        return getBoolean(KEY_EMAIL_VERIFIED);
    }
}
