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
import java.util.Objects;
import java.util.stream.Collectors;

public class AnnouncementViewModel extends ViewModel {
    private AnnouncementRepository announcementRepository;
    private MutableLiveData<AnnouncementState> mAnnouncementState =
            new MutableLiveData<>(new AnnouncementState(true));

    private MutableLiveData<List<LocalAnnouncement>> mAnnouncements =
            new MutableLiveData<>(new ArrayList<>());

    public AnnouncementViewModel(AnnouncementRepository instance) {
        this.announcementRepository = instance;
        getAnnouncementsOnce();
    }

    public LiveData<List<LocalAnnouncement>> getAnnouncements() {
        return mAnnouncements;
    }

    public LiveData<AnnouncementState> getState() {
        return mAnnouncementState;
    }

    public void requestMoreAnnouncements() {
        mAnnouncementState.setValue(new AnnouncementState(true));
        DataState<List<Announcement>> state = this.announcementRepository.loadMore();

        if (state instanceof DataState.Error) {
            mAnnouncementState.setValue(new
                    AnnouncementState(((DataState.Error) state).getError().getMessage()));
        } else if (state instanceof DataState.TimedOut) {
            mAnnouncementState.setValue(new AnnouncementState(((DataState.TimedOut) state).getMaxDuration()));
        } else if (state instanceof DataState.Success) {
            List<Announcement> announcementList = ((DataState.Success<List<Announcement>>) state).getData();

            Objects.requireNonNull(mAnnouncements.getValue()).addAll(announcementList
                    .stream()
                    .map(LocalAnnouncement::localAnnouncementFromParseAnnouncement)
                    .collect(Collectors.toList()));
            mAnnouncements.setValue(mAnnouncements.getValue());
        }
        mAnnouncementState.setValue(new AnnouncementState(false));
    }

    public void requestReload() {
        getAnnouncementsOnce();
    }

    private void getAnnouncementsOnce() {
        mAnnouncementState.setValue(new AnnouncementState(true));
        DataState<List<Announcement>> state = this.announcementRepository.getAnnouncements();

        if (state instanceof DataState.Error) {
            mAnnouncementState.setValue(new
                    AnnouncementState(((DataState.Error) state).getError().getMessage()));
        } else if (state instanceof DataState.TimedOut) {
            mAnnouncementState.setValue(new AnnouncementState(((DataState.TimedOut) state).getMaxDuration()));
        } else if (state instanceof DataState.Success) {
            List<Announcement> announcementList = ((DataState.Success<List<Announcement>>) state).getData();

            mAnnouncements.setValue(announcementList
                    .stream()
                    .map(LocalAnnouncement::localAnnouncementFromParseAnnouncement)
                    .collect(Collectors.toList()));
        }
        mAnnouncementState.setValue(new AnnouncementState(false));

    }
}