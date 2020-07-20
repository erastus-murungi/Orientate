package com.erastus.orientate.student.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.erastus.orientate.student.event.models.EventResult;

public class EventViewModel extends ViewModel {
    private MutableLiveData<EventResult> mEventsResult = new MutableLiveData<>();

    LiveData<EventResult> getEventsExist() {
        return mEventsResult;
    }

}