package com.erastus.orientate.student.announcements.models;

import androidx.annotation.Nullable;

public class AnnouncementState {
    @Nullable
    private String errorMessage;

    private boolean isLoading;

    @Nullable
    private Long timedOut;

    public AnnouncementState(@Nullable String errorMessage) {
        this.errorMessage = errorMessage;
        this.timedOut = null;
        isLoading = false;
    }

    public AnnouncementState(boolean isLoading) {
        this.timedOut = null;
        this.isLoading = isLoading;
        this.errorMessage = null;
    }

    public AnnouncementState(@Nullable Long timedOut) {
        this.timedOut = timedOut;
        this.errorMessage = null;
        this.isLoading = false;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isLoading() {
        return isLoading;
    }

    @Nullable
    public Long getTimedOut() {
        return timedOut;
    }
}
