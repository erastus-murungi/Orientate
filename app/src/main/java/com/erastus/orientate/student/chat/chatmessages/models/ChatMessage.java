package com.erastus.orientate.student.chat.chatmessages.models;

import android.util.Log;

import com.erastus.orientate.applications.App;
import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.student.announcements.models.Announcement;
import com.erastus.orientate.student.chat.conversations.FetchedLazily;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.models.Student;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.boltsinternal.Task;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    public String getSenderId() {
        return getSender().getObjectId();
    }

    /**
     * This is a blocking method and should be used with LiveData
     */
    @FetchedLazily
    public Student getSender() {
        try {
            return Objects.requireNonNull(getParseObject(KEY_SENDER)).fetchIfNeeded();
        } catch (ParseException e) {
            Log.e(TAG, "getSender:", e);
            return null;
        }
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

    @FetchedLazily
    public Conversation getConversation() {
        try {
            return getParseObject(KEY_CONVERSATION).fetch();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
