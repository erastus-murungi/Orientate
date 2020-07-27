package com.erastus.orientate.student.signup.name;

import androidx.annotation.Nullable;

public class InputValid {

    @Nullable Integer errorCode;

    @Nullable String errorString;

    boolean dataValid = false;

    public InputValid(@Nullable String errorString) {
        this.errorString = errorString;
    }

    public InputValid(@Nullable Integer errorCode) {
        this.errorCode = errorCode;
    }

    public InputValid(boolean dataValid) {
        this.dataValid = dataValid;
    }

    @Nullable
    public Integer getErrorCode() {
        return errorCode;
    }

    @Nullable
    public String getErrorString() {
        return errorString;
    }

    public boolean isDataValid() {
        return dataValid;
    }
}
