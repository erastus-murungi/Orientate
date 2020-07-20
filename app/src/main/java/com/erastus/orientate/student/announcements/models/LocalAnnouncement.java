package com.erastus.orientate.student.announcements.models;

import com.erastus.orientate.institution.models.LocalInstitution;
import com.erastus.orientate.student.models.Urgency;

import java.time.LocalDateTime;

public class LocalAnnouncement {
    private Urgency mUrgencyLevel;
    private String mTitle;
    private String mBody;
    private String mUrl;
    private LocalInstitution mOwnerInstitution;
    private LocalDateTime mCreatedAt;
    private String mPostedBy;

    public LocalAnnouncement(Announcement announcement) {
        this.mUrgencyLevel = announcement.getUrgencyLevel();
        this.mTitle = announcement.getTitle();
        this.mBody = announcement.getBody();
        this.mUrl = announcement.getUrl();
        this.mOwnerInstitution =
                new LocalInstitution(announcement.getOwnerInstitution());
        this.mCreatedAt = announcement.getCreatedAtAsLocalDateTime();
        this.mPostedBy = announcement.getPostedBy();
    }

    public Urgency getUrgencyLevel() {
        return mUrgencyLevel;
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
