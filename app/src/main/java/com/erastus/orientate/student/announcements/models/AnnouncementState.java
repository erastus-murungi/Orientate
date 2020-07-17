package com.erastus.orientate.student.announcements.models;

import androidx.annotation.Nullable;

import java.time.LocalDateTime;

public class AnnouncementState {
    @Nullable
    private String errorMessage;

    private boolean isLoading;

    public AnnouncementState(@Nullable String errorMessage) {
        this.errorMessage = errorMessage;
        isLoading = false;
    }

    public AnnouncementState(boolean isLoading) {
        this.isLoading = isLoading;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isLoading() {
        return isLoading;
    }
}
