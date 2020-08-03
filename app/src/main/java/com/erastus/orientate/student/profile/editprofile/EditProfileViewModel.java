package com.erastus.orientate.student.profile.editprofile;

import androidx.lifecycle.ViewModel;

import com.erastus.orientate.applications.App;
import com.erastus.orientate.models.ExtendedParseUser;

public class EditProfileViewModel extends ViewModel {

    private ExtendedParseUser mCurrentUser;
    private EditProfileRepository mRepository;


    public EditProfileViewModel() {
        mCurrentUser = App.get().getCurrentUser();
        mRepository = EditProfileRepository.getInstance();
    }


    public ExtendedParseUser getCurrentUser() {
        return mCurrentUser;
    }
}
