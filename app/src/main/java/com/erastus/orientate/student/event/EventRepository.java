package com.erastus.orientate.student.event;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.erastus.orientate.student.event.models.Event;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.utils.DateUtils;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class EventRepository {
    private static final String TAG = "EventRepository";
    private static volatile EventRepository instance;

    private MutableLiveData<DataState> mState = new MutableLiveData<>();
    private MutableLiveData<List<Event>> mEventsDataSet = new MutableLiveData<>(new ArrayList<>());
    public static final int PAGE_ZERO = 0;

    // private constructor : singleton access

    public static synchronized EventRepository getInstance() {
        if (instance == null) {
            instance = new EventRepository();
        }
        return instance;
    }

    public LiveData<List<Event>> getEvents(int maxNumber) {
        loadEvents(maxNumber);
        return mEventsDataSet;
    }

    public LiveData<DataState> getState() {
        return mState;
    }

    public void loadEvents(Integer maxNumber) {
        loadEvents(maxNumber, PAGE_ZERO);
    }

    public void loadEvents(Integer maxNumber, Integer pageNumber) {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.setLimit(maxNumber);
        query.include(Event.KEY_INSTITUTION);
        query.orderByDescending(Event.KEY_STARTING_ON);
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

    public void loadEventsSpecificDate(Integer maxNumber, Calendar c) {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.setLimit(maxNumber);
        query.orderByDescending(Event.KEY_STARTING_ON);
        query.include(Event.KEY_INSTITUTION);

        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        query.whereGreaterThan(Event.KEY_STARTING_ON, c.getTime());
        c.add(Calendar.DATE, 1);
        query.whereLessThan(Event.KEY_STARTING_ON, c.getTime());

        query.findInBackground((events, e) -> {
            mState.setValue(new DataState.Error(new Exception("Fake Exception")));
//            if (e == null) {
//                mEventsDataSet.setValue(events);
//                mState.setValue(new DataState.Success<>(events));
//            } else {
//                mState.setValue(new DataState.Error(e));
//            }
        });
    }
}
