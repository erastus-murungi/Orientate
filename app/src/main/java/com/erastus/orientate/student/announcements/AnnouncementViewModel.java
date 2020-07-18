package com.erastus.orientate.student.announcements;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.erastus.orientate.student.announcements.models.Announcement;
import com.erastus.orientate.student.announcements.models.AnnouncementRequestResult;
import com.erastus.orientate.student.announcements.models.LocalAnnouncement;
import com.erastus.orientate.student.models.DataState;

import java.util.List;
import java.util.stream.Collectors;

public class AnnouncementViewModel extends ViewModel {
    public static final int MAX_NUMBER_OF_ANNOUNCEMENTS_TO_FETCH = 20;
    private AnnouncementRepository mAnnouncementRepository;

    @Nullable
    private LiveData<AnnouncementRequestResult> mAnnouncementState;

    private LiveData<List<LocalAnnouncement>> mAnnouncements;

    @SuppressWarnings("unchecked")
    public AnnouncementViewModel(AnnouncementRepository announcementRepository) {
        this.mAnnouncementRepository = announcementRepository;
        MutableLiveData<List<Announcement>> announcements =
                announcementRepository.getAnnouncements(MAX_NUMBER_OF_ANNOUNCEMENTS_TO_FETCH);

        this.mAnnouncements = Transformations.switchMap(announcements, input ->
                new MutableLiveData<>(fromParseAnnouncementsListToLocalAnnouncementsList(input)));
        MutableLiveData<DataState> state = announcementRepository.getState();

        this.mAnnouncementState = Transformations.switchMap(state, dataState -> {
            if (dataState instanceof DataState.Success) {
                return new MutableLiveData<>(new AnnouncementRequestResult(false));
            } else if (dataState instanceof DataState.Error) {
                return new MutableLiveData<>(new AnnouncementRequestResult(
                        ((DataState.Error) dataState).getError().getMessage()
                ));
            }
            return new MutableLiveData<>();
        });

    }

    private List<LocalAnnouncement> fromParseAnnouncementsListToLocalAnnouncementsList(
            List<Announcement> announcements) {
        return announcements
                .stream()
                .map(LocalAnnouncement::localAnnouncementFromParseAnnouncement)
                .collect(Collectors.toList());
    }

    public LiveData<List<LocalAnnouncement>> getAnnouncements() {
        return mAnnouncements;
    }

    @NonNull
    public LiveData<AnnouncementRequestResult> getState() {
        assert mAnnouncementState != null;
        return mAnnouncementState;
    }

    public void requestMoreAnnouncements() {
        mAnnouncementRepository.loadAnnouncements(MAX_NUMBER_OF_ANNOUNCEMENTS_TO_FETCH);
    }

    public void requestReload() {
        mAnnouncementRepository.loadAnnouncements(MAX_NUMBER_OF_ANNOUNCEMENTS_TO_FETCH);
    }
}