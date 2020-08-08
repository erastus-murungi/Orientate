package com.erastus.orientate.student.models;

import com.erastus.orientate.utils.DateUtils;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

@ParseClassName("Student")
public class Student extends ParseObject {
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_MIDDLE_NAME = "middle_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_DOB = "date_of_birth";
    public static final String KEY_ENROLLED_AT = "enrolled_at";
    public static final String KEY_USER = "user";
    public static final String KEY_PROFILE_IMAGE = "profile_image";

    public String getFirstName() {
        return getString(KEY_FIRST_NAME);
    }

    public String getMiddleName() {
        return getString(KEY_MIDDLE_NAME);
    }

    public String getLastName() {
        return getString(KEY_LAST_NAME);
    }

    public LocalDate getDateOfBirth() {
        return DateUtils.localDateTimeFromDate(Objects.requireNonNull(getDate(KEY_DOB))).toLocalDate();
    }

    public String getProfileImageUrl() {
        return Objects.requireNonNull(getParseFile(KEY_PROFILE_IMAGE)).getUrl();
    }

    public String getFullName() {
        if (getMiddleName() == null) {
            return getFirstName() + " " + getLastName();
        } else {
            return getFirstName() + " " + getMiddleName() + " " + getLastName();
        }
    }

    public void setFirstName(String firstName) {
        put(KEY_FIRST_NAME, firstName);
    }

    public void setMiddleName(String middleName) {
        put(KEY_MIDDLE_NAME, middleName);
    }

    public void setLastName(String lastName) {
        put(KEY_LAST_NAME, lastName);
    }

    public void setDob(Date dob) {
        put(KEY_DOB, dob);
    }

    public void setUser(String parseUser) {
        put(KEY_USER, parseUser);
    }

    public void setProfilePicture(ParseFile profilePicture) {
        put(KEY_PROFILE_IMAGE, profilePicture);
    }

    public ParseObject getEnrolledAtParseObject() {
        return getParseObject(KEY_ENROLLED_AT);
    }
}
