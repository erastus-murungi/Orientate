package com.erastus.orientate.student.announcements;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.erastus.orientate.student.announcements.models.Announcement;
import com.erastus.orientate.student.announcements.models.AnnouncementState;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.student.announcements.models.LocalAnnouncement;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementViewModel extends ViewModel {
    private AnnouncementRepository announcementRepository;
    private MutableLiveData<AnnouncementState> mAnnouncementState = new MutableLiveData<>(new AnnouncementState(true));

    private MutableLiveData<List<LocalAnnouncement>> mAnnouncements = new MutableLiveData<>(new ArrayList<>());

    public AnnouncementViewModel(AnnouncementRepository instance) {
        this.announcementRepository = instance;
        DataState<MutableLiveData<List<Announcement>>> state = this.announcementRepository.getAnnouncements();

        if (state instanceof DataState.Error) {
            mAnnouncementState.setValue(new
                    AnnouncementState(((DataState.Error) state).getError().getMessage()));
        } else if (state instanceof DataState.Success){
            MutableLiveData<List<Announcement>> liveData = ((DataState.Success<MutableLiveData<List<Announcement>>>) state).getData();
            mAnnouncements = new MutableLiveData
                    (liveData.getValue().stream().map(LocalAnnouncement::localAnnouncementFromParseAnnouncement));
        }
    }

    public LiveData<List<LocalAnnouncement>> getAnnouncements() {
        return mAnnouncements;
    }

    public LiveData<AnnouncementState> getState() {
        return mAnnouncementState;
    }

    public void requestMoreAnnouncements() {
        DataState<MutableLiveData<List<Announcement>>> state = this.announcementRepository.loadMore();
    }
}