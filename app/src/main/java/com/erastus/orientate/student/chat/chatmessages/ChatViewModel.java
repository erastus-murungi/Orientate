package com.erastus.orientate.student.chat.chatmessages;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.erastus.orientate.student.chat.chatmessages.models.Message;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.chat.models.Attachment;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.student.models.SimpleState;
import com.parse.ParseObject;

import java.util.List;

public class ChatViewModel extends ViewModel {
    private static final String TAG = "ChatViewModel";

    private Conversation mConversation;

    private LiveData<SimpleState<List<Message>>> mListChatMessagesSimpleState;
    private SimpleState<Boolean> mMessageSent;
    private ChatRepository mRepository;
    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    private LiveData<Message> mNewMessageArrived;

    private MutableLiveData<List<Message>> newMessages;

    LiveData<List<Message>> getNewMessages() {
        return newMessages;
    }

    public LiveData<Message> getNewMessageArrived() {
        return mNewMessageArrived;
    }

    @SuppressWarnings("unchecked")
    public ChatViewModel(Conversation conversation) {
        mRepository = ChatRepository.getInstance();
        mConversation = conversation;
        mListChatMessagesSimpleState = Transformations.switchMap(mRepository.getState(), input -> {
            if (input instanceof DataState.Error) {
                return new MutableLiveData<>(new SimpleState<>(((DataState.Error) input).getError().getMessage()));
            } else if (input instanceof DataState.Success) {
                return new MutableLiveData<>(new SimpleState<>(((DataState.Success<List<Message>>) input).getData()));
            }
            return new MutableLiveData<>(new SimpleState<>((Boolean) true));
        });

        // propagate the changes only to
        newMessages = mRepository.getMessages();
        mRepository.getMessagesForConversation(mConversation);
        mNewMessageArrived = mRepository.getLatestMessage();
    }

    public LiveData<SimpleState<List<Message>>> getState() {
        return mListChatMessagesSimpleState;
    }


    public void loadMoreChats() {

    }

    public void sendMessage(String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }

        //TODO replace the attachments argument with optional Attachment object
        mRepository.sendMessage(message, mConversation, ParseObject.create(Attachment.class));
        Log.d(TAG, "sendMessage: ");

//        hostActivity.getPubNub()
//                .publish()
//                .channel(mChannel)
//                .shouldStore(true)
//                .message(Message.newBuilder().text(message).build())
//                .async(new PNCallback<PNPublishResult>() {
//                    @Override
//                    public void onResponse(PNPublishResult result, PNStatus status) {
//                        if (!status.isError()) {
//                            long newMessageTimetoken = result.getTimetoken();
//                        } else {
//                            Message msg = Message.createUnsentMessage(Message.newBuilder().text(finalMessage).build());
//                            mMessages.add(msg);
//                            History.chainMessages(mMessages, mMessages.size());
//                            runOnUiThread(() -> {
//                                if (mEmptyView.getVisibility() == View.VISIBLE) {
//                                    mEmptyView.setVisibility(View.GONE);
//                                }
//                                mChatAdapter.update(mMessages);
//                                scrollChatToBottom();
//
//                                Toast.makeText(fragmentContext, R.string.message_not_sent, Toast.LENGTH_SHORT).show();
//
//                            });
//                        }
//                    }
//                });
    }

    public void removeListener() {

    }

    public void fetchHistory() {
        mIsLoading.setValue(true);
        mRepository.getMessagesForConversationBefore(mConversation);
    }

    public void subscribe() {

    }

    public void initListener() {
    }

    public void reload() {}

    public Conversation getConversation() {
        return mConversation;
    }

    public String getMyConversationId() {
        return mConversation.getObjectId();
    }

    public String getConversationTitle() {
        return mConversation.getTitle();
    }
}
