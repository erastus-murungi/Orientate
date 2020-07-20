package com.erastus.orientate.student.event.models;

import androidx.annotation.Nullable;

public class EventResult {

    public boolean isLoading() {
        return isLoading;
    }

    @Nullable
    public Boolean getEventsExist() {
        return eventsExist;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    @Nullable
    private Boolean isLoading;

    @Nullable
    private Boolean eventsExist;

    @Nullable
    private String errorMessage;

    public EventResult(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public EventResult(@Nullable String errorMessage) {
        this.errorMessage = errorMessage;
        isLoading = false;
        eventsExist = false;
    }

    public EventResult(@Nullable Boolean isLoading,
                       @Nullable Boolean eventsExist,
                       @Nullable String errorMessage) {
        this.isLoading = isLoading;
        this.eventsExist = eventsExist;
        this.errorMessage = errorMessage;
    }
}
