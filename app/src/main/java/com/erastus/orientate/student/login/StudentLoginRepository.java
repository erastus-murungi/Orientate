package com.erastus.orientate.student.login;

import com.erastus.orientate.student.models.DataState;
import com.parse.ParseUser;
import com.parse.boltsinternal.Task;

import java.util.concurrent.TimeUnit;

public class StudentLoginRepository {
    private static volatile StudentLoginRepository instance;
    public static final long maxDuration = 50;

    // private constructor : singleton access

    public static synchronized StudentLoginRepository getInstance() {
        if (instance == null) {
            instance = new StudentLoginRepository();
        }
        return instance;
    }

    public void logout() {
        ParseUser.logOut();
    }

    // API requests
    public DataState<ParseUser> login(String username, String password) {
        // handle login on a background thread
        Task<ParseUser> parseUserTask = ParseUser.logInInBackground(username, password);
        try {
            parseUserTask.waitForCompletion(maxDuration, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (parseUserTask.isCompleted() && parseUserTask.getError() == null) {
            return new DataState.Success<>(parseUserTask.getResult());
        } else if (!parseUserTask.isCompleted() && parseUserTask.getError() == null) {
            return new DataState.TimedOut(maxDuration);
        } else {
            Exception exception = parseUserTask.getError();
            return new DataState.Error(exception == null ? new Exception() : exception);
        }
    }
}
