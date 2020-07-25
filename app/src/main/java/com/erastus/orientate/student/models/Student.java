package com.erastus.orientate.student.models;

import com.erastus.orientate.utils.DateUtils;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.time.LocalDate;
import java.util.Objects;

@ParseClassName("Student")
public class Student extends ParseObject {
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_MIDDLE_NAME = "middle_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_DOB = "date_of_birth";
    public static final String KEY_INSTITUTION = "institution";
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
}
