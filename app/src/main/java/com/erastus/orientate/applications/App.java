package com.erastus.orientate.applications;

import android.app.Application;

import com.erastus.orientate.R;
import com.erastus.orientate.student.chat.chatmessages.models.ChatMessage;
import com.erastus.orientate.institution.models.Institution;
import com.erastus.orientate.models.GenericUser;
import com.erastus.orientate.student.announcements.models.Announcement;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.event.models.Event;
import com.erastus.orientate.student.models.Student;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class App extends Application {
    public static final String APP_ID = "erastus-orientate";
    public static final String SERVER_URL = "https://erastus-orientate.herokuapp.com/parse/";
    private ParseUser mCurrentUser;

    public ParseUser getCurrentUser() {
        if (mCurrentUser == null) {
            mCurrentUser = ParseUser.getCurrentUser();
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
        ParseObject.registerSubclass(GenericUser.class);
        ParseObject.registerSubclass(Institution.class);
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Student.class);
        ParseObject.registerSubclass(ChatMessage.class);
        ParseObject.registerSubclass(Conversation.class);


        //TODO only for troubleshooting -- remove this line for production
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
    }
}
