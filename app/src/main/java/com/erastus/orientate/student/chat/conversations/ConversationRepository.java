package com.erastus.orientate.student.chat.conversations;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.models.DataState;
import com.parse.ParseQuery;

import java.util.List;

public class ConversationRepository {
    private MutableLiveData<List<Conversation>> mConversationMutableLiveData;
    private MutableLiveData<DataState> mState;

    public LiveData<List<Conversation>> getConversations() {
        return mConversationMutableLiveData;
    }


    public void fetchConversations() {
        ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);
        query.findInBackground((objects, e) -> {
            if (e == null) {
                mState.postValue(new DataState.Success<>(objects));
                mConversationMutableLiveData.postValue(objects);
            } else {
                mState.postValue(new DataState.Error(e));
            }
        });
    }
}
