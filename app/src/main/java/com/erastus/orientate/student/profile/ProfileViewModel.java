package com.erastus.orientate.student.profile;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.erastus.orientate.institution.models.Institution;
import com.erastus.orientate.institution.models.LocalInstitution;
import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.student.models.SimpleState;
import com.erastus.orientate.student.models.Student;
import com.parse.ParseUser;

public class ProfileViewModel extends ViewModel {
    private static final String TAG = "ProfileViewModel";

    private LiveData<SimpleState<Student>> mStudent;
    private LiveData<SimpleState<LocalInstitution>> mStudentInstitution;
    private LiveData<ExtendedParseUser> mUser;

    public LiveData<SimpleState<LocalInstitution>> getStudentInstitution() {
        return mStudentInstitution;
    }

    public LiveData<SimpleState<Student>> getStudent() {
        return mStudent;
    }

    public LiveData<ExtendedParseUser> getUser() {
        return mUser;
    }

    private ProfileRepository mRepository = ProfileRepository.getInstance();


    @SuppressWarnings("unchecked")
    public ProfileViewModel() {
        mStudent = Transformations.switchMap(mRepository.getStudent(), input -> {
            if (input instanceof DataState.Success) {
                return new MutableLiveData<>(new SimpleState<>(((DataState.Success<Student>) input).getData()));
            } else if (input instanceof DataState.Error) {
                return new MutableLiveData<>(new SimpleState<>(((DataState.Error) input).getError().getMessage()));
            }
            return new MutableLiveData<>(new SimpleState<>((Boolean) true));
        });

        mStudentInstitution = Transformations.switchMap(mRepository.getCurrentStudentInstitution(), input -> {
            if (input instanceof DataState.Success) {
                Institution institution = ((DataState.Success<Institution>) input).getData();
                return new MutableLiveData<>(new SimpleState<>(new LocalInstitution(institution)));
            } else if (input instanceof DataState.Error) {
                return new MutableLiveData<>(new SimpleState<>(((DataState.Error) input).getError().getMessage()));
            }
            return new MutableLiveData<>(new SimpleState<>((Boolean) true));
        });

        mUser = Transformations.map(mRepository.getCurrentUser(), ExtendedParseUser::new);
    }

    public void logout() {
        ParseUser.logOut();
        Log.d(TAG, "logout: logged out user" + "current user = " + ParseUser.getCurrentUser());
    }

    private MutableLiveData<Boolean> mShouldLogOut = new MutableLiveData<>();

    public LiveData<Boolean> getShouldLogOut() {
        return mShouldLogOut;
    }

    public void notifyEventOptionSelected(ProfileOption option) {
        switch (option) {
            case LOG_OUT:
                // notify the main activity to end
                mShouldLogOut.setValue(true);
                logout();

                break;
            case ABOUT:
                //
                break;
            default:
                //
        }
    }
}