package com.erastus.orientate.student.signup.name;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.erastus.orientate.R;
import com.erastus.orientate.student.signup.InputValid;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;


public class NameEmailViewModel extends ViewModel {
    // Regex to check valid username.
    private static String sRegexUsername = "^[aA-zZ]\\w{5,29}$";

    // Regex to check valid name.
    private static String sRegexFullName = "^\\p{L}+[\\p{L}\\p{Z}\\p{P}]*";

    // Compile the Regex
    private static Pattern mUsernamePattern = Pattern.compile(sRegexUsername);

    private static Pattern mFullNamePattern = Pattern.compile(sRegexFullName);


    private MutableLiveData<InputValid> mUserNameAcceptable = new MutableLiveData<>(new InputValid(R.string.empty_username));

    private MutableLiveData<InputValid> mFullNameAcceptable = new MutableLiveData<>(new InputValid(R.string.empty_name));

    private MutableLiveData<InputValid> mEmailAcceptable = new MutableLiveData<>(new InputValid(R.string.empty_email));

    // To control whether user can move on to the next fragment
    private MediatorLiveData<Boolean> mInputAcceptable = setUpMediatorLiveData();

    private MediatorLiveData<Boolean> setUpMediatorLiveData() {
        MediatorLiveData<Boolean> proceed = new MediatorLiveData<>();

        Observer<InputValid> observer = inputValid -> {
            boolean allOkay = true;
            allOkay = allOkay && mUserNameAcceptable.getValue().isDataValid();
            allOkay = allOkay && mEmailAcceptable.getValue().isDataValid();
            allOkay = allOkay && mFullNameAcceptable.getValue().isDataValid();
            mInputAcceptable.setValue(allOkay);
        };
        proceed.addSource(mFullNameAcceptable, observer);
        proceed.addSource(mEmailAcceptable, observer);
        proceed.addSource(mUserNameAcceptable, observer);
        return proceed;
    }

    public LiveData<InputValid> getUserNameAcceptable() {
        return mUserNameAcceptable;
    }

    public LiveData<InputValid> getFullNameAcceptable() {
        return mFullNameAcceptable;
    }

    public LiveData<InputValid> getEmailAcceptable() {
        return mEmailAcceptable;
    }

    public LiveData<Boolean> getInputAcceptable() {
        return mInputAcceptable;
    }

    public NameEmailViewModel() {
    }


    public void emailInputChanged(String email) {
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
            mEmailAcceptable.setValue(new InputValid(true));
        } catch (AddressException e) {
            mEmailAcceptable.setValue(new InputValid(e.getMessage()));
        }
    }

    public void usernameInputChanged(@NonNull String userName) {
        if (TextUtils.isEmpty(userName)) {
            mUserNameAcceptable.setValue(new InputValid(R.string.empty_username));
        }
        Matcher m = mUsernamePattern.matcher(userName.trim());

        if (m.matches()) {
            mUserNameAcceptable.setValue(new InputValid(true));
        } else {
            mUserNameAcceptable.setValue(new InputValid(R.string.invalid_username));
        }
    }

    public void fullNameInputChanged(String fullName) {
        if (TextUtils.isEmpty(fullName)) {
            mFullNameAcceptable.setValue(new InputValid(R.string.empty_name));
        }
        Matcher m = mFullNamePattern.matcher(fullName.trim());

        if (m.matches()) {
            mFullNameAcceptable.setValue(new InputValid(true));
        } else {
            mFullNameAcceptable.setValue(new InputValid(R.string.invalid_name));
        }
    }
}