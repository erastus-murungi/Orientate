package com.erastus.orientate.student.chat.conversations;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.erastus.orientate.applications.App;
import com.erastus.orientate.student.chat.chatmessages.models.ChatMessage;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.utils.TaskRunner;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.util.List;
import java.util.concurrent.Callable;

public class ConversationRepository {
    private static final String TAG = "ConversationRepository";

    private static volatile ConversationRepository sInstance;
    private MutableLiveData<List<Conversation>> mConversationMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<DataState> mState = new MutableLiveData<>();

    public static synchronized ConversationRepository getInstance() {
        if (sInstance == null) {
            sInstance = new ConversationRepository();
        }
        return sInstance;
    }

    public LiveData<DataState> getState() {
        fetchConversationsBelongingToThisUser();
        filterConversations();
        return mState;
    }


    public void fetchConversationsBelongingToThisUser() {
        ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);
        query.whereEqualTo(Conversation.KEY_PARTICIPANTS, App.get().getCurrentUser().getObjectId()).findInBackground((objects, e) -> {
            if (e == null) {
                mState.postValue(new DataState.Success<>(objects));
                mConversationMutableLiveData.postValue(objects);
                Log.d(TAG, "fetchConversations: " + objects);
            } else {
                mState.postValue(new DataState.Error(e));
                Log.e(TAG, "fetchConversations: " + e);
            }
        });

    }

    void filterConversations() {
        Log.d(TAG, "filterConversations: running");
        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);
        query.whereEqualTo(Conversation.KEY_PARTICIPANTS, App.get().getCurrentUser().getObjectId());
        parseLiveQueryClient.subscribe(query).handleEvents((query1, event, object) -> Log.d(TAG, "onEvents: " + event));
    }

    private static class GetInnerFields implements Callable<DataState> {
        private List<Conversation> mConversations;

        GetInnerFields(List<Conversation> conversations) {
            this.mConversations = conversations;
        }

        @Override
        public DataState call() {
            for (Conversation conversation : mConversations) {
                try {
                    ChatMessage chatMessage = conversation.getParseObject(Conversation.KEY_LAST_MESSAGE).fetchIfNeeded();
                    conversation.setLastMessage(chatMessage);
                } catch (ParseException e) {
                    return new DataState.Error(e);
                }
            }
            return new DataState.Success<>(mConversations);
        }
    }

    private void getNestedObjects(List<Conversation> conversations) {
        TaskRunner.getInstance().executeAsync(new GetInnerFields(conversations), data -> mState.postValue(data));
    }
}
