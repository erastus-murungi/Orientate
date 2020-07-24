package com.erastus.orientate.student.chat.chatmessages.models;

        import com.erastus.orientate.models.GenericUser;
        import com.parse.ParseClassName;
        import com.parse.ParseObject;

@ParseClassName("ChatMessage")
public class ChatMessage extends ParseObject {

        public static final String KEY_CONVERSATION = "conversation";
        public static final String KEY_SENDER = "sender";
        public static final String KEY_CONTENT = "content";
        public static final String KEY_MESSAGE_TYPE = "type";


        private MessageType messageType;
        private boolean isOwnMessage;


        public GenericUser getUser() {
                return null;
        }

        public MessageType getMessageType() {
                return messageType;
        }

        public void setMessageType(MessageType messageType) {
                this.messageType = messageType;
        }

        public boolean isOwnMessage() {
                return isOwnMessage;
        }

        public void setOwnMessage(boolean ownMessage) {
                isOwnMessage = ownMessage;
        }
}
