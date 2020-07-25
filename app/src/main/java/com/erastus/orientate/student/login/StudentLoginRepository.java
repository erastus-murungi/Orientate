package com.erastus.orientate.student.login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.student.models.Student;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class StudentLoginRepository {
    private static volatile StudentLoginRepository instance;

    private Student mLoggedInStudent;
    private MutableLiveData<DataState> mStudentState = new MutableLiveData<>();

    public Student getLoggedInStudent() {
        return mLoggedInStudent;
    }

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
                requestStudent();
                mState.setValue(new DataState.Success<>(user));
            } else {
                mState.setValue(new DataState.Error(e));
            }
        });
    }

    private void requestStudent() {
        ParseQuery<Student> query = ParseQuery.getQuery(Student.class);
        query.include(Student.KEY_INSTITUTION);
        query.whereEqualTo(Student.KEY_USER, ParseUser.getCurrentUser());
        query.getFirstInBackground((object, e) -> {
            if (e == null) {
                mLoggedInStudent = object;
                mStudentState.setValue(new DataState.Success<>(object));
            } else {
                mState.setValue(new DataState.Error(e));
            }
        });
    }

    public MutableLiveData<DataState> getState() {
        return mState;
    }

    public MutableLiveData<DataState> loadStudent() {
        requestStudent();
        return mStudentState;
    }
}
