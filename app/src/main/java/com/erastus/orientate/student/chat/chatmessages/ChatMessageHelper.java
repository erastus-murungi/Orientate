package com.erastus.orientate.student.chat.chatmessages;

import com.erastus.orientate.student.chat.chatmessages.models.ChatMessage;
import com.erastus.orientate.student.chat.chatmessages.models.MessageType;

import java.util.List;
import java.util.concurrent.TimeUnit;

class ChatMessageHelper {

    private static final int HEADER_FULL = 10;
    private static final int HEADER = 20;
    private static final int MIDDLE = 30;
    private static final int END = 40;

    private ChatMessageHelper() {
    }


    static void chainMessages(List<ChatMessage> messages, int count) {

        int limit = count;
        if (limit > messages.size()) {
            limit = messages.size();
        }

        for (int i = 0; i < limit; i++) {
            ChatMessage message = messages.get(i);
            if (i > 0) {
                ChatMessageHelper.chain(message, messages.get(i - 1));
            } else {
                message.setMessageType(MessageType.OWN_HEADER_FULL);
            }
        }
    }


    static void chain(ChatMessage currentMsg, ChatMessage previousMsg) {

        long diffToPrev = (currentMsg.getCreatedAt().getTime() - previousMsg.getCreatedAt().getTime()) / 10_000L;

        long offset = TimeUnit.MINUTES.toMillis(1);

        boolean ownMessage = previousMsg.getSender().getObjectId().equals(currentMsg.getSender().getObjectId());
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
            return instance.getMessageType() == MessageType.OWN_HEADER_FULL || instance.getMessageType() == MessageType.OWN_HEADER_FULL;
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
        return false;
    }

    private static int assignType(ChatMessage instance, int type) {
        if (type == HEADER_FULL) {
            return instance.isOwnMessage() ? MessageType.OWN_HEADER_FULL : MessageType.REC_HEADER_FULL;
        }
        if (type == HEADER) {
            return instance.isOwnMessage() ? MessageType.OWN_HEADER_SERIES : MessageType.REC_HEADER_SERIES;
        }
        if (type == MIDDLE) {
            return instance.isOwnMessage() ? MessageType.OWN_MIDDLE : MessageType.REC_MIDDLE;
        }
        if (type == END) {
            return instance.isOwnMessage() ? MessageType.OWN_END : MessageType.REC_END;
        }
        return -1;
    }
}
