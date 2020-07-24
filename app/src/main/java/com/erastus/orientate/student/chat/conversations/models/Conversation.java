package com.erastus.orientate.student.chat.conversations.models;

import com.erastus.orientate.student.chat.chatmessages.models.ChatMessage;
import com.erastus.orientate.student.chat.conversations.LateInit;
import com.erastus.orientate.utils.DateUtils;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;


@ParseClassName("Conversation")
public class Conversation extends ParseObject {
    public static final String KEY_CREATOR = "creator";
    public static final String KEY_TITLE = "title";
    public static final String KEY_LAST_MESSAGE = "last_message";
    public static final String KEY_PROFILE_IMAGE = "profile_picture";

    private ChatMessage mLastMessage;


    public String getTitle() {
        return getString(KEY_TITLE);
    }


    public ParseUser getCreator() {
        return getParseUser(KEY_CREATOR);
    }

    public String getProfileImageUrl() {
        return getParseFile(KEY_PROFILE_IMAGE).getUrl();
    }

    @LateInit
    public ChatMessage getLastMessage() {
        return mLastMessage;
    }

    public void setLastMessage(ChatMessage mLastMessage) {
        this.mLastMessage = mLastMessage;
    }

    @LateInit
    public String getLastMessageText() {
        if (mLastMessage == null) {
            return null;
        }
        return mLastMessage.getContent();
    }

    public String getLastSeen() {
        if (mLastMessage == null) {
            return "";
        } else {
            return DateUtils.getRelativeTimeAgo(mLastMessage.getCreatedAt());
        }
    }
}
