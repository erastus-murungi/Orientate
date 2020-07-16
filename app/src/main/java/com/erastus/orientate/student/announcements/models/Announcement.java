package com.erastus.orientate.student.announcements.models;

import android.util.Log;

import com.erastus.orientate.institution.models.Institution;
import com.erastus.orientate.student.event.Urgency;
import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Announcement")
public class Announcement extends ParseObject {
    public static final String TAG = Announcement.class.getSimpleName();
    public static final String KEY_URGENCY_LEVEL = "urgency_level";
    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_URL = "url";
    public static final String KEY_INSTITUTION = "owner";


    public Urgency getUrgencyLevel() {
        switch (getInt(KEY_URGENCY_LEVEL)) {
            case 0:
                return Urgency.CASUAL;
            case 1:
                return Urgency.IMPORTANT;
            case 2:
                return Urgency.VERY_IMPORTANT;
            default:
                Log.e(TAG, "Urgency.ERROR");
                return Urgency.ERROR;
        }
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public String getBody() {
        return getString(KEY_BODY);
    }

    public String getUrl() {
        return getString(KEY_URL);
    }

    public Institution getOwnerInstitution() {
        return (Institution) getParseObject(KEY_INSTITUTION);
    }
}
