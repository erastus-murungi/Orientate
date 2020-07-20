package com.erastus.orientate.student.event.eventdetail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.erastus.orientate.student.event.models.LocalEvent;

public class EventDetailsViewModel extends ViewModel {
    private MutableLiveData<LocalEvent> mLocalEvent;


    public EventDetailsViewModel(LocalEvent event) {
        mLocalEvent = new MutableLiveData<>(event);
    }

    public MutableLiveData<LocalEvent> getLocalEvent() {
        return mLocalEvent;
    }
}
