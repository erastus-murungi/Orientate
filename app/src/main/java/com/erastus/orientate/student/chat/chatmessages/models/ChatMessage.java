package com.erastus.orientate.student.chat.chatmessages.models;

import com.erastus.orientate.student.chat.conversations.LateInit;
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
    private boolean isOwnMessage;
    private ParseUser mSender;


    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public boolean isOwnMessage() {
        return isOwnMessage;
    }

    public void setOwnMessage(boolean ownMessage) {
        isOwnMessage = ownMessage;
    }

    public String getSenderId() {
        if (mSender == null) {
            return null;
        }
        return mSender.getObjectId();
    }

    /**
     * This is a blocking method and should be used with LiveData
     */
    @LateInit
    public ParseUser getSender() {
        return mSender;
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
