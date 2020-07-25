package com.erastus.orientate.student.chat.chatmessages;

import android.view.View;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.erastus.orientate.student.chat.chatmessages.models.ChatMessage;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.student.models.SimpleState;

import java.util.List;

public class ChatViewModel extends ViewModel {

    private Conversation mConversation;

    private LiveData<SimpleState<List<ChatMessage>>> mListChatMessagesSimpleState;
    private SimpleState<Boolean> mMessageSent;
    private ChatMessageRepository mRepository;
    private MutableLiveData<Boolean> mButtonPressState;

    @SuppressWarnings("unchecked")
    public ChatViewModel(Conversation conversation) {
        mRepository = ChatMessageRepository.getInstance();
        mConversation = conversation;
        mListChatMessagesSimpleState = Transformations.switchMap(mRepository.getState(), input -> {
            if (input instanceof DataState.Error) {
                return new MutableLiveData<>(new SimpleState<>(((DataState.Error) input).getError().getMessage()));
            } else if (input instanceof DataState.Success) {
                return new MutableLiveData<>(new SimpleState<>(((DataState.Success<List<ChatMessage>>) input).getData()));
            }
            return new MutableLiveData<>(new SimpleState<>((Boolean) true));
        });
    }

    public LiveData<SimpleState<List<ChatMessage>>> getState() {
        mRepository.getMessagesForConversation(mConversation);
        mRepository.setUpMessageSubscriptionImpl(mConversation);
        return mListChatMessagesSimpleState;
    }


    public void loadMoreChats() {

    }

    public void sendMessage(String message) {

    }

    public void removeListener() {

    }

    public void fetchHistory() {

    }

    public void subscribe() {

    }

    public void initListener() {
    }

    private Long getEarliestTimestamp(List<ChatMessage> messagesList) {
        if (messagesList != null && !messagesList.isEmpty()) {
            return messagesList.get(0).getCreatedAt().getTime();
        }
        return null;
    }

    public void reload() {}
}
