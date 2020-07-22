package com.erastus.orientate.student.navigation;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.erastus.orientate.models.GenericUser;
import com.parse.ParseUser;

public class StudentNavViewModel extends ViewModel {

    /**
     * To make it easier to manipulate the action bar from within the EventDetailFragment
     */
    private MutableLiveData<ActionBarStatus> mStatus = new MutableLiveData<>();

    private MutableLiveData<GenericUser> mUser =
            new MutableLiveData<>(new GenericUser(ParseUser.getCurrentUser()));

    public MutableLiveData<GenericUser> getUser() {
        return mUser;
    }

    public MutableLiveData<ActionBarStatus> getActionBarStatus() {
        return mStatus;
    }
}
