package com.erastus.orientate.student.chat.conversations;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.student.models.SimpleState;

import java.util.List;

public class ConversationViewModel extends ViewModel {
    public static final int MAX_NUM_CONVERSATIONS = 40;

    private ConversationRepository mRepository;

    private LiveData<SimpleState<List<Conversation>>> mState = new MutableLiveData<>(new SimpleState<List<Conversation>>(true));

    @SuppressWarnings("unchecked")
    public ConversationViewModel() {
        this.mRepository = ConversationRepository.getInstance();
        this.mState = Transformations.switchMap(mRepository.getState(), input -> {
            if (input instanceof DataState.Success) {
                List<Conversation> conversation = ((DataState.Success<List<Conversation>>) input).getData();
                return new MutableLiveData<>(new SimpleState<>(conversation));
            } else if (input instanceof DataState.Error) {
                return new MutableLiveData<>(new SimpleState<>(((DataState.Error) input).getError().getMessage()));
            }
            return new MutableLiveData<>(new SimpleState<>((Boolean) true));
        });
    }

    public LiveData<SimpleState<List<Conversation>>> getState() {
        return mState;
    }
}
