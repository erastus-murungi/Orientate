package com.erastus.orientate.student.announcements.models;

import com.erastus.orientate.institution.models.Institution;
import com.erastus.orientate.student.models.Urgency;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class LocalAnnouncement {
    private Urgency mUrgencyLevel;
    private String mTitle;
    private String mBody;
    private String mUrl;
    private Institution mOwnerInstitution;
    private LocalDateTime mCreatedAt;
    private String mPostedBy;

    public LocalAnnouncement(Announcement announcement) {
        this.mUrgencyLevel = announcement.getUrgencyLevel();
        this.mTitle = announcement.getTitle();
        this.mBody = announcement.getBody();
        this.mUrl = announcement.getUrl();
        this.mOwnerInstitution = announcement.getOwnerInstitution();
        this.mCreatedAt = announcement.getCreatedAtAsLocalDateTime();
        this.mPostedBy = announcement.getPostedBy();
    }

    public static List<LocalAnnouncement> toLocalAnnouncementsList(
            List<Announcement> announcements) {
        return announcements
                .stream()
                .map(LocalAnnouncement::new)
                .collect(Collectors.toList());
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

    public Institution getOwner() {
        return mOwnerInstitution;
    }

    public LocalDateTime getCreatedAt() {
        return mCreatedAt;
    }

    public String getPostedBy() {
        return mPostedBy;
    }



}
