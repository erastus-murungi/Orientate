package com.erastus.orientate.models;

import android.util.Log;

import com.erastus.orientate.applications.App;
import com.erastus.orientate.student.models.Student;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("User")
public class ExtendedParseUser extends ParseUser {
    public static final String KEY_IS_STUDENT = "is_student";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_EMAIL_VERIFIED = "emailVerified";
    public static final String KEY_STUDENT = "student";
    public final String TAG = getClassName();

    private ParseUser mUser;



    // for parse
    public ExtendedParseUser() {

    }


    @Override
    public String getObjectId() {
        return mUser.getObjectId();
    }

    @Override
    public String getUsername() {
        String username = mUser.getUsername();
        return username == null ? " " : username;
    }


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
        return getObjectId().equals(App.get().getCurrentUser().getObjectId());
    }

    public boolean isOnline() {
        return true;
    }

    public Student getStudent() {
        try {
            ParseObject object = mUser.getParseObject(KEY_STUDENT);
            return object.fetchIfNeeded();
        } catch (ParseException e) {
            Log.e(TAG, "getStudent: ", e);
            return null;
        }
    }
}
