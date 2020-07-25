package com.erastus.orientate.student.chat.chatmessages;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.erastus.orientate.student.chat.conversations.models.Conversation;

public class ChatViewModelFactory implements ViewModelProvider.Factory {

    private Conversation mConversation;

    ChatViewModelFactory(Conversation conversation) {
        mConversation =  conversation;
    }
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ChatViewModel.class)) {
            return (T) new ChatViewModel(mConversation);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
