package com.erastus.orientate.student.login;

import android.util.Patterns;

import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.erastus.orientate.R;
import com.erastus.orientate.student.login.models.LoginFormState;
import com.erastus.orientate.student.login.models.LoginResult;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.student.models.SimpleState;
import com.erastus.orientate.student.models.Student;
import com.parse.Parse;
import com.parse.ParseUser;

public class StudentLoginViewModel extends ViewModel {
    private StudentLoginRepository mStudentLoginRepository;
    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<DataState> mDataState;
    private LiveData<SimpleState<Student>> mStudent;

    private MediatorLiveData<SimpleState> mShouldLogIn = new MediatorLiveData<>();

    @Nullable
    private LiveData<SimpleState<ParseUser>> loginResult;

    public StudentLoginViewModel() {
        this.mStudentLoginRepository = StudentLoginRepository.getInstance();
        this.mDataState = mStudentLoginRepository.getState();

        // one-to-one dynamic transformation : Like a Haskell Monad
        // every time mDataState changes, Transformations.switchMap un-subscribes from the previous
        // LiveData object to another provided by the converting function

        this.loginResult = Transformations.switchMap(mStudentLoginRepository.getState(), input -> {
            if (input instanceof DataState.Success) {
                return new MutableLiveData<>(new SimpleState<>(((DataState.Success<ParseUser>) input).getData()));
            } else if (input instanceof DataState.Error) {
                return new MutableLiveData<>(new SimpleState<>(((DataState.Error) input).getError().getLocalizedMessage()));
            }
            return new MutableLiveData<>(new SimpleState<>((Boolean) true));
        });

        mStudent = Transformations.map(mStudentLoginRepository.loadStudent(), input -> {
            if (input instanceof DataState.Success) {
                return new SimpleState<>(((DataState.Success<Student>) input).getData());
            } else if (input instanceof DataState.Error) {
                return new SimpleState<>(((DataState.Error) input).getError().getMessage());
            } else {
                return new SimpleState<>((Boolean) true);
            }
        });
    }


    public LiveData<SimpleState<Student>> getStudent() {
        return mStudent;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    @Nullable
    LiveData<SimpleState<ParseUser>> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        mStudentLoginRepository.login(username, password);
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    public boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= 8;
    }
}
