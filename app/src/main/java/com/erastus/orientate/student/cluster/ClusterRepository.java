package com.erastus.orientate.student.cluster;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.student.chat.chatmessages.ChatRepository;
import com.erastus.orientate.student.chat.chatmessages.models.Message;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.utils.TaskRunner;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.util.List;
import java.util.concurrent.Callable;

public class ClusterRepository {

    private static final String TAG = "ClusterRepository";

    private MutableLiveData<List<ExtendedParseUser>> users = new MutableLiveData<>();

    public static volatile ClusterRepository sInstance;

    public static ClusterRepository getInstance() {
        if (sInstance == null) {
            sInstance = new ClusterRepository();
        }
        return sInstance;
    }

    // listen for a new users
    public static void setUpNewUserSubscription() {
        // we are looking for messages
        ParseQuery<ParseUser> userParseQuery = ParseQuery.getQuery(ParseUser.class);

        // PERSONAL BEWARE!!! whereContains does not work
        userParseQuery.include(ExtendedParseUser.KEY_USER_INFO);

        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        parseLiveQueryClient
                .subscribe(userParseQuery)
                .handleEvent(SubscriptionHandling.Event.CREATE,
                        (query, newUser) -> new Handler(Looper.getMainLooper())
                                .post(() -> {

                                        }
                                )
                );
    }
}
