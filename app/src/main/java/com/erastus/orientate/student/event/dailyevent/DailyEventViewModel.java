package com.erastus.orientate.student.event.dailyevent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.erastus.orientate.student.event.models.Event;

import java.util.List;

public class DailyEventViewModel extends ViewModel {

    private MutableLiveData<List<Event>> mEvents = new MutableLiveData<>();

    public LiveData<List<Event>> getEvents() {return mEvents;}

}
