package com.erastus.orientate.student.chat.chatmessages;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.erastus.orientate.applications.App;
import com.erastus.orientate.student.chat.chatmessages.models.Message;
import com.erastus.orientate.student.chat.conversations.ConversationRepository;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.chat.models.Attachment;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.student.models.SimpleState;
import com.erastus.orientate.student.models.Student;
import com.erastus.orientate.utils.TaskRunner;
import com.parse.ParseQuery;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


public class ChatRepository {
    private final static String TAG = getInstance().getClass().getSimpleName();

    private final static int N_MESSAGES_HISTORY = 80;

    private static volatile ChatRepository sInstance;

    private static Map<String, List<Message>> mChats = new HashMap<>();

    private MutableLiveData<String> mErrors = new MutableLiveData<>();

    private static HashSet<Conversation> sSeen = new HashSet<>();

    private static final Object lock = new Object();

    private static final Object mutex = new Object();

    private static MutableLiveData<List<Message>> newMessages = new MutableLiveData<>();

    private MutableLiveData<DataState> mChatMessageDataSate = new MutableLiveData<>();

    private static MutableLiveData<SimpleState<Calendar>> mLastMessageSaved = new MutableLiveData<>();

    private static MutableLiveData<Message> mNewMessageArrived = new MutableLiveData<>();

    // we can use just a regular boolean since only one thread is accessing this
    private MutableLiveData<Boolean> mNoMoreMessages = new MutableLiveData<>(false);

    // because of working on multiple threads
    private AtomicBoolean isLoading = new AtomicBoolean(true);

    // for storing the latest messages
    // TODO persist in a local database
    private ArrayBlockingQueue<Message> queue = new ArrayBlockingQueue<>(N_MESSAGES_HISTORY);

    private static AtomicReference<Conversation> currentConversation = new AtomicReference<>();

    public static Conversation getCurrentConversation() {
        return currentConversation.get();
    }

    public static void setCurrentConversation(Conversation currentConversation) {
        getInstance().currentConversation.set(currentConversation);
    }

    public List<Message> getLatestMessages() {
        List<Message> sink = new ArrayList<>();
        queue.drainTo(sink);
        return sink;
    }


    public LiveData<Message> getLatestMessage() {
        return mNewMessageArrived;
    }

    public MutableLiveData<List<Message>> getMessages() {
        return newMessages;
    }

    class MessageProcessor implements Callable<DataState> {
        String mConversationId;
        List<Message> mNewMessages;

        MessageProcessor(List<Message> newMessages, String conversationId) {
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
                    List<Message> messages = Objects.requireNonNull(mChats.get(mConversationId));
                    messages.addAll(mNewMessages);
                    MessageHelper.chainMessages(messages, messages.size());
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
            for (Conversation conversation : conversations) {
                if (!sSeen.contains(conversation)) {
                    setUpMessageSubscription(conversation);
                    sSeen.add(conversation);
                    Log.d(TAG, "call: conversation with title: " + conversation.getTitle() + " subscribed");
                }
            }
            return null;
        }
    }

    static class ConversationProcessor implements Callable<Void> {
        private Conversation conversation;

        ConversationProcessor(Conversation conversation) {
            this.conversation = conversation;
        }

        @Override
        public Void call() {
            // subscribe each of the conversations
            if (!sSeen.contains(conversation)) {
                setUpMessageSubscription(conversation);
                sSeen.add(conversation);
            }
            return null;
        }
    }


    // all the conversations a user is subscribed to;
    // if a conversation is added, it is automatically subscribed by this block
    static {
        ConversationRepository.getInstance().getState().observeForever(dataState -> {
            boolean proceed = false;
            if (dataState instanceof DataState.Success) {
                try {
                    TaskRunner.getInstance().executeAsync(
                            new ConversationProcessorList(
                                    // the data we are receiving is null: error handling to be completed later
                                    ((DataState.Success<List<Conversation>>) dataState).getData()), (data) -> {
                            });
                } catch (ClassCastException e) {
                    Log.e(TAG, "static initializer: ", e);
                    proceed = true;
                }
                if (proceed) {
                    try {
                        TaskRunner.getInstance().executeAsync(
                                new ConversationProcessor(
                                        ((DataState.Success<Conversation>) dataState).getData()), (data) -> {
                                });
                    } catch (ClassCastException e1) {
                        Log.e(TAG, "static initializer: ", e1);
                    }
                }
            }
        });
    }

