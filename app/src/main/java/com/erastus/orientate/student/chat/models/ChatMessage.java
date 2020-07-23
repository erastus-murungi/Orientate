package com.erastus.orientate.student.chat.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;


@ParseClassName("Message")
public class ChatMessage extends ParseObject {
    public static final String KEY_CONVERSATION = "conversation";
    public static final String KEY_SENDER = "sender";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_MESSAGE_TYPE = "type";
}
