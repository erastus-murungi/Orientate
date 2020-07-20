package com.erastus.orientate.student.signup.dob;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DobViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private SimpleDateFormat mCheckDate = new SimpleDateFormat("yyyy/mm/dd", Locale.getDefault());
    private MutableLiveData<Boolean> mDateIsValid = new MutableLiveData<>();

    public LiveData<Boolean> getDateIsValid() {
        return mDateIsValid;
    }

    public void dateChanged(String date) {
        mCheckDate.setLenient(false);
        try {
            mCheckDate.parse(date);
        } catch (ParseException e) {
            mDateIsValid.setValue(false);
        }

    }
}