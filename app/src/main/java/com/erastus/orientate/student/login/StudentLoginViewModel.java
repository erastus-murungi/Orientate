package com.erastus.orientate.student.login;

import android.util.Patterns;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.erastus.orientate.R;
import com.erastus.orientate.student.login.models.LoginFormState;
import com.erastus.orientate.student.login.models.LoginResult;
import com.erastus.orientate.student.models.DataState;
import com.parse.ParseUser;

public class StudentLoginViewModel extends ViewModel {
    private StudentLoginRepository mStudentLoginRepository;

    public StudentLoginViewModel(StudentLoginRepository studentLoginRepository) {
        this.mStudentLoginRepository = studentLoginRepository;
    }

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    @Nullable
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();


    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        DataState<ParseUser> result = mStudentLoginRepository.login(username, password);

        if (result instanceof DataState.Success) {
            loginResult.setValue(new LoginResult());
        } else if (result instanceof DataState.Error){
            loginResult.setValue(new LoginResult(((DataState.Error) result).getError().getMessage()));
        }
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
        return password != null && password.trim().length() > 5;
    }
}
