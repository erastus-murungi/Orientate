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

public class AnnouncementViewModel extends ViewModel {
    public static final int MAX_NUMBER_OF_ANNOUNCEMENTS_TO_FETCH = 20;
    private AnnouncementRepository mAnnouncementRepository;

    @Nullable
    private LiveData<AnnouncementRequestResult> mAnnouncementState;

    private LiveData<List<LocalAnnouncement>> mAnnouncements;

    @SuppressWarnings("unchecked")
    public AnnouncementViewModel(AnnouncementRepository announcementRepository) {
        this.mAnnouncementRepository = announcementRepository;
        LiveData<List<Announcement>> announcements =
                announcementRepository.getAnnouncements(MAX_NUMBER_OF_ANNOUNCEMENTS_TO_FETCH);

        this.mAnnouncements = Transformations.switchMap(announcements, input ->
                new MutableLiveData<>(LocalAnnouncement.toLocalAnnouncementsList(input)));
        MutableLiveData<DataState> announcementRequestState = announcementRepository.getState();

        this.mAnnouncementState = Transformations.switchMap(announcementRequestState, dataState -> {
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