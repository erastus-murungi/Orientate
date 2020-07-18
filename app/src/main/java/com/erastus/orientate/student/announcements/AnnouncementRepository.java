package com.erastus.orientate.student.announcements;

import androidx.lifecycle.MutableLiveData;

import com.erastus.orientate.student.announcements.models.Announcement;
import com.erastus.orientate.student.models.DataState;
import com.parse.ParseQuery;
import com.parse.boltsinternal.Task;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * singleton pattern
 */
public class AnnouncementRepository {

    public static long maxDuration = 50;
    private static AnnouncementRepository instance;

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

    public DataState<List<Announcement>> getAnnouncements() {
        ParseQuery<Announcement> query = ParseQuery.getQuery(Announcement.class);
        Task<List<Announcement>> listTask = query.findInBackground();

        try {
            listTask.waitForCompletion(maxDuration, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (listTask.isCompleted() && listTask.getError() == null) {
            return new DataState.Success<>(listTask.getResult());
        } if (!listTask.isCompleted() && listTask.getError() == null) {
            return new DataState.TimedOut(maxDuration);
        }
        return new DataState.Error(listTask.getError());
    }


    public DataState<List<Announcement>> loadMore() {
        return null;
    }
}
