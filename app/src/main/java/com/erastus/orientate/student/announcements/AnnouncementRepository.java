package com.erastus.orientate.student.announcements;

import androidx.lifecycle.MutableLiveData;

import com.erastus.orientate.student.announcements.models.Announcement;
import com.erastus.orientate.student.announcements.models.DataState;
import com.parse.ParseQuery;
import com.parse.boltsinternal.Task;

import java.util.List;

/**
 * singleton pattern
 */
public class AnnouncementRepository {
    private static AnnouncementRepository instance;

    /**
     * By adding the keyword synchronized, we esnure that we still get one instance even in
     * a multithreaded environment
     */

    public static synchronized AnnouncementRepository getInstance() {
        if (instance == null) {
            instance = new AnnouncementRepository();
        }
        return instance;
    }

    public DataState<MutableLiveData<List<Announcement>>> getAnnouncements() {
        ParseQuery<Announcement> query = ParseQuery.getQuery(Announcement.class);
        Task<List<Announcement>> task = query.findInBackground();
        if (task.isCompleted()) {
            return new DataState.Success<>(new MutableLiveData<>(task.getResult()));
        }
        return new DataState.Error(task.getError());
    }


    public DataState<MutableLiveData<List<Announcement>>> loadMore() {
        return null;
    }
}
