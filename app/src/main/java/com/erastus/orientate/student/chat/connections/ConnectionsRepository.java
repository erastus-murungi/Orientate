package com.erastus.orientate.student.chat.connections;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.erastus.orientate.applications.App;
import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.models.DataState;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConnectionsRepository {
    private static MutableLiveData<DataState> sConnections = new MutableLiveData<>();

    private static MutableLiveData<DataState> sNewConversation = new MutableLiveData<>();

    public static volatile ConnectionsRepository sInstance;

    public static synchronized ConnectionsRepository getInstance() {
        if (sInstance == null) {
            sInstance = new ConnectionsRepository();
        }
        return sInstance;
    }

    public LiveData<DataState> getNewConversation() {
        return sNewConversation;
    }

    public LiveData<DataState> getConnections() {
        return sConnections;
    }

    public void fetchConnections() {
        JSONArray array = App.get().getCurrentUser().getConnections();
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        try {
            query.whereContainedIn(Conversation.KEY_OBJECT_ID, fromJsonArray(array));
        } catch (JSONException e) {
            sConnections.postValue(new DataState.Error(e));
        }
        query.include(ExtendedParseUser.KEY_STUDENT);
        query.findInBackground((participants, e) -> {
            if (e == null) {
                sConnections.postValue(new DataState.Success<>(
                        participants.stream().map(ExtendedParseUser::new).collect(Collectors.toList())));
            } else {
                sConnections.postValue(new DataState.Error(e));
            }
        });
    }
    public void fetchConversationsWithExactlyUsers(List<String> userIds) {
        ParseQuery<Conversation> conversationParseQuery = ParseQuery.getQuery(Conversation.class);

        conversationParseQuery.whereContainedIn(Conversation.KEY_PARTICIPANTS, userIds);

        conversationParseQuery.getFirstInBackground((conversation, e) -> {
            if (e == null) {
                sNewConversation.postValue(new DataState.Success<>(conversation));
            } else {
                if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                    Conversation c = new Conversation();
                    c.addAll(Conversation.KEY_PARTICIPANTS, userIds);
                    c.saveInBackground(e1 -> {
                        if (e1 == null) {
                            sNewConversation.postValue(new DataState.Success<>(c));
                        } else {
                            sNewConversation.postValue(new DataState.Error(e1));
                        }
                    });
                } else {
                    sNewConversation.postValue(new DataState.Error(e));
                }
            }
        });
    }



    private static List<String> fromJsonArray(JSONArray array) throws JSONException {
        List<String> a = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            a.add(i, array.getString(i));
        }
        return a;
    }
}
