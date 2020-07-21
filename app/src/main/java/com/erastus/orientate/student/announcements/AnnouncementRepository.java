package com.erastus.orientate.student.announcements;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.erastus.orientate.student.announcements.models.Announcement;
import com.erastus.orientate.student.models.DataState;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * singleton pattern
 */
public class AnnouncementRepository {
    private static final Integer PAGE_ZERO = 0;
    private static volatile AnnouncementRepository instance;

    private MutableLiveData<DataState> mState = new MutableLiveData();
    private MutableLiveData<List<Announcement>> mDataSet = new MutableLiveData<>(new ArrayList<>());

    /**
     * By adding the keyword synchronized, we ensure that we still get one instance even in
     * a multithreaded environment
     */

    public static synchronized AnnouncementRepository getInstance() {
        if (instance == null) {
            instance = new AnnouncementRepository();
        }
        return instance;
    }

    public MutableLiveData<DataState> getState() {
        return mState;
    }

    public LiveData<List<Announcement>> getAnnouncements(Integer maxNumber) {
        loadAnnouncements(maxNumber);
        return mDataSet;
    }

    public void loadAnnouncements(Integer maxNumber, Integer pageNumber) {
        ParseQuery<Announcement> query = ParseQuery.getQuery(Announcement.class);
        query.setLimit(maxNumber);
        query.findInBackground((announcements, e) -> {
//            mState.setValue(new DataState.Error(new Exception("Fake Exception")));
            if (e == null) {
                mDataSet.setValue(announcements);
                mState.setValue(new DataState.Success<>(announcements));
            } else {
                mState.setValue(new DataState.Error(e));
            }
        });
    }

    public void loadAnnouncements(Integer maxNumber) {
        loadAnnouncements(maxNumber, PAGE_ZERO);
    }

}
