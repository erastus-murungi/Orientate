package com.erastus.orientate.student.event.eventdetail;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.erastus.orientate.student.event.models.LocalEvent;

class EventDetailsViewModelFactory implements ViewModelProvider.Factory {
    LocalEvent mLocalEvent;

    EventDetailsViewModelFactory(LocalEvent event) {
        mLocalEvent = event;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(EventDetailsViewModel.class)) {
            return (T) new EventDetailsViewModel(mLocalEvent);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
