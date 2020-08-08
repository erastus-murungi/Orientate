package com.erastus.orientate.student.cluster;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.utils.CompareUsers;
import com.erastus.orientate.utils.ParallelVpTree;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ClusterRepository {

    private static final String TAG = "ClusterRepository";
    private ParallelVpTree<ExtendedParseUser> vpTree;
    private static MutableLiveData<List<ExtendedParseUser>> sUsersInChatRooms = new MutableLiveData<>(new ArrayList<>());
    private static MutableLiveData<List<Conversation>> sConversations = new MutableLiveData<>(new ArrayList<>());
    private static Map<String, List<ExtendedParseUser>> sConversationToParticipantsMap = new HashMap<>();
    private static MutableLiveData<Exception> sExceptions = new MutableLiveData<>();
    private static AtomicInteger sNumberConversations = new AtomicInteger(0);
    private static CompareUsers sCompareUsers = new CompareUsers();

    private static int defaultThreshold = 6;
    private static int maxGroupSize = defaultThreshold;

    public static volatile ClusterRepository sInstance;

    public static ClusterRepository getInstance() {
        if (sInstance == null) {
            sInstance = new ClusterRepository();
        }
        return sInstance;
    }

    // listen for a new users
    public static void setUpNewUserSubscription() {
        waitForAllParticipantsToBeFetched();
        // we are looking for messages
        ParseQuery<ParseUser> userParseQuery = ParseQuery.getQuery(ParseUser.class);

        // PERSONAL BEWARE!!! whereContains does not work
        userParseQuery.include(ExtendedParseUser.KEY_USER_INFO);

        userParseQuery.whereEqualTo(ExtendedParseUser.KEY_WANTS_ROOM, true);

        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        parseLiveQueryClient
                .subscribe(userParseQuery)
                .handleEvents((query, event, newUser) -> {
                    if (event == SubscriptionHandling.Event.CREATE ||
                            event == SubscriptionHandling.Event.UPDATE) {
                        new Handler(Looper.getMainLooper())
                                .post(() ->
                                        ParallelVpTree.
                                                TaskRunner.
                                                getInstance().
                                                executeAsync(
                                                        () -> respondToNewUser(new ExtendedParseUser(newUser)),
                                                        (conversation) -> conversation.saveEventually(e -> {
                                                                            if (e != null) {
                                                                                sExceptions.postValue(e);
                                                                            }
                                                                        }
                                                                )
                                                )
                                );
                    }
                });
    }

    private static void waitForAllParticipantsToBeFetched() {
        while (sConversations.getValue() != null
                && sNumberConversations.get() != sConversations.getValue().size()) ;
    }

    private static Conversation respondToNewUser(ExtendedParseUser newUser) {
        List<ExtendedParseUser> users = sUsersInChatRooms.getValue();
        if (users == null) {
            Log.e(TAG, "respondToNewUser: ", new NullPointerException());
            return null;
        }
        users.add(newUser);

        List<Conversation> conversations = sConversations.getValue();
        if (conversations == null) {
            Log.e(TAG, "respondToNewUser: ", new NullPointerException());
            return null;
        }

        if (users.size() <= defaultThreshold) {
            // no need to run the algorithm
            if (conversations.size() != 1) {
                Log.e(TAG, "respondToNewUser: ",
                        new IllegalStateException("No. users in chat-rooms <= threshold, " +
                                "but number of chat-rooms is + " + conversations.size()));
            } else {
                Conversation one = conversations.get(0);
                one.addUnique(Conversation.KEY_PARTICIPANTS, newUser.getStudent().getObjectId());
            }
            return null;
        }

        // find all the partially filled groups
        List<Conversation> partialConversations = conversations
                .stream()
                .filter((conversation -> conversation.getParticipants().length() < maxGroupSize))
                .collect(Collectors.toList());

        if (partialConversations.size() == 0) {
            if (conversations.size() == 0) {
                return createNewConversationWithUser(newUser);
            } else {
                maxGroupSize += 1;
                return findConversationForNewUser(conversations, newUser);
            }
        } else {
            return findConversationForNewUser(conversations, newUser);
        }

    }

    private static Conversation findConversationForNewUser(List<Conversation> conversations, ExtendedParseUser newUser) {
        Conversation matchConversation = null;
        double matchScore = Double.POSITIVE_INFINITY;
        for (Conversation conversation : conversations) {
            List<ExtendedParseUser> participants = sConversationToParticipantsMap.get(conversation.getObjectId());
            if (participants != null) {
                List<Double> distances = participants.parallelStream().map((participant) -> {
                            try {
                                return sCompareUsers.compare(participant, newUser);
                            } catch (JSONException e) {
                                sExceptions.postValue(e);
                                return 0.0d;
                            }
                        }
                ).collect(Collectors.toList());
                OptionalDouble averageDistance = distances.parallelStream().mapToDouble((x) -> x).average();
                if (averageDistance.isPresent() && averageDistance.getAsDouble() < matchScore) {
                    matchConversation = conversation;
                    matchScore = averageDistance.getAsDouble();
                }
            } else {
                sExceptions.postValue(new NullPointerException("Participants array is null"));
                return null;
            }
        }
        matchConversation.add(Conversation.KEY_PARTICIPANTS, newUser.getObjectId());
        return matchConversation;
    }

    private static Conversation createNewConversationWithUser(ExtendedParseUser newUser) {
        Conversation conversation = new Conversation();
        conversation.add(Conversation.KEY_PARTICIPANTS, newUser.getObjectId());
        return conversation;
    }

    private static List<String> fromJsonArray(JSONArray array) throws JSONException {
        List<String> a = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            a.add(i, array.getString(i));
        }
        return a;
    }

    private static void fetchAllParticipants() {
        for (Conversation conversation : Objects.requireNonNull(sConversations.getValue())) {
            fetchParticipants(conversation);
        }
    }

    public static void fetchParticipants(Conversation conversation) {
        JSONArray array = conversation.getParticipants();
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        try {
            query.whereContainedIn(Conversation.KEY_OBJECT_ID, fromJsonArray(array));
        } catch (JSONException e) {
            Log.e(TAG, "fetchParticipants: ", e);
        }
        query.include(ExtendedParseUser.KEY_STUDENT);

        query.findInBackground((participants, e) -> {
            if (e == null) {
                sNumberConversations.incrementAndGet();
                sConversationToParticipantsMap.put(
                        conversation.getObjectId(),
                        participants.stream().map(ExtendedParseUser::new).collect(Collectors.toList())
                );
            } else {
                sExceptions.postValue(e);
            }
        });
    }

    public static void init() {
        fetchAllParticipants();
        setUpNewUserSubscription();
    }
}
