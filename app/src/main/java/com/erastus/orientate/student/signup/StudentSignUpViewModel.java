package com.erastus.orientate.student.signup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.parse.ParseFile;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class StudentSignUpViewModel extends ViewModel {

    private static SignUpRepository mRepo;

    private MutableLiveData<InputValid> mSignUpResult = new MutableLiveData<>();

    public StudentSignUpViewModel() {
        mRepo = SignUpRepository.getInstance();
    }

    private MutableLiveData<String> userName = new MutableLiveData<>();

    private MutableLiveData<String> mEmail = new MutableLiveData<>();

    private MutableLiveData<String> mPassword = new MutableLiveData<>();

    private MutableLiveData<String> mFullName = new MutableLiveData<>();

    private MutableLiveData<String> mDateOfBirth = new MutableLiveData<>();

    private MutableLiveData<File> mProfilePicture = new MutableLiveData<>();

    public void userNameChanged(String username) {
        userName.setValue(username);
    }

    public void emailChanged(String email) {
        mEmail.setValue(email);
    }

    public void passwordChanged(String password) {
        mPassword.setValue(password);
    }

    public void fullNameChanged(String fullName) {
        mFullName.setValue(fullName);
    }

    public void dateOfBirthChanged(String dateOfBirth) {
        mDateOfBirth.setValue(dateOfBirth);
    }

    public void profilePictureChanged(File file) {
        mProfilePicture.setValue(file);
    }

    public void signUp() {
        String[] string = mFullName.getValue().split(" ", 2);
        mRepo.signUpStudent(userName.getValue(), mPassword.getValue(),
                mEmail.getValue(), getParseFile(),
                string[0], getMiddleName(string),
                string[1], getDob());
    }

    private String getMiddleName(String[] string) {
        String[] middle = Arrays.copyOfRange(string, 1, string.length - 1);
        return String.join(" ", middle);
    }


    public LiveData<InputValid> getSignUpResult() {
        return mSignUpResult;
    }

    private Date getDob() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/mm/dd", Locale.getDefault());
        Date date = null;
        try {
            String s = mDateOfBirth.getValue();
            if (s != null) {
                date = format.parse(mDateOfBirth.getValue());
            }
        } catch (ParseException e) {
            e.printStackTrace();
            date = null;
            mSignUpResult.setValue(new InputValid(e.getLocalizedMessage()));
        }
        return date;
    }


    private ParseFile getParseFile() {
        File file = mProfilePicture.getValue();
        if (file == null) {
            return null;
        }
        return new ParseFile(file);
    }
}
