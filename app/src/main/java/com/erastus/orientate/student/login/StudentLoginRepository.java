package com.erastus.orientate.student.login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.erastus.orientate.student.models.DataState;
import com.parse.ParseUser;

public class StudentLoginRepository {
    private static volatile StudentLoginRepository instance;


    private MutableLiveData<DataState> mState = new MutableLiveData<>();

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
    public void login(String username, String password) {
        // handle login on a background thread

        ParseUser.logInInBackground(username, password, (user, e) -> {
            if (e == null) {
                mState.setValue(new DataState.Success<>(user));
            } else {
                mState.setValue(new DataState.Error(e));
            }
        });
    }

    public MutableLiveData<DataState> getState() {
        return mState;
    }
}
