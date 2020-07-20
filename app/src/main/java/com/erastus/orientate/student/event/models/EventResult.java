package com.erastus.orientate.student.event.models;

import androidx.annotation.Nullable;

public class EventResult {

    @Nullable
    private Boolean isLoading;

    @Nullable
    private Boolean eventsExist;

    @Nullable
    private String errorMessage;

    public EventResult(@Nullable Boolean isLoading) {
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