    // we don't want two threads creating two different repositories
    public static synchronized ChatRepository getInstance() {
        if (sInstance == null) {
            sInstance = new ChatRepository();
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


    static class NewMessageHandler implements Callable<List<Message>> {
        Message message;
        String currentConversationId;

        NewMessageHandler(Message newMessage, String currentConversationId) {
            message = newMessage;
            this.currentConversationId = currentConversationId;
        }

        @Override
        public List<Message> call() {
            // we do not want to handle new messages on the UI Thread
            assertNotIsMainThread();
            Log.d(TAG, "handleNewMessage: handling new message" + message);

            // this method makes sure that we only send to the UI One message at a time
            synchronized (lock) {
                // find the appropriate List
                String conversationId = message.getConversationId();
                List<Message> messages = mChats.get(conversationId);
                if (messages == null) {
                    messages = new ArrayList<>();
                    mChats.put(conversationId, messages);
                }

                mNewMessageArrived.postValue(message);

                messages.add(message);

                MessageHelper.chainMessages(messages, messages.size());
                // update the UI with this new message
                if (message.getConversationId().equals(getCurrentConversation().getObjectId())) {
                    return messages;
                } else {
                    return null;
                }
            }
        }
    }


    public static void setUpMessageSubscription(Conversation conversation) {
        // we are looking for messages
        ParseQuery<Message> messageParseQuery = ParseQuery.getQuery(Message.class);

        // PERSONAL BEWARE!!! whereContains does not work
        messageParseQuery.whereEqualTo(Message.KEY_CONVERSATION, conversation);

        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        parseLiveQueryClient
                .subscribe(messageParseQuery)
                .handleEvent(SubscriptionHandling.Event.CREATE,
                        (query, message) -> new Handler(Looper.getMainLooper())
                                .post(() -> {
                                            Log.d(TAG, "run: CREATE" + message.getContent());
                                            TaskRunner.getInstance().executeAsync(
                                                    new NewMessageHandler(message, conversation.getObjectId()),
                                                    messages -> newMessages.postValue(messages));
                                        }
                                )
                );
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

    public void getMessagesForConversation(Conversation conversation) {
        getMessagesForConversation(conversation, null);
    }


    public void getMessagesForConversationBefore(Conversation conversation) {
        List<Message> messages = mChats.get(conversation.getObjectId());
        if (messages == null || messages.size() == 0) {
            return;
        }
        getMessagesForConversation(conversation, getEarliestTimestamp(messages));
    }


    public void getMessagesForConversation(Conversation conversation, Date bound) {
        // we are adding lists lazily
        String conversationId = conversation.getObjectId();
        if (mChats.get(conversationId) == null) {
            mChats.put(conversationId, new ArrayList<>());
        }

        ParseQuery<Message> parseQuery = ParseQuery.getQuery(Message.class);
        parseQuery.whereEqualTo(Message.KEY_CONVERSATION, conversation);
        parseQuery.include(Message.KEY_SENDER);
        parseQuery.setLimit(N_MESSAGES_HISTORY);

        if (bound != null) {
            parseQuery.whereLessThan(Message.KEY_CREATED_AT, bound);
        }

        parseQuery.addAscendingOrder(Message.KEY_CREATED_AT);
        parseQuery.findInBackground((messages, e) -> {
            if (e == null) {
                TaskRunner.getInstance().executeAsync(
                        new MessageProcessor(messages, conversationId),
                        new NewMessageCallback());
            } else {
                Log.e(TAG, "getMessagesForConversation: ", e);
                mChatMessageDataSate.postValue(new DataState.Error(e));
            }
        });
    }

    class NewMessageCallback implements TaskRunner.Callback<DataState> {

        @Override
        public void onComplete(DataState result) {
            // if result is null, the the new message does not belong to this conversation
            // messages do not belong to current conversation
            if (result != null) {
                Log.d(TAG, "onComplete: message belongs to a different conversation");
                if (result instanceof DataState.Error) {
                    mChatMessageDataSate.postValue(new DataState.Error(((DataState.Error) result).getError()));
                } else if (result instanceof DataState.Success) {
                    mChatMessageDataSate.postValue(result);
                }
            }
        }
    }

    private Date getEarliestTimestamp(List<Message> messagesList) {
        if (messagesList != null && !messagesList.isEmpty()) {
            return messagesList.get(0).getCreatedAt();
        }
        return null;
    }

    static class MessageSender implements Callable<Void> {
        Student sender;
        Conversation conversation;
        String content;
        Attachment attachment;

        MessageSender(@NonNull Student sender,
                      @NonNull Conversation conversation,
                      @NonNull String content, Attachment attachment) {
            this.sender = sender;
            this.conversation = conversation;
            this.content = content;
            this.attachment = attachment;
        }

        @Override
        public Void call() {
            Message message = new Message();
            message.setConversation(conversation);
            message.setContent(content);
            message.setSender(sender);
            message.setAttachment(attachment);

            message.saveEventually(e -> {
                if (e == null) {
                    mLastMessageSaved.setValue(new SimpleState<>(Calendar.getInstance()));
                    Log.d(TAG, "call: " + message + " saved");
                } else {
                    mLastMessageSaved.setValue(new SimpleState<>(e.getLocalizedMessage()));
                }
            });
            return null;
        }
    }

    public void sendMessage(String message, Conversation conversation, Attachment attachment) {
        TaskRunner.getInstance()
                .executeAsync(
                        new MessageSender(
                                App.get().getCurrentUser().getStudent(),
                                conversation, message, attachment),
                        (data) -> {
                        });
    }
}
