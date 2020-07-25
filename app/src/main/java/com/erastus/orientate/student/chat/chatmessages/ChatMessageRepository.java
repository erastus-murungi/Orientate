package com.erastus.orientate.student.chat.chatmessages;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.erastus.orientate.applications.App;
import com.erastus.orientate.student.chat.chatmessages.models.ChatMessage;
import com.erastus.orientate.student.chat.connections.Connection;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.chat.models.Participant;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.utils.TaskRunner;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatMessageRepository {
    private static final String TAG = "ChatMessageRepository";
    private static volatile ChatMessageRepository sInstance;
    private ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
    private MutableLiveData<List<ChatMessage>> mMessages = new MutableLiveData<>(new ArrayList<>());
    private Map<String, List<ChatMessage>> mChats = new HashMap<>();
    private Map<String, MutableLiveData<List<Participant>>> mParticipants = new HashMap<>();


    private MutableLiveData<DataState> mChatMessageDataSate = new MutableLiveData<>();

    // because of working on multiple threads
    private AtomicBoolean isLoading = new AtomicBoolean(true);

    private static final Object mutex = new Object();

    public static synchronized ChatMessageRepository getInstance() {
        if (sInstance == null) {
            sInstance = new ChatMessageRepository();
        }
        return sInstance;
    }

    public LiveData<DataState> getState() {
        return mChatMessageDataSate;
    }

    public boolean getIsLoading() {
        return isLoading.get();
    }

    public void setIsLoading(boolean isLoading) {
        this.isLoading.set(isLoading);
    }

//    private void setUpMessageSubscription() {
//        ParseQuery<ChatMessage> outerQuery = ParseQuery.getQuery(ChatMessage.class);
//        ParseQuery<Conversation> innerQuery = ParseQuery.getQuery(Conversation.class);
//        innerQuery.whereEqualTo(Conversation.KEY_PARTICIPANTS, App.get().getCurrentUser());
//        outerQuery.whereMatchesQuery(ChatMessage.KEY_CONVERSATION, innerQuery);
//
//        SubscriptionHandling<ChatMessage> subscriptionHandling = parseLiveQueryClient.subscribe(outerQuery);
//
//        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<ChatMessage>() {
//            @Override
//            public void onEvent(ParseQuery<ChatMessage> query, ChatMessage object) {
//                if (object != null) {
//
//                }
//            }
//        });
//    }

    class MessageProcessor implements Callable<DataState> {
        String mConversationId;
        List<ChatMessage> mNewMessages;

        MessageProcessor(List<ChatMessage> newMessages, String conversationId) {
            mConversationId = conversationId;
            mNewMessages = newMessages;
        }

        @Override
        public DataState call() {
            setIsLoading(true);
            try {
                synchronized (mutex) {
                    List<ChatMessage> messages = mChats.get(mConversationId);
                    assert messages != null;
                    messages.addAll(mNewMessages);
                    ChatMessageHelper.chainMessages(messages, messages.size());
                    setIsLoading(false);
                    return new DataState.Success<>(messages);
                }
            } catch (NullPointerException e) {
                Log.e(TAG, "call: ", e);
                setIsLoading(false);
                return new DataState.Error(e);
            }

        }
    }


    void make() {
        Log.d(TAG, "make: running");
//        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        ParseQuery<ChatMessage> query = ParseQuery.getQuery(ChatMessage.class);
        ParseQuery<Conversation> innerQuery = ParseQuery.getQuery(Conversation.class);
        innerQuery.whereEqualTo(Conversation.KEY_PARTICIPANTS, App.get().getCurrentUser().getObjectId());
        query.whereMatchesQuery(ChatMessage.KEY_CONVERSATION, innerQuery);
        // Add query constraints here
        query.findInBackground((objects, e) -> Log.d(TAG, "done: " + " " + objects + " " + e));
    }


    public void getMessagesForConversation(Conversation conversation) {
        if (mChats.get(conversation.getObjectId()) == null) {
            mChats.put(conversation.getObjectId(), new ArrayList<>());
        }
        ParseQuery<ChatMessage> parseQuery = ParseQuery.getQuery(ChatMessage.class);
        parseQuery.whereEqualTo(ChatMessage.KEY_CONVERSATION, conversation);
        parseQuery.include(ChatMessage.KEY_SENDER);
        parseQuery.findInBackground((messages, e) -> {
            if (e == null) {
                TaskRunner.getInstance().executeAsync(
                        new MessageProcessor(messages, conversation.getObjectId()),
                        data -> mChatMessageDataSate.postValue(
                                (data instanceof DataState.Error) ?  new DataState.Error(((DataState.Error) data).getError()) :
                                new DataState.Success<>(messages)));
            } else {
                Log.e(TAG, "getMessagesForConversation: ", e);
                mChatMessageDataSate.postValue(new DataState.Error(e));
            }
        });
    }
}
