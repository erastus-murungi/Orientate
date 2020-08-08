package com.erastus.orientate.student.profile.discoverpeople;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.erastus.orientate.applications.App;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.models.DataState;
import com.parse.ParseQuery;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

public class DiscoverPeopleRepository {
    private static final String TAG = "DiscoverPeopleRepository";

    private static MutableLiveData<Conversation> sConversationFound = new MutableLiveData<>();

    private static MutableLiveData<DataState> sPreviousConversationMatches = new MutableLiveData<>();

    public static volatile DiscoverPeopleRepository sInstance;

    static {
        setUpNewConversationListener();
        getPreviousConversationMatchesBelongingToThisUser();
    }

    public static synchronized DiscoverPeopleRepository getInstance() {
        if (sInstance == null) {
            sInstance = new DiscoverPeopleRepository();
        }
        return sInstance;
    }

    public MutableLiveData<DataState> getPreviousConversationMatchesState() {
        return sPreviousConversationMatches;
    }


    public static void setUpNewConversationListener() {
        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        ParseQuery<Conversation> conversationParseQuery = ParseQuery.getQuery(Conversation.class);

        conversationParseQuery.whereEqualTo(Conversation.KEY_IS_SYSTEM_GENERATED, true);

        parseLiveQueryClient.subscribe(conversationParseQuery).handleEvents((query, event, conversation) -> {
            Handler refresh = new Handler(Looper.getMainLooper());
            refresh.post(() -> {
                Log.d(TAG, "run: " + event);
                if (event == SubscriptionHandling.Event.CREATE) {
                    sConversationFound.postValue(conversation);
                } else if (event == SubscriptionHandling.Event.DELETE) {
                }
            });
        });
    }

    private static void getPreviousConversationMatchesBelongingToThisUser() {
        ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);
        query.whereEqualTo(Conversation.KEY_IS_SYSTEM_GENERATED, true);
        query.whereEqualTo(Conversation.KEY_PARTICIPANTS,
                App.get().getCurrentUser().getObjectId()).findInBackground((objects, e) -> {
            if (e == null) {
                sPreviousConversationMatches.postValue(new DataState.Success<>(objects));
                Log.d(TAG, "fetchConversations: " + objects);
            } else {
                sPreviousConversationMatches.postValue(new DataState.Error(e));
                Log.e(TAG, "fetchConversations: " + e);
            }
        });
    }
}

