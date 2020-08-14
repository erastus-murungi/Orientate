package com.erastus.orientate.student.profile.discoverpeople;

import android.net.TrafficStats;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.erastus.orientate.applications.App;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.student.models.SimpleState;

import java.util.List;

public class DiscoverPeopleViewModel extends ViewModel {

    private LiveData<SimpleState<List<Conversation>>> mState;
    private DiscoverPeopleRepository mRepo;
    private LiveData<SimpleState<Conversation>> mConversationFound;

    public DiscoverPeopleViewModel() {
        mRepo = DiscoverPeopleRepository.getInstance();

        this.mState = Transformations.switchMap(mRepo.getPreviousConversationMatchesState(), input -> {
            if (input instanceof DataState.Success) {
                List<Conversation> conversation = ((DataState.Success<List<Conversation>>) input).getData();
                return new MutableLiveData<>(new SimpleState<>(conversation));
            } else if (input instanceof DataState.Error) {
                return new MutableLiveData<>(new SimpleState<>(((DataState.Error) input).getError().getMessage()));
            }
            return new MutableLiveData<>(new SimpleState<>((Boolean) true));
        });

        this.mConversationFound = Transformations.switchMap(mRepo.getConversationFound(), input -> {
            if (input instanceof DataState.Success) {
                Conversation conversation = ((DataState.Success<Conversation>) input).getData();
                return new MutableLiveData<>(new SimpleState<>(conversation));
            } else if (input instanceof DataState.Error) {
                return new MutableLiveData<>(new SimpleState<>(((DataState.Error) input).getError().getMessage()));
            }
            return new MutableLiveData<>(new SimpleState<>((Boolean) true));
        });
    }

    public LiveData<SimpleState<Conversation>> getConversationFound() {
        return mConversationFound;
    }

    public LiveData<SimpleState<List<Conversation>>> getPreviousConversationMatchesState() {
        return mState;
    }

    public void findMatch() {
        App.get().getCurrentUser().setWantsRoom(true);
        mRepo.findMatch();
    }

    public void reload() {

    }
}