package com.erastus.orientate.student.navigation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.erastus.orientate.applications.App;
import com.erastus.orientate.institution.models.Institution;
import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.student.models.Student;
import com.parse.GetCallback;
import com.parse.ParseException;

public class StudentNavRepository {

    private static volatile StudentNavRepository sInstance;

    private static MutableLiveData<DataState> mInstitution = new MutableLiveData<>();


    public static synchronized StudentNavRepository getInstance() {
        if (sInstance == null) {
            sInstance = new StudentNavRepository();
        }
        return sInstance;
    }

    public LiveData<DataState> getStudentInstitution() {
        ExtendedParseUser loggedInUser = App.get().getCurrentUser();
        loggedInUser.getStudentParseObject().fetchIfNeededInBackground((loggedInStudent, e) -> {
            if (e == null) {
                loggedInStudent.getParseObject(Student.KEY_ENROLLED_AT).
                        fetchIfNeededInBackground(
                                (institution, e1) -> {
                                    if (e1 == null) {
                                        mInstitution.postValue(new DataState.Success<>(institution));
                                    } else {
                                        mInstitution.postValue(new DataState.Error(e1));
                                    }
                                });
            } else {
                mInstitution.postValue(new DataState.Error(e));
            }
        });
        return mInstitution;
    }
}
