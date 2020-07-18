package com.erastus.orientate.student.announcements.models;

import androidx.annotation.Nullable;

public class AnnouncementRequestResult {
    @Nullable
    private String errorMessage;

    private boolean isLoading;

    public AnnouncementRequestResult(@Nullable String errorMessage) {
        this.errorMessage = errorMessage;
        isLoading = false;
    }

    public AnnouncementRequestResult(boolean isLoading) {
        this.isLoading = isLoading;
        this.errorMessage = null;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isLoading() {
        return isLoading;
    }
}
