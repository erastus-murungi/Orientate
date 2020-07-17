package com.erastus.orientate.student.event;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EventViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private static final int DAYS_OF_WEEK = 7;
    private MutableLiveData<String> mDate;

    public int getNumPages() {
        return DAYS_OF_WEEK;
    }
}