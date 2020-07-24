package com.erastus.orientate.student.chat.chatmessages.models;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.erastus.orientate.models.GenericUser;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.boltsinternal.Task;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@ParseClassName("ChatMessage")
public class ChatMessage extends ParseObject {
        private static final String TAG = "ChatMessage";

        public static final String KEY_CONVERSATION = "conversation";
        public static final String KEY_SENDER = "sender";
        public static final String KEY_CONTENT = "content";
        public static final String KEY_MESSAGE_TYPE = "type";
        public static final String KEY_USER = "user";


        private int messageType;
        private boolean isOwnMessage;


        public int getMessageType() {
                return messageType;
        }

        public void setMessageType(int messageType) {
                this.messageType = messageType;
        }

        public boolean isOwnMessage() {
                return isOwnMessage;
        }

        public void setOwnMessage(boolean ownMessage) {
                isOwnMessage = ownMessage;
        }

        public String getSenderId() {
                return getParseObject(KEY_SENDER).getObjectId();
        }

        /**
         * This is a blocking method and should be used with LiveData
         * @return
         */
        public ParseUser getSender() {
                ParseUser sender = null;
                try {
                        sender = getParseUser(KEY_SENDER).fetchIfNeeded();
                } catch (ParseException e) {
                        e.printStackTrace();
                        Log.e(TAG, "getSender: ", e);
                }
                catch (NullPointerException e) {
                        Log.e(TAG, "getSender: getParseUser(KEY_SENDER) returned null", e);
                }
                return sender;
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

}
