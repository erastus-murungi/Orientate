package com.erastus.orientate.student.navigation;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.erastus.orientate.applications.App;
import com.erastus.orientate.institution.models.Institution;
import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.models.Orientation;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.student.models.Student;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class StudentNavRepository {

    private static volatile StudentNavRepository sInstance;

    private static MutableLiveData<DataState> sInstitution = new MutableLiveData<>();

    private static MutableLiveData<DataState> sOrientation = new MutableLiveData<>();

    public LiveData<DataState> getStudentOrientation() {
        return sOrientation;
    }

    public static synchronized StudentNavRepository getInstance() {
        if (sInstance == null) {
            sInstance = new StudentNavRepository();
        }
        return sInstance;
    }

    private static void getOrientationObject(@NonNull Institution institution, String loggedInUserId) {
        ParseQuery<Orientation> orientationParseQuery = ParseQuery.getQuery(Orientation.class);
        orientationParseQuery.whereEqualTo(Orientation.KEY_OWNED_BY, institution);
        orientationParseQuery.whereEqualTo(Orientation.KEY_TO, loggedInUserId);
        orientationParseQuery.include(Orientation.KEY_OWNED_BY);
        orientationParseQuery.getFirstInBackground((orientation, e) -> {
            if (e == null) {
                sOrientation.postValue(new DataState.Success<>(orientation));
            } else {
                sOrientation.postValue(new DataState.Error(e));
            }
        });

    }

    public LiveData<DataState> getStudentInstitution() {
        ExtendedParseUser loggedInUser = App.get().getCurrentUser();

        loggedInUser.getStudentParseObject().fetchIfNeededInBackground((loggedInStudent, e) -> {
            if (e == null) {
                loggedInStudent.
                        getParseObject(Student.KEY_ENROLLED_AT).
                        fetchIfNeededInBackground((GetCallback<Institution>)
                                (institution, e1) -> {
                                if (e1 == null) {
                                    sInstitution.postValue(new DataState.Success<>(institution));
                                    getOrientationObject(institution, loggedInUser.getObjectId());
                                } else {
                                    sInstitution.postValue(new DataState.Error(e1));
                                }
                            }
                );
            } else {
                sInstitution.postValue(new DataState.Error(e));
            }
        });
        return sInstitution;
    }

    public void saveNewRating(float newRating) {
        DataState state = sOrientation.getValue();
        if (state instanceof DataState.Success) {
            Orientation orientation = ((DataState.Success<Orientation>) state).getData();
            float oldRating = (float) orientation.getVoteAverage();
            int numVotes = orientation.getNumVotes();

            orientation.put(Orientation.KEY_NUM_VOTES,  + 1);
            orientation.put(Orientation.KEY_VOTE_AVERAGE, ((oldRating * numVotes) + newRating ) / orientation.getNumVotes());
            orientation.saveInBackground(e -> {
                if (e == null) {
                    sOrientation.postValue(new DataState.Success<>(orientation));
                } else {
                    sOrientation.postValue(new DataState.Error(e));
                }
            });
        }

    }
}
