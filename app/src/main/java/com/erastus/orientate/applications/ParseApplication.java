package com.erastus.orientate.applications;

import android.app.Application;

import com.erastus.orientate.R;
import com.erastus.orientate.institution.models.Institution;
import com.erastus.orientate.models.GenericUser;
import com.erastus.orientate.student.announcements.models.Announcement;
import com.erastus.orientate.student.event.models.Event;
import com.parse.Parse;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {
    public static final String APP_ID = "erastus-orientate";
    public static final String SERVER_URL = "https://erastus-orientate.herokuapp.com/parse/";

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Announcement.class);
        ParseObject.registerSubclass(GenericUser.class);
        ParseObject.registerSubclass(Institution.class);
        ParseObject.registerSubclass(Event.class);


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
