package com.erastus.orientate.student.announcements.models;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.erastus.orientate.institution.models.Institution;
import com.erastus.orientate.student.models.Urgency;
import com.erastus.orientate.utils.DateUtils;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.boltsinternal.Task;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@ParseClassName("Announcement")
public class Announcement extends ParseObject {
    public static final String TAG = Announcement.class.getSimpleName();
    public static final long MAX_DURATION = 50L;
    public static final String KEY_URGENCY_LEVEL = "urgency_level";
    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_URL = "url";
    public static final String KEY_INSTITUTION = "owner";
    public static final String KEY_POSTED_BY = "posted_by";

    public Announcement() {
    }


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
        Task<Institution> institutionTask =
                Objects.requireNonNull(getParseObject(KEY_INSTITUTION)).fetchIfNeededInBackground();
        try {
            institutionTask.waitForCompletion(MAX_DURATION, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Institution institution = null;
        if (institutionTask.isCompleted() && institutionTask.getError() == null) {
            institution = institutionTask.getResult();
        } else {
            Log.e(TAG, "Exception", institutionTask.getError());
        }
        return institution;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDateTime getCreatedAtAsLocalDateTime() {
        return DateUtils.localDateTimeFromDate(getCreatedAt());
    }

    public String getPostedBy() {
        return getString(KEY_POSTED_BY);
    }
}
