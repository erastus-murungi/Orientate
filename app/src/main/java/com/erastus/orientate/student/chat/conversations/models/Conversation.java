package com.erastus.orientate.student.chat.conversations.models;

import android.util.Log;

import com.erastus.orientate.student.chat.chatmessages.models.Message;
import com.erastus.orientate.student.chat.conversations.FetchedLazily;
import com.erastus.orientate.utils.DateUtils;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;


@ParseClassName("Conversation")
public class Conversation extends ParseObject {

    public static final String TAG = "Conversation";

    public static final String KEY_CREATOR = "creator";
    public static final String KEY_TITLE = "title";
    public static final String KEY_LAST_MESSAGE = "last_message";
    public static final String KEY_PROFILE_IMAGE = "profile_picture";
    public static final String KEY_PARTICIPANTS = "participants";
    public static final String KEY_IS_SYSTEM_GENERATED = "is_system_generated";

    private Message mLastMessage;


    public String getTitle() {
        return getString(KEY_TITLE);
    }

    @FetchedLazily
    public ParseUser getCreator() {
        try {
            return getParseUser(KEY_CREATOR).fetch();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "getCreator: ", e);
            return null;
        }
    }

    public String getProfileImageUrl() {
        ParseFile file = getParseFile(KEY_PROFILE_IMAGE);
        try {
            return file.getUrl();
        } catch (NullPointerException e) {
            return null;
        }
    }


    public void setLastMessage(Message mLastMessage) {
        this.mLastMessage = mLastMessage;
    }

    @FetchedLazily
    public Message getLastMessage() {
        try {
            return getParseObject(KEY_LAST_MESSAGE).fetch();
        } catch (ParseException e) {
            Log.e("Conversation", "getLastMessage: ", e);
        }
        return null;
    }


    @FetchedLazily
    public String getLastMessageText() {
        try {
            return getLastMessage().getContent();
        } catch (NullPointerException e) {
            return "";
        }
    }

    public String getLastSeen() {
        if (mLastMessage == null) {
            return "";
        } else {
            return DateUtils.getRelativeTimeAgo(mLastMessage.getCreatedAt());
        }
    }

    public JSONArray getParticipants() {
        return getJSONArray(KEY_PARTICIPANTS);
    }

    public boolean getIsSystemGenerated() {
        return getBoolean(KEY_IS_SYSTEM_GENERATED);
    }

}
