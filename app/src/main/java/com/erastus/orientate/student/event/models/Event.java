package com.erastus.orientate.student.event.models;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.erastus.orientate.institution.models.Institution;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;


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
    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_LOCATION_STRING = "event_location_string";


    public LocalDateTime getStartingOn() {
        return Objects.requireNonNull(getStartDateTime(), "No happening date");
    }

    public LocalDateTime getEndingOn() {
        return getEndingDateTime();
    }

    public String getStringLocation() {
        return getString(KEY_LOCATION_STRING);
    }

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

    public LatLng getWhere() {
        ParseGeoPoint parseGeoPoint = Objects.requireNonNull(getParseGeoPoint(KEY_WHERE));
        return new LatLng(parseGeoPoint.getLatitude(), parseGeoPoint.getLongitude());
    }

    public boolean getIsRecurring() {
        return getBoolean(KEY_IS_RECURRING);
    }

    public Integer getUpVoteCount() {
        return getInt(KEY_UPVOTE_COUNT);
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

    public String getUrl() {
        return getString(KEY_URL);
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

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public String getBody() {
        return getString(KEY_BODY);
    }

    public String getLocationString() {
        return getString(KEY_LOCATION_STRING);
    }

    public void setUpVoteCount(Integer newUpVoteCount) {
        put(KEY_UPVOTE_COUNT, newUpVoteCount);
    }
}
