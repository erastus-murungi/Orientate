package com.erastus.orientate.student.chat.chatmessages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.erastus.orientate.student.chat.chatmessages.models.ChatMessage;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.models.DataState;
import com.parse.ParseQuery;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageRepository {
    private volatile ChatMessageRepository instance;
    private ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
    private MutableLiveData<List<ChatMessage>> mMessages = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<DataState> mChatMessageDataSate;

    public synchronized ChatMessageRepository getInstance() {
        if (instance == null) {
            instance = new ChatMessageRepository();
        }
        return instance;
    }

    public LiveData<List<ChatMessage>> getRecentMessages() {
        return mMessages;
    }


    private void setUpMessageSubscription() {
        ParseQuery<ChatMessage> parseQuery = ParseQuery.getQuery(ChatMessage.class);
        SubscriptionHandling<ChatMessage> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);

        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, (query, object) -> {

        });
    }

    private void grabMessages(Conversation conversation) {
        ParseQuery<ChatMessage> parseQuery = ParseQuery.getQuery(ChatMessage.class);
        parseQuery.whereEqualTo(ChatMessage.KEY_CONVERSATION, conversation);
        parseQuery.findInBackground((messages, e) -> {
            if (e == null) {
                mMessages.setValue(messages);
                mChatMessageDataSate.postValue(new DataState.Success<>(messages));
            } else {
                mChatMessageDataSate.postValue(new DataState.Error(e));
            }
        });
    }
}
