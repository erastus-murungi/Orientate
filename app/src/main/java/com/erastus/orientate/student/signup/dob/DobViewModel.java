package com.erastus.orientate.student.signup.dob;

import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.erastus.orientate.R;
import com.erastus.orientate.student.signup.InputValid;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DobViewModel extends ViewModel {
    private SimpleDateFormat mCheckDate = new SimpleDateFormat("yyyy/mm/dd", Locale.getDefault());

    // data is initially valid because the date of birth is optional
    private MutableLiveData<InputValid> mDateIsValid = new MutableLiveData<>(new InputValid(true));

    private MutableLiveData<InputValid> mPasswordValid = new MutableLiveData<>(new InputValid(false));

    private MutableLiveData<InputValid> mConfirmPasswordValid = new MutableLiveData<>(new InputValid(false));

    private MutableLiveData<Bitmap> mImageBitMap = new MutableLiveData<>();

    // To control whether user can move on to the next fragment
    private MediatorLiveData<Boolean> mInputAcceptable = setUpMediatorLiveData();

    private MediatorLiveData<Boolean> setUpMediatorLiveData() {
        MediatorLiveData<Boolean> proceed = new MediatorLiveData<>();

        Observer<InputValid> observer = inputValid -> {
            boolean allOkay = true;
            allOkay = allOkay && mPasswordValid.getValue().isDataValid();
            allOkay = allOkay && mConfirmPasswordValid.getValue().isDataValid();
            allOkay = allOkay && mDateIsValid.getValue().isDataValid();
            mInputAcceptable.setValue(allOkay);
        };
        proceed.addSource(mPasswordValid, observer);
        proceed.addSource(mConfirmPasswordValid, observer);
        proceed.addSource(mDateIsValid, observer);
        return proceed;
    }

    // getters
    public LiveData<InputValid> getDateIsValid() {
        return mDateIsValid;
    }

    public LiveData<InputValid> getPasswordValid() {
        return mPasswordValid;
    }

    public LiveData<InputValid> getConfirmPasswordValid() {
        return mConfirmPasswordValid;
    }


    public void dateChanged(String date) {
        if (TextUtils.isEmpty(date)) {
            mDateIsValid.setValue(new InputValid(true));
            return;
        }
        mCheckDate.setLenient(false);
        try {
            mCheckDate.parse(date);
            mDateIsValid.setValue(new InputValid(true));
        } catch (ParseException e) {
            mDateIsValid.setValue(new InputValid(e.getLocalizedMessage()));
        }

    }

    public void passwordChanged(String password) {
        if (isPasswordValid(password)) {
            mPasswordValid.setValue(new InputValid(true));
        } else {
            mPasswordValid.setValue(new InputValid(R.string.invalid_password));
        }
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= 8;
    }


    public void confirmPasswordChanged(String password, String confirmPassword) {
        if (mPasswordValid.getValue().isDataValid() && password.equals(confirmPassword)) {
            mConfirmPasswordValid.setValue(new InputValid(true));
        } else {
            if (!mPasswordValid.getValue().isDataValid()) {
                mConfirmPasswordValid.setValue(new InputValid(R.string.invalid_initial_password));
            } else {
                mConfirmPasswordValid.setValue(new InputValid(R.string.passwords_dont_match));
            }
        }
    }

    public LiveData<Bitmap> getImageBitMap() {
        return mImageBitMap;
    }

    public void bitmapChanged(Bitmap bitmap) {
        mImageBitMap.setValue(bitmap);
    }

    public LiveData<Boolean> getInputAcceptable() {
        return mInputAcceptable;
    }
}