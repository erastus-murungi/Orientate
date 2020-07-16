package com.erastus.orientate.student.event.models;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.erastus.orientate.institution.models.Institution;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@RequiresApi(api = Build.VERSION_CODES.O)
@ParseClassName("Event")
public class Event extends ParseObject {
    public static final String TAG = Event.class.getSimpleName();
    public static final String KEY_STARTING_ON = "starting_on";
    public static final String KEY_ENDING_ON = "ending_on";
    public static final String KEY_MUST_ATTEND = "must_attend";
    public static final String KEY_WHERE = "where";
    public static final String KEY_IS_RECURRING = "is_recurring";
    public static final String KEY_UPVOTE_COUNT = "upvote_count";
    public static final String KEY_INSTITUTION = "owned_by";
    public static final String KEY_URL = "url";
    public static final String KEY_SPONSORS = "sponsors";

    public LocalDateTime getStartDateTime() {
        Date date = getDate(KEY_STARTING_ON);
        if (date == null) {
            throw new NullPointerException();
        } else {
            return dateToLocalDateTime(date);
        }
    }

    public static LocalDateTime dateToLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public LocalDateTime getEndingDateTime() {
        Date date = getDate(KEY_ENDING_ON);
        if (date != null) {
            return dateToLocalDateTime(date);
        } else {
            Log.i(TAG, "Event" + this.toString() + "does not have an ending date");
            return null;
        }
    }

    public LocalDateTime getCreatedAtLocalDateTime() {
        return dateToLocalDateTime(super.getCreatedAt());
    }

    public LocalDateTime getUpdatedAtLocalDateTime() {
        return dateToLocalDateTime(super.getUpdatedAt());
    }

    public boolean getIsMustAttend() {
        return getBoolean(KEY_MUST_ATTEND);
    }

    public ParseGeoPoint getWhere() {
        return getParseGeoPoint(KEY_WHERE);
    }

    public boolean getIsRecurring() {
        return getBoolean(KEY_IS_RECURRING);
    }

    public Long getUpVoteCount() {
        return getLong(KEY_UPVOTE_COUNT);
    }

    public Institution getInstitution() {
        return (Institution) getParseObject(KEY_INSTITUTION);
    }

    public String[] getSponsors() {
        try {
            return jsonArrayToStringArray(getJSONArray(KEY_SPONSORS));
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception", e);
            return null;
        }
    }

    public static String[] jsonArrayToStringArray(JSONArray jsonArray) throws JSONException {
        if (jsonArray == null) {
            Log.e(TAG, "Null pointer");
            return null;
        }
        String[] s = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            s[i] = jsonArray.getString(i);
        }
        return s;
    }
}
