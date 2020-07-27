package com.erastus.orientate.student.signup.name;

import androidx.annotation.Nullable;


/** Data validation state for the first part of the login form*/
public class SignUpFormState {

    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer fullNameError;
    private boolean isDataValid;

    public SignUpFormState(@Nullable Integer usernameError,
                           @Nullable Integer passwordError,
                           @Nullable Integer fullNameError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.fullNameError = fullNameError;
        this.isDataValid = false;
    }

    @Nullable
    public Integer getFullNameError() {
        return fullNameError;
    }

    public SignUpFormState(boolean isDataValid) {
        this.usernameError = null;
        this.passwordError = null;
        this.fullNameError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    public Integer getPasswordError() {
        return passwordError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
