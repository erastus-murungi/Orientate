package com.erastus.orientate.student.login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.student.models.Student;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class StudentLoginRepository {
    private static volatile StudentLoginRepository instance;

    private MutableLiveData<DataState> mStudentState = new MutableLiveData<>();

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
                ParseObject s = user.getParseUser(ExtendedParseUser.KEY_STUDENT);
                if (s == null) {
                    mState.setValue(new DataState.Error(new NullPointerException()));
                } else {
                    s.fetchIfNeededInBackground((object, e1) -> {
                        if (e1 == null) {
                            mState.setValue(new DataState.Success<>(user));
                        } else {
                            mState.setValue(new DataState.Error(e1));
                        }
                    });
                }
            } else {
                mState.setValue(new DataState.Error(e));
            }
        });
    }

    public MutableLiveData<DataState> getState() {
        return mState;
    }

    public MutableLiveData<DataState> loadStudent() {
        return mStudentState;
    }
}
