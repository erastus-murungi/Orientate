package com.erastus.orientate.student.chat.chatmessages.models;

public abstract class MessageType {
    public static final int OWN_HEADER_FULL = 1;
    public static final int OWN_HEADER_SERIES = 2;
    public static final int OWN_MIDDLE = 3;
    public static final int OWN_END = 4;

    public static final int REC_HEADER_FULL = 5;
    public static final int REC_HEADER_SERIES = 6;
    public static final int REC_MIDDLE = 7;
    public static final int REC_END = 8;

    public static final int DATE = 9;

    public abstract int getMessageType();
}