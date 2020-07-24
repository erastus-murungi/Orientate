package com.erastus.orientate.student.chat.conversations.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;


@ParseClassName("Conversation")
public class Conversation extends ParseObject {
    public static final String KEY_CREATOR = "creator";
    public static final String KEY_TITLE = "title";
    public static final String KEY_LAST_MESSAGE = "last_message";


    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public ParseObject getLastMessage() {
        return getParseObject(KEY_LAST_MESSAGE);
    }

    public ParseUser getCreator() {
        return getParseUser(KEY_CREATOR);
    }
}
