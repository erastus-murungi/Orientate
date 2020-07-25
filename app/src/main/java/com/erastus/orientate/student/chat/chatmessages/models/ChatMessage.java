package com.erastus.orientate.student.chat.chatmessages.models;

import com.erastus.orientate.applications.App;
import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.student.chat.conversations.FetchedLazily;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Message")
public class ChatMessage extends ParseObject {
    private static final String TAG = "ChatMessage";

    public static final String KEY_CONVERSATION = "conversation";
    public static final String KEY_SENDER = "sender";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_MESSAGE_TYPE = "type";
    public static final String KEY_USER = "user";

    public ChatMessage() {
    }

    private int messageType;

    private ExtendedParseUser mExtendedParseUser;


    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    @FetchedLazily
    public boolean isOwnMessage() {
        return getSenderId().equals(App.get().getCurrentUser().getObjectId());
    }

    @FetchedLazily
    public String getSenderId() {
        return getSender().getObjectId();
    }

    /**
     * This is a blocking method and should be used with LiveData
     */
    @FetchedLazily
    public ParseUser getSender() {
        return getParseUser(KEY_SENDER);
    }

    public ExtendedParseUser getExtendedParseUser() {
        if (mExtendedParseUser == null) {
            mExtendedParseUser = new ExtendedParseUser(getSender());
        }
        return mExtendedParseUser;
    }

    public String getContent() {
        return getString(KEY_CONTENT);
    }

    public boolean isSent() {
        return false;
    }

    public boolean isOnline() {
        return false;
    }

}
