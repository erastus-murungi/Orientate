package com.erastus.orientate.student.announcements.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.erastus.orientate.institution.models.LocalInstitution;
import com.erastus.orientate.student.models.Urgency;

import java.time.LocalDateTime;

public class LocalAnnouncement {
    public static final String TAG = "LocalAnnouncement";
    private Urgency mUrgency_level;
    private String mTitle;
    private String mBody;
    private String mUrl;
    private LocalInstitution mOwnerInstitution;
    private LocalDateTime mCreatedAt;
    private String mPostedBy;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalAnnouncement localAnnouncementFromParseAnnouncement(Announcement announcement) {
        LocalAnnouncement localAnnouncement = new LocalAnnouncement();
        localAnnouncement.mUrgency_level = announcement.getUrgencyLevel();
        localAnnouncement.mTitle = announcement.getTitle();
        localAnnouncement.mBody = announcement.getBody();
        localAnnouncement.mUrl = announcement.getUrl();
        localAnnouncement.mOwnerInstitution =
                LocalInstitution.localInstitutionFromParseInstitution(announcement.getOwnerInstitution());
        localAnnouncement.mCreatedAt = announcement.getCreatedAtAsLocalDateTime();
        localAnnouncement.mPostedBy = announcement.getPostedBy();
        return localAnnouncement;
    }

    public Urgency getUrgency_level() {
        return mUrgency_level;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getBody() {
        return mBody;
    }

    public String getUrl() {
        return mUrl;
    }

    public LocalInstitution getOwner() {
        return mOwnerInstitution;
    }

    public LocalDateTime getCreatedAt() {
        return mCreatedAt;
    }

    public String getPostedBy() {
        return mPostedBy;
    }
}
