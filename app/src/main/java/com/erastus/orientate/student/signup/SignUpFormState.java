package com.erastus.orientate.student.signup;

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
    @Nullable
    private Integer dateOfBirthError;

    @Nullable
    public Integer getDateOfBirthError() {
        return dateOfBirthError;
    }

    public void setDateOfBirthError(@Nullable Integer dateOfBirthError) {
        this.dateOfBirthError = dateOfBirthError;
    }

    @Nullable
    public Integer getProfilePictureError() {
        return profilePictureError;
    }

    public void setProfilePictureError(@Nullable Integer profilePictureError) {
        this.profilePictureError = profilePictureError;
    }

    @Nullable
    private Integer profilePictureError;


    public SignUpFormState(@Nullable Integer usernameError,
                           @Nullable Integer passwordError,
                           @Nullable Integer fullNameError, @Nullable Integer dateOfBirthError, @Nullable Integer profilePictureError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.fullNameError = fullNameError;
        this.dateOfBirthError = dateOfBirthError;
        this.profilePictureError = profilePictureError;
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
