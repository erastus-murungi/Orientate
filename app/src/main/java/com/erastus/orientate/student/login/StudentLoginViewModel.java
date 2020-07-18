package com.erastus.orientate.student.login;

import android.util.Patterns;

import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.erastus.orientate.R;
import com.erastus.orientate.student.login.models.LoginFormState;
import com.erastus.orientate.student.login.models.LoginResult;
import com.erastus.orientate.student.models.DataState;

public class StudentLoginViewModel extends ViewModel {
    private StudentLoginRepository mStudentLoginRepository;
    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<DataState> mDataState = new MutableLiveData<>();
    @Nullable
    private LiveData<LoginResult> loginResult = new MutableLiveData<>();

    public StudentLoginViewModel(StudentLoginRepository studentLoginRepository) {
        this.mStudentLoginRepository = studentLoginRepository;
        this.mDataState = studentLoginRepository.getState();

        // one-to-one dynamic transformation : Like a Haskell Monad
        // every time mDataState changes, Transformations.switchMap un-subscribes from the previous
        // LiveData object to another provided by the converting function

        this.loginResult = Transformations.switchMap(mDataState, input -> {
            if (mDataState.getValue() instanceof DataState.Success) {
                return new MutableLiveData<>(new LoginResult());
            } else if (mDataState.getValue() instanceof DataState.Error) {
                return new MutableLiveData<>(new LoginResult(((DataState.Error) mDataState.getValue()).getError().getMessage()));
            }
            return new MutableLiveData<>();
        });
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    @org.jetbrains.annotations.Nullable
    LiveData<LoginResult> getLoginResult() {
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
    private boolean isUserNameValid(String username) {
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
