package com.erastus.orientate.student.chat.chatmessages;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.erastus.orientate.student.chat.chatmessages.models.ChatMessage;
import com.erastus.orientate.student.chat.conversations.ConversationRepository;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.utils.TaskRunner;
import com.parse.ParseQuery;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatMessageRepository {
    private static final String TAG = "ChatMessageRepository";
    private static volatile ChatMessageRepository sInstance;
    private ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
    private Map<String, List<ChatMessage>> mChats = new HashMap<>();
    private MutableLiveData<String> mErrors = new MutableLiveData<>();
    private static HashSet<Conversation> seen = new HashSet<>();

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
                // we only have one list of messages per conversation
                // in case a message is incoming, we would encounter a ConcurrentModificationException
                // because two threads would be modifying the list at the same time
                synchronized (mutex) {
                    List<ChatMessage> messages = Objects.requireNonNull(mChats.get(mConversationId));
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


    static class ConversationProcessorList implements Callable<Void> {
        private List<Conversation> conversations;
        ConversationProcessorList(List<Conversation> conversations) {
            this.conversations = conversations;
        }

        @Override
        public Void call() {
            // subscribe each of the conversations
            for (Conversation conversation: conversations) {
                if (!seen.contains(conversation)) {
                    setUpMessageSubscriptionImpl(conversation);
                    seen.add(conversation);
                }
            }
            return null;
        }
    }

    static class ConversationProcessor implements Callable<Void> {
        private Conversation conversation;
        ConversationProcessor(Conversation
                                      conversation) {
            this.conversation = conversation;
        }

        @Override
        public Void call() {
            // subscribe each of the conversations
            if (!seen.contains(conversation)) {
                    setUpMessageSubscriptionImpl(conversation);
                    seen.add(conversation);
            }
            return null;
        }
    }


    // all the conversations a user is subscribed to;
    // if a conversation is added, it is automatically subscribed by this block
    static {
        ConversationRepository.getInstance().getState().observeForever(dataState -> {
            if (dataState instanceof DataState.Success) {
                try {
                    TaskRunner.getInstance().executeAsync(
                            new ConversationProcessorList(
                                    // the data we are receiving is null: error handling to be completed later
                                    ((DataState.Success<List<Conversation>>) dataState).getData()), (data) -> {});
                } catch (ClassCastException e) {
                    Log.e(TAG, "static initializer: ", e);
                    try {
                        TaskRunner.getInstance().executeAsync(
                                new ConversationProcessor(
                                        ((DataState.Success<Conversation>) dataState).getData()), (data) -> {});
                    } catch (ClassCastException e1) {
                        Log.e(TAG, "static initializer: ", e1);
                    }
                }
            }
        });
    }

    private MutableLiveData<DataState> mChatMessageDataSate = new MutableLiveData<>();

    // because of working on multiple threads
    private AtomicBoolean isLoading = new AtomicBoolean(true);

    private static final Object mutex = new Object();

    // we don't want two threads creating two different repositories
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

    public static void setUpMessageSubscriptionImpl(Conversation conversation) {
        // we are looking for messages
        ParseQuery<ChatMessage> query = ParseQuery.getQuery(ChatMessage.class);

        // PERSONAL BEWARE!!! whereContains does not work
        query.whereEqualTo(ChatMessage.KEY_CONVERSATION, conversation);

        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        parseLiveQueryClient.subscribe(query).handleEvents((query1, event, message) -> {
            Handler refresh = new Handler(
                    Looper.getMainLooper());
            refresh.post(() -> {
                Log.d(TAG, "setUpMessageSubscriptionImpl: " + Looper.getMainLooper().isCurrentThread());
                if (event == SubscriptionHandling.Event.CREATE) {
                    handleNewMessage(message);
                    Log.d(TAG, "run: CREATE" + message.getContent());
                } else if (event == SubscriptionHandling.Event.DELETE) {
                    // Handle deletion of parseObjectSubclass here
                    Log.d(TAG, "run: DELETE" + message);
                }
            });
        });
    }

    private void setUpConversionObserver(int start) {
    }

    private void setUpObservers() {
    }

    private static void assertNotIsMainThread() {
        if (Looper.getMainLooper().isCurrentThread()) {
            throw new IllegalThreadStateException();
        }
    }


    // this method makes sure that we only send to the UI One message at a time
    private static synchronized void handleNewMessage(ChatMessage message) {
        // we do not want to handle new messages on the UI Thread
        assertNotIsMainThread();
        Log.d(TAG, "handleNewMessage: new message is being handled");
        // update the UI with this new message
    }

    public void getMessagesForConversation(Conversation conversation) {
        // we are adding lists lazily
        String conversationId = conversation.getObjectId();
        if (mChats.get(conversationId) == null) {
            mChats.put(conversationId, new ArrayList<>());
        }

        ParseQuery<ChatMessage> parseQuery = ParseQuery.getQuery(ChatMessage.class);
        parseQuery.whereEqualTo(ChatMessage.KEY_CONVERSATION, conversation);
        parseQuery.include(ChatMessage.KEY_SENDER);
        parseQuery.addAscendingOrder(ChatMessage.KEY_CREATED_AT);
        parseQuery.findInBackground((messages, e) -> {
            if (e == null) {
                TaskRunner.getInstance().executeAsync(
                        new MessageProcessor(messages, conversationId),

                        data -> mChatMessageDataSate.postValue(
                                (data instanceof DataState.Error) ?
                                        new DataState.Error(((DataState.Error) data).getError()) :
                                new DataState.Success<>(messages)));
            } else {
                Log.e(TAG, "getMessagesForConversation: ", e);
                mChatMessageDataSate.postValue(new DataState.Error(e));
            }
        });
    }
}
