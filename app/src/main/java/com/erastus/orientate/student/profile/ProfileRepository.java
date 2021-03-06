package com.erastus.orientate.student.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.student.models.Student;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ProfileRepository {
    private static volatile ProfileRepository repository;
    private MutableLiveData<DataState> mStudentDataState = new MutableLiveData<>();
    private MutableLiveData<DataState> mStudentInstitutionDataState = new MutableLiveData<>();
    private MutableLiveData<ParseUser> mCurrentUser = new MutableLiveData<>();

    public LiveData<ParseUser> getCurrentUser() {
        mCurrentUser.setValue(ParseUser.getCurrentUser());
        return mCurrentUser;
    }

    public LiveData<DataState> getCurrentStudentInstitution() {
        return mStudentInstitutionDataState;
    }

    public static synchronized ProfileRepository getInstance() {
        if (repository == null) {
            repository = new ProfileRepository();
        }
        return repository;
    }

    public LiveData<DataState> getStudent() {
        return mStudentDataState;
    }
}
