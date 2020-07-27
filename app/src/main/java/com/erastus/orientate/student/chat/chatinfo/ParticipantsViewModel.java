package com.erastus.orientate.student.chat.chatinfo;

import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.student.models.SimpleState;

import java.util.List;

public class ParticipantsViewModel extends ViewModel {
    private static final String TAG = "ParticipantsViewModel";
    private LiveData<SimpleState<List<ExtendedParseUser>>> mParticipants;
    private ParticipantsRepository mRepository = ParticipantsRepository.getInstance();
    private Conversation mConversation;

    @SuppressWarnings("unchecked")
    public ParticipantsViewModel(Conversation conversation) {
        mConversation = conversation;
        fetchParticipants();
        this.mParticipants = Transformations.map(
                mRepository.getParticipantsList(), (Function<DataState, SimpleState<List<ExtendedParseUser>>>) input -> {
                    if (input instanceof DataState.Success) {
                        Log.i(TAG, "apply: ");
                        return new SimpleState<>(((DataState.Success<List<ExtendedParseUser>>) input).getData());
                    } else if (input instanceof DataState.Error) {
                        Log.e(TAG, "apply: ", ((DataState.Error) input).getError());
                        return new SimpleState<>(((DataState.Error) input).getError().getLocalizedMessage());
                    }
                    return new SimpleState<>((Boolean) true);
                }
        );
    }

    public LiveData<SimpleState<List<ExtendedParseUser>>> getParticipants() {
        return mParticipants;
    }

    private void fetchParticipants() {
        mRepository.fetchParticipants(mConversation);
    }

    public void reload() {
        mRepository.fetchParticipants(mConversation);
    }

    public Conversation getConversation() {
        return mConversation;
    }
}
