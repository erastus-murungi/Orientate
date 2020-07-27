package com.erastus.orientate.student.chat.chatinfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.models.DataState;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParticipantsRepository {

    private static volatile ParticipantsRepository sInstance;

    private MutableLiveData<DataState> mParticipantsList = new MutableLiveData<>();

    public static synchronized ParticipantsRepository getInstance() {
        if (sInstance == null) {
            sInstance = new ParticipantsRepository();
        }
        return sInstance;
    }


    public LiveData<DataState> getParticipantsList() {
        return mParticipantsList;
    }

    public void fetchParticipants(Conversation conversation) {
        JSONArray array = conversation.getParticipants();
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        try {
            query.whereContainedIn(Conversation.KEY_OBJECT_ID, fromJsonArray(array));
        } catch (JSONException e) {
            mParticipantsList.postValue(new DataState.Error(e));
        }
        query.include(ExtendedParseUser.KEY_STUDENT);
        query.findInBackground((participants, e) -> {
                if (e == null) {
                mParticipantsList.postValue(new DataState.Success<>(
                        participants.stream().map(ExtendedParseUser::new).collect(Collectors.toList())));
                } else {
                    mParticipantsList.postValue(new DataState.Error(e));
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
