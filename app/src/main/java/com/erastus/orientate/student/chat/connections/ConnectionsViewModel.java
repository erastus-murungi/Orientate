package com.erastus.orientate.student.chat.connections;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.erastus.orientate.applications.App;
import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.student.models.SimpleState;

import java.util.Arrays;
import java.util.List;

public class ConnectionsViewModel extends ViewModel {
    private static final String TAG = "ConnectionsViewModel";
    private ConnectionsRepository mRepo;

    private LiveData<SimpleState<List<ExtendedParseUser>>> mConnections;

    private LiveData<SimpleState<Conversation>> mStartConversation;

    public LiveData<SimpleState<Conversation>> getStartConversation() {
        return mStartConversation;
    }

    @SuppressWarnings("unchecked")
    public ConnectionsViewModel() {
        mRepo = ConnectionsRepository.getInstance();
        mRepo.fetchConnections();
        this.mConnections = Transformations.map(
                mRepo.getConnections(), input -> {
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
        mStartConversation = Transformations.map(mRepo.getNewConversation(), input -> {
                if (input instanceof DataState.Success) {
                    Log.i(TAG, "apply: ");
                    return new SimpleState<>(((DataState.Success<Conversation>) input).getData());
                } else if (input instanceof DataState.Error) {
                    Log.e(TAG, "apply: ", ((DataState.Error) input).getError());
                    return new SimpleState<>(((DataState.Error) input).getError().getLocalizedMessage());
                }
                return new SimpleState<>((Boolean) true);
        });
    }

    public LiveData<SimpleState<List<ExtendedParseUser>>> getConnections() {
        return mConnections;
    }

    public void reload() {
    }

    public void startConversationWithUser(ExtendedParseUser user) {
        mRepo.fetchConversationsWithExactlyUsers(Arrays.asList(user.getObjectId(), App.get().getCurrentUser().getObjectId()));
    }
}