package com.erastus.orientate.student.login.models;

import androidx.annotation.Nullable;

public class LoginResult {
    @Nullable
    private String errorMessage;

    public LoginResult(@Nullable String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LoginResult() {}

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }
}