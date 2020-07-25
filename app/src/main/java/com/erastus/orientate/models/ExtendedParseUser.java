package com.erastus.orientate.models;

import android.util.Log;

import com.erastus.orientate.applications.App;
import com.erastus.orientate.student.announcements.models.Announcement;
import com.parse.ParseClassName;
import com.parse.ParseUser;
import com.parse.boltsinternal.Task;

import org.parceler.Parcel;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ParseClassName("User")
public class ExtendedParseUser extends ParseUser {
    public static final String KEY_IS_STUDENT = "is_student";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_EMAIL_VERIFIED = "emailVerified";
    public static final String TAG = "GenericUser";

    // for parse
    public ExtendedParseUser() {
    }

    @Override
    public String getUsername() {
        return mUser.getUsername();
    }

    private ParseUser mUser;

    public ExtendedParseUser(ParseUser parseUser) {
        mUser = parseUser;
    }

    public boolean getIsStudent() {
        return mUser.getBoolean(KEY_IS_STUDENT);
    }

    public String getEmail() {
        return mUser.getString(KEY_EMAIL);
    }

    public boolean getIsEmailVerified() {
        return mUser.getBoolean(KEY_EMAIL_VERIFIED);
    }

    public boolean isMe() {
        return this == App.get().getCurrentUser();
    }

    public boolean isOnline() {
        return true;
    }
}
