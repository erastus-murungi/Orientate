package com.erastus.orientate.student.chat.chatmessages;

import com.erastus.orientate.student.chat.chatmessages.models.ChatMessage;
import com.parse.ParseQuery;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

public class ChatMessageRepository {
    private volatile ChatMessageRepository instance;
    private ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

    public synchronized ChatMessageRepository getInstance() {
        if (instance == null) {
            instance = new ChatMessageRepository();
        }
        return instance;
    }


    private void getMessages() {
        ParseQuery<ChatMessage> parseQuery = ParseQuery.getQuery(ChatMessage.class);
        SubscriptionHandling<ChatMessage> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);

        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, (query, object) -> {

        });
    }
}
