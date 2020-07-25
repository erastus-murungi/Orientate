package com.erastus.orientate.student.chat.chatmessages;

import android.util.Log;

import com.erastus.orientate.student.chat.chatmessages.models.ChatMessage;
import com.erastus.orientate.student.chat.chatmessages.models.MessageType;
import com.erastus.orientate.student.login.StudentLoginRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;

class ChatMessageHelper {
    private static final String TAG = "ChatMessageHelper";

    private static final int HEADER_FULL = 10;
    private static final int HEADER = 20;
    private static final int MIDDLE = 30;
    private static final int END = 40;
    private static String myId;

    static {
        myId = StudentLoginRepository.getInstance().getLoggedInStudent().getObjectId();
    }

    private ChatMessageHelper() {
    }


    static void chainMessages(List<ChatMessage> messages, int count) {

        int limit = count;
        if (limit > messages.size()) {
            limit = messages.size();
        }
        if (messages.size() > 0) {
            messages.get(0).setMessageType(assignType(messages.get(0), HEADER_FULL));
            for (int i = 1; i < limit; i++) {
                ChatMessage message = messages.get(i);
                ChatMessageHelper.chain(message, messages.get(i - 1));
            }
            for (ChatMessage message: messages) {
                if (message.getMessageType() < 1 || message.getMessageType() > 8) {
                    Log.e(TAG, "chainMessages: message.getObjectId()" + " has a viewType of " + message.getMessageType());
                    message.setMessageType(MessageType.OWN_MIDDLE);
                }
            }
        }
    }


    static void chain(ChatMessage currentMsg, ChatMessage previousMsg) {

        long diffToPrev = (currentMsg.getCreatedAt().getTime() - previousMsg.getCreatedAt().getTime()) / 10_000L;

        long offset = TimeUnit.MINUTES.toMillis(1);

        boolean ownMessage = currentMsg.getSender().getObjectId().equals(previousMsg.getSender().getObjectId());
        boolean chainable = false;

        if (ownMessage)
            chainable = (diffToPrev <= offset);

        if (ownMessage) {
            if (chainable) {
                currentMsg.setMessageType(assignType(currentMsg, END));

                if (isTypeOf(previousMsg, HEADER_FULL)) {
                    previousMsg.setMessageType(assignType(previousMsg, HEADER));
                } else if (isTypeOf(previousMsg, END)) {
                    previousMsg.setMessageType(assignType(previousMsg, MIDDLE));
                }
            } else {
                currentMsg.setMessageType(assignType(currentMsg, HEADER_FULL));
                if (!isTypeOf(previousMsg, HEADER_FULL)) {
                    previousMsg.setMessageType(assignType(previousMsg, END));
                }
            }
        } else {
            currentMsg.setMessageType(assignType(currentMsg, HEADER_FULL));
        }

    }

    private static boolean isTypeOf(ChatMessage instance, int type) {
        if (type == HEADER_FULL) {
            return instance.getMessageType() == MessageType.OWN_HEADER_FULL || instance.getMessageType() == MessageType.REC_HEADER_FULL;
        }
        if (type == HEADER) {
            return instance.getMessageType() == MessageType.OWN_HEADER_SERIES || instance.getMessageType() == MessageType.REC_HEADER_SERIES;
        }
        if (type == MIDDLE) {
            return instance.getMessageType() == MessageType.OWN_MIDDLE || instance.getMessageType() == MessageType.REC_MIDDLE;
        }
        if (type == END) {
            return instance.getMessageType() == MessageType.OWN_END || instance.getMessageType() == MessageType.REC_END;
        }
        Log.e(TAG, "isTypeOf: " + instance.getObjectId() + " " + instance, new IllegalStateException());
        return false;
    }

    private static int assignType(ChatMessage instance, int type) {
        if (type == HEADER_FULL) {
            return isOwnMessage(instance) ? MessageType.OWN_HEADER_FULL : MessageType.REC_HEADER_FULL;
        }
        if (type == HEADER) {
            return isOwnMessage(instance) ? MessageType.OWN_HEADER_SERIES : MessageType.REC_HEADER_SERIES;
        }
        if (type == MIDDLE) {
            return isOwnMessage(instance) ? MessageType.OWN_MIDDLE : MessageType.REC_MIDDLE;
        }
        if (type == END) {
            return isOwnMessage(instance) ? MessageType.OWN_END : MessageType.REC_END;
        }
        Log.e(TAG, "assignType: " + instance.getObjectId() + " " + instance, new IllegalStateException());
        return -1;
    }

    private static boolean isOwnMessage(ChatMessage message) {
        return message.getSender().getObjectId().equals(myId);
    }
}
