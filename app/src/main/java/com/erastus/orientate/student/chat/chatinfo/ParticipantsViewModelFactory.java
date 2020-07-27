package com.erastus.orientate.student.chat.chatinfo;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.erastus.orientate.student.chat.conversations.models.Conversation;

public class ParticipantsViewModelFactory implements ViewModelProvider.Factory {

    Conversation mConversation;
    ParticipantsViewModelFactory(Conversation conversation) {
        mConversation = conversation;
    }
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ParticipantsViewModel.class)) {
            return (T) new ParticipantsViewModel(mConversation);
        } else {
            throw  new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
