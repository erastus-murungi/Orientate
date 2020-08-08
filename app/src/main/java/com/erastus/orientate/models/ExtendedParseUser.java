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
    public static final String KEY_USER_INFO = "user_info";
    public static final String KEY_WANTS_ROOM = "wants_room";
    public static final String KEY_IS_MASTER = "is_master";
    public final String TAG = getClassName();

    private ParseUser mUser;

    // empty constructor for parse
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

    public UserInfo getUserInfo() {
        try {
            ParseObject object = mUser.getParseObject(KEY_USER_INFO);
            return object.fetchIfNeeded();
        } catch (ParseException e) {
            Log.e(TAG, "getStudent: ", e);
            return null;
        }
    }

    public ParseObject getStudentParseObject() {
        return mUser.getParseObject(KEY_STUDENT);
    }

    public boolean getWantsRoom() {
        return mUser.getBoolean(KEY_WANTS_ROOM);
    }

    public void setWantsRoom(boolean wantsRoom) {
        put(KEY_WANTS_ROOM, wantsRoom);
    }

    public boolean getIsMaster() {
        return mUser.getBoolean(KEY_IS_MASTER);
    }
}
