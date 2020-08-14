package com.erastus.orientate.models;

import android.util.Log;

import com.erastus.orientate.institution.models.Institution;
import com.erastus.orientate.student.chat.conversations.FetchedLazily;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

@ParseClassName("Orientation")
public class Orientation extends ParseObject {
    public static final String TAG = "Orientation";
    public static final String KEY_TO = "to";
    public static final String KEY_OWNED_BY = "owned_by";
    public static final String KEY_WELCOME_MESSAGE = "welcome_message";
    public static final String KEY_NUM_VOTES = "num_votes";
    public static final String KEY_VOTE_AVERAGE = "vote_average";
    public static final String KEY_TITLE = "title";

    public Orientation() {}

    public String getWelcomeMessage() {
        return getString(KEY_WELCOME_MESSAGE);
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public int getNumVotes() {
        return getInt(KEY_NUM_VOTES);
    }

    public double getVoteAverage() {
        return getDouble(KEY_VOTE_AVERAGE);
    }

    @FetchedLazily
    public Institution getInstitution() {
        try {
            return getParseObject(KEY_OWNED_BY).fetchIfNeeded();
        } catch (ParseException | NullPointerException e) {
            Log.e(TAG, "getInstitution: ", e);
            return null;
        }
    }
}
