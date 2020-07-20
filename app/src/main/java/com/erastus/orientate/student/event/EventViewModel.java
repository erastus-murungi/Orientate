package com.erastus.orientate.student.event;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;


import com.erastus.orientate.student.event.models.Event;
import com.erastus.orientate.student.event.models.EventResult;
import com.erastus.orientate.student.event.models.LocalEvent;
import com.erastus.orientate.student.models.DataState;

import java.util.List;

public class EventViewModel extends ViewModel {
    public static final int MAX_NUMBER_OF_ANNOUNCEMENTS_TO_FETCH = 40;
    private LiveData<List<LocalEvent>> mEventsList;
    private LiveData<EventResult> mEventsResult;
    private EventRepository mRepository;

    LiveData<EventResult> getEventsExist() {
        return mEventsResult;
    }

    public EventViewModel(EventRepository eventRepository) {
        this.mRepository = eventRepository;
        LiveData<List<Event>> events =
                eventRepository.getEvents(MAX_NUMBER_OF_ANNOUNCEMENTS_TO_FETCH);

        this.mEventsList = Transformations.switchMap(events,
                input -> new MutableLiveData<>(LocalEvent.getLocalEventsList(input)));

        LiveData<DataState> eventsRequestResult = mRepository.getState();

        this.mEventsResult = Transformations.switchMap(eventsRequestResult, dataState -> {
            if (dataState instanceof DataState.Success) {
                return new MutableLiveData<>(new EventResult(false));
            } else if (dataState instanceof DataState.Error) {
                return new MutableLiveData<>(new EventResult(
                        ((DataState.Error) dataState).getError().getMessage()
                ));
            }
            return new MutableLiveData<>();
        });
    }


    public LiveData<EventResult> getEventsResult() {
        return mEventsResult;
    }

    public LiveData<List<LocalEvent>> getEvents() {
        return mEventsList;
    }
}