package com.erastus.orientate.applications;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.erastus.orientate.R;
import com.erastus.orientate.models.Orientation;
import com.erastus.orientate.models.UserInfo;
import com.erastus.orientate.student.chat.chatmessages.models.Message;
import com.erastus.orientate.institution.models.Institution;
import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.student.announcements.models.Announcement;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.chat.models.Attachment;
import com.erastus.orientate.student.cluster.ClusterRepository;
import com.erastus.orientate.student.event.models.Event;
import com.erastus.orientate.student.models.Student;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class App extends Application {
    public static final String CHANNEL_ID = "NewMessage";
    public static final String CHANNEL_ID_1 = "NewConversation";
    public static final String APP_ID = "erastus-orientate";
    public static final String SERVER_URL = "https://erastus-orientate.herokuapp.com/parse/";
    private ExtendedParseUser mCurrentUser;

    public ExtendedParseUser getCurrentUser() {
        if (mCurrentUser == null) {
            mCurrentUser = new ExtendedParseUser(ParseUser.getCurrentUser());

//            if (mCurrentUser.getIsMaster()) {
            ClusterRepository.init();
//            }
        }

        return mCurrentUser;
    }

    public static synchronized App get() {
        if (sInstance == null) {
            sInstance = new App();
        }
        return sInstance;
    }

    private static App sInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Announcement.class);
        ParseObject.registerSubclass(ExtendedParseUser.class);
        ParseObject.registerSubclass(Institution.class);
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Student.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(Conversation.class);
        ParseObject.registerSubclass(Attachment.class);
        ParseObject.registerSubclass(UserInfo.class);
        ParseObject.registerSubclass(Orientation.class);

        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.networkInterceptors().add(httpLoggingInterceptor);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APP_ID)
                .clientBuilder(builder)
                .clientKey(getString(R.string.MasterKey))
                .server(SERVER_URL).build());


        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Chat",
                    NotificationManager.IMPORTANCE_MIN
            );
            channel.setDescription("Received a new Group message");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_ID_1,
                    "Conversation Found",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Received a new Group message");
            manager.createNotificationChannel(channel1);
        }
    }
}
