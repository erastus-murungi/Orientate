package com.erastus.orientate.student.event.eventdetail;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.erastus.orientate.student.event.models.Event;

class EventDetailsViewModelFactory implements ViewModelProvider.Factory {
    Event mEvent;

    EventDetailsViewModelFactory(Event event) {
        mEvent = event;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(EventDetailsViewModel.class)) {
            return (T) new EventDetailsViewModel(mEvent);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
