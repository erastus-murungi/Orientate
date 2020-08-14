package com.erastus.orientate.student.chat.conversations;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.erastus.orientate.applications.App;
import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.models.DataState;
import com.parse.ParseQuery;

import java.util.List;

public class ConversationRepository {
    private static final String TAG = "ConversationRepository";

    private static volatile ConversationRepository sInstance;
    private static MutableLiveData<List<Conversation>> mConversationMutableLiveData = new MutableLiveData<>();
    private static MutableLiveData<DataState> mState = new MutableLiveData<>();

    public static synchronized ConversationRepository getInstance() {
        if (sInstance == null) {
            sInstance = new ConversationRepository();
        }
        return sInstance;
    }

    static {
        fetchConversationsBelongingToThisUser();
    }

    public LiveData<DataState> getState() {
        return mState;
    }

    public static void fetchConversationsBelongingToThisUser() {
        ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);
        query.whereEqualTo(Conversation.KEY_PARTICIPANTS,
                App.get().getCurrentUser().getObjectId()).findInBackground((objects, e) -> {
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
}
