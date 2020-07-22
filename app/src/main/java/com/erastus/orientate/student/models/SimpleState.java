package com.erastus.orientate.student.models;

import androidx.annotation.Nullable;

public class SimpleState<T> {
    private boolean isLoading;

    @Nullable
    private String errorMessage;

    @Nullable
    private Integer errorCode;

    @Nullable
    private T data;

    @Nullable
    private Boolean internalErrorOccurred;

    /**
     * @param isLoading If data is loading then everything is null
     */
    public SimpleState(boolean isLoading) {
        this.isLoading = isLoading;
        this.errorMessage = null;
        this.errorCode = null;
        this.data = null;
        this.internalErrorOccurred = null;
    }

    public SimpleState(@Nullable String errorMessage) {
        this.isLoading = false;
        this.errorMessage = errorMessage;
        this.errorCode = null;
        this.data = null;
        this.internalErrorOccurred = null;
    }

    public SimpleState(@Nullable String errorMessage, @Nullable Integer errorCode) {
        this.isLoading = false;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.data = null;
        this.internalErrorOccurred = null;
    }

    /** Full Success */
    public SimpleState(@Nullable T data) {
        this.isLoading = false;
        this.errorMessage = null;
        this.errorCode = null;
        this.data = data;
        this.internalErrorOccurred = null;
    }

    /** Partial success */
    public SimpleState(@Nullable String errorMessage,
                       @Nullable String errorCode,
                       @Nullable T data) {
        this.isLoading = false;
        this.errorMessage = null;
        this.errorCode = null;
        this.data = data;
        this.internalErrorOccurred = null;
    }

    public SimpleState(@Nullable Boolean internalErrorOccurred) {
        this.internalErrorOccurred = internalErrorOccurred;
        this.isLoading = false;
        this.errorMessage = null;
        this.errorCode = null;
        this.data = null;
    }

    public boolean isLoading() {
        return isLoading;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    @Nullable
    public Integer getErrorCode() {
        return errorCode;
    }

    @Nullable
    public T getData() {
        return data;
    }

    @Nullable
    public Boolean getInternalErrorOccurred() {
        return internalErrorOccurred;
    }
}
