package com.erastus.orientate.student.event;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;


import com.erastus.orientate.student.event.models.Event;
import com.erastus.orientate.student.event.models.EventResult;
import com.erastus.orientate.student.event.models.LocalEvent;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.utils.DateUtils;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class EventViewModel extends ViewModel {
    private static final String TAG = "EventViewModel";
    public static final int MAX_NUMBER_OF_ANNOUNCEMENTS_TO_FETCH = 40;
    private LiveData<Map<String, Set<LocalEvent>>> mEvents;
    private LiveData<EventResult> mEventsResult;
    private EventRepository mRepository;

    public EventViewModel() {
        this.mRepository = EventRepository.getInstance();
        LiveData<List<Event>> events =
                mRepository.getEvents(MAX_NUMBER_OF_ANNOUNCEMENTS_TO_FETCH);

        this.mEvents = Transformations.switchMap(events, eventsFromRepo ->
                new MutableLiveData<>(clusterEventsByTimes(LocalEvent.getLocalEventsSet(eventsFromRepo))));

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
        return Objects.requireNonNull(Objects.requireNonNull(mEvents
                .getValue())
                .get(DateUtils.getTimeAmPm(localDateTime)))
                        .stream()
                        .filter(localEvent -> localEvent.getStartingOn().getHour() == localDateTime.getHour())
                        .collect(Collectors.toList());
    }

    public LiveData<Map<String, Set<LocalEvent>>> getEvents() {
        return mEvents;
    }


    public Map<String, Set<LocalEvent>> clusterEventsByTimes(@NonNull Set<LocalEvent> events) {
        HashMap<String, Set<LocalEvent>> map = new HashMap<>();
        for (LocalEvent event: events) {
            String startTime = DateUtils.getTimeAmPm(event.getStartingOn());
            if (map.containsKey(startTime)) {
                Objects.requireNonNull(map.get(startTime)).add(event);
            } else {
                Set<LocalEvent> e = new HashSet<>();
                e.add(event);
                map.put(startTime, e);
            }
        }
        return map;
    }

    public void reload(Calendar c) {
        mRepository.loadEventsSpecificDate(MAX_NUMBER_OF_ANNOUNCEMENTS_TO_FETCH, c);
    }
}