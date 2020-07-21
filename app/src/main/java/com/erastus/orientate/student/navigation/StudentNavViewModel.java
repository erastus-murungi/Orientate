package com.erastus.orientate.student.navigation;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StudentNavViewModel extends ViewModel {

    /**
     * To make it easier to manipulate the action bar from within the EventDetailFragment
     */
    private MutableLiveData<ActionBarStatus> mStatus = new MutableLiveData<>();

    public MutableLiveData<ActionBarStatus> getActionBarStatus() {
        return mStatus;
    }
}
