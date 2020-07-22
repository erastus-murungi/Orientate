package com.erastus.orientate.student.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.parse.ParseUser;

public class ProfileViewModel extends ViewModel {

    public void logout() {
        ParseUser.logOut();
    }

    private MutableLiveData<Boolean> mShouldLogOut = new MutableLiveData<>(false);

    public LiveData<Boolean> getShouldLogOut() {
        return mShouldLogOut;
    }

    public void notifyEventOptionSelected(ProfileOption option) {
        switch (option) {
            case LOG_OUT:
                logout();
                // notify the main activity to end
                mShouldLogOut.setValue(true);
        }
    }
}