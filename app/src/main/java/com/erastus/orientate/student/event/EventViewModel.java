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

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EventViewModel extends ViewModel {
    public static final int MAX_NUMBER_OF_ANNOUNCEMENTS_TO_FETCH = 40;
    private LiveData<List<LocalEvent>> mEventsList;
    private LiveData<EventResult> mEventsResult;
    private EventRepository mRepository;

    public EventViewModel() {
        this.mRepository = EventRepository.getInstance();
        LiveData<List<Event>> events =
                mRepository.getEvents(MAX_NUMBER_OF_ANNOUNCEMENTS_TO_FETCH);

        this.mEventsList = Transformations.switchMap(events,
                input -> new MutableLiveData<>(LocalEvent.getLocalEventsList(input)));

        LiveData<DataState> eventsRequestResult = mRepository.getState();

        this.mEventsResult = Transformations.switchMap(eventsRequestResult, dataState -> {
            if (dataState instanceof DataState.Success) {
                if (((DataState.Success<List<Event>>) dataState).getData().size() == 0) {
                    return new MutableLiveData<>(new EventResult(false, false, null));
                }
                return new MutableLiveData<>(new EventResult(false, true, null));
            } else if (dataState instanceof DataState.Error) {
                return new MutableLiveData<>(new EventResult(
                        ((DataState.Error) dataState).getError().getMessage()
                ));
            }
            return new MutableLiveData<>();
        });
    }

    public void requestEventsSpecificDate(Calendar calendar) {
        mRepository.loadEventsSpecificDate(MAX_NUMBER_OF_ANNOUNCEMENTS_TO_FETCH, calendar);

    }

    public LiveData<EventResult> getEventsResult() {
        return mEventsResult;
    }

    public List<LocalEvent> getEventsSpecificTime(LocalDateTime localDateTime) {
        return Objects.requireNonNull(mEventsList
                        .getValue())
                        .stream()
                        .filter(localEvent -> localEvent.getStartingOn() == localDateTime)
                        .collect(Collectors.toList());
    }

    public LiveData<List<LocalEvent>> getEvents() {
        return mEventsList;
    }

    public void reload() {

    }
}