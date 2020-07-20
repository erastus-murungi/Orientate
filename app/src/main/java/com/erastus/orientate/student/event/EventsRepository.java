package com.erastus.orientate.student.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.erastus.orientate.student.event.models.Event;
import com.erastus.orientate.student.models.DataState;
import com.parse.ParseQuery;

import java.util.List;

public class EventsRepository {
    private static volatile EventsRepository instance;

    private MutableLiveData<DataState> mState = new MutableLiveData<>();
    private MutableLiveData<List<Event>> mEventsDataSet = new MutableLiveData<>();
    public static final int PAGE_ZERO = 0;

    // private constructor : singleton access

    public static synchronized EventsRepository getInstance() {
        if (instance == null) {
            instance = new EventsRepository();
        }
        return instance;
    }

    public LiveData<List<Event>> fetchEvents(int maxNumber) {
        loadEvents(maxNumber);
        return mEventsDataSet;
    }

    public void loadEvents(Integer maxNumber) {
        loadEvents(maxNumber, PAGE_ZERO);
    }

    public void loadEvents(Integer maxNumber, Integer pageNumber) {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.setLimit(maxNumber);
        query.findInBackground((events, e) -> {
//            mState.setValue(new DataState.Error(new Exception("Fake Exception")));
            if (e == null) {
                List<Event> l = mEventsDataSet.getValue();
                assert l != null;
                l.clear();
                l.addAll(events);
                mEventsDataSet.setValue(l);
                mState.setValue(new DataState.Success<>(events));
            } else {
                mState.setValue(new DataState.Error(e));
            }
        });
    }
}
