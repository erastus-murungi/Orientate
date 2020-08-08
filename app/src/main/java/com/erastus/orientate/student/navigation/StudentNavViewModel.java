package com.erastus.orientate.student.navigation;

import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.erastus.orientate.institution.models.Institution;
import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.student.models.SimpleState;
import com.parse.ParseUser;

public class StudentNavViewModel extends ViewModel {
    private static final String TAG = "StudentNavViewModel";

    /**
     * To make it easier to manipulate the action bar from within the EventDetailFragment
     */
    private MutableLiveData<ActionBarStatus> mStatus = new MutableLiveData<>();

    private MutableLiveData<ExtendedParseUser> mUser =
            new MutableLiveData<>(new ExtendedParseUser(ParseUser.getCurrentUser()));

    public MutableLiveData<ExtendedParseUser> getUser() {
        return mUser;
    }

    public MutableLiveData<ActionBarStatus> getActionBarStatus() {
        return mStatus;
    }

    private LiveData<SimpleState<Institution>> mStudentInstitution;

    @SuppressWarnings("unchecked")
    public StudentNavViewModel() {
        StudentNavRepository repo = StudentNavRepository.getInstance();
        mStudentInstitution = Transformations.map(repo.getStudentInstitution(), input -> {
            if (input instanceof DataState.Success) {
                return new SimpleState<>(((DataState.Success<Institution>) input).getData());
            } else if (input instanceof DataState.Error) {
                Exception e = ((DataState.Error) input).getError();
                Log.e(TAG, "apply: ", e);
                return new SimpleState<>(e.getLocalizedMessage());
            }
            else {
                return new SimpleState<>((Boolean) true);
            }
        });
    }

    public LiveData<SimpleState<Institution>> getStudentInstitution() {
        return mStudentInstitution;
    }

}
