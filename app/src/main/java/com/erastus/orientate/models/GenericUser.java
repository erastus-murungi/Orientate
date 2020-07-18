package com.erastus.orientate.models;

import android.util.Log;

import com.erastus.orientate.student.announcements.models.Announcement;
import com.erastus.orientate.student.announcements.models.AnnouncementState;
import com.erastus.orientate.student.models.DataState;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.boltsinternal.Task;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ParseClassName("User")
public class GenericUser extends ParseUser {
    public static final String KEY_PROFILE_IMAGE = "profile_image";
    public static final String KEY_IS_STUDENT = "is_student";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_EMAIL_VERIFIED = "emailVerified";
    private static final String TAG = "GenericUser";

    public GenericUser() {}

    private ParseUser mUser;

    public GenericUser(ParseUser parseUser) {
        mUser = parseUser;
    }

    public String getProfileImageUrl() {
        Task<ParseUser> parseUserTask = mUser.fetchIfNeededInBackground();

        try {
            parseUserTask.waitForCompletion(Announcement.MAX_DURATION, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (parseUserTask.isCompleted() && parseUserTask.getError() == null) {
            mUser = parseUserTask.getResult();
        } if (!parseUserTask.isCompleted() && parseUserTask.getError() == null) {
            Log.e(TAG, "Error timed out", new TimeoutException());
        }
        return Objects.requireNonNull(mUser.getParseFile(KEY_PROFILE_IMAGE)).getUrl();
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
}
