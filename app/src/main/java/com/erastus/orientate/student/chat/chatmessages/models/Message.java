package com.erastus.orientate.student.chat.chatmessages.models;

import android.util.Log;

import com.erastus.orientate.applications.App;
import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.student.announcements.models.Announcement;
import com.erastus.orientate.student.chat.conversations.FetchedLazily;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.chat.models.Attachment;
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
public class Message extends ParseObject {
    private static final String TAG = "ChatMessage";

    public static final String KEY_CONVERSATION = "conversation";
    public static final String KEY_SENDER = "sender";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_ATTACHMENT = "attachment";

    public Message() {
    }

    private int messageType;

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

    public void setConversation(Conversation conversation) {
        put(KEY_CONVERSATION, conversation);
    }

    public void setContent(String content) {
        put(KEY_CONTENT, content);
    }

    public void setSender(Student sender) {
        put(KEY_SENDER, sender);
    }

    public void setAttachment(Attachment attachment) {
        put(KEY_ATTACHMENT, attachment);
    }

    public String getConversationId() {
        return getConversation().getObjectId();
    }

    public boolean isOwn() {
        return getSenderId().equals(App.get().getCurrentUser().getStudent().getObjectId());
    }
}
