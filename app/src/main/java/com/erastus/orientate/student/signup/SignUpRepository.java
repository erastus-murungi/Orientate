package com.erastus.orientate.student.signup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.student.models.Student;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.Date;

public class SignUpRepository {

    public static volatile SignUpRepository sInstance;

    private MutableLiveData<DataState> mSignUpResult = new MutableLiveData<>();

    public static synchronized SignUpRepository getInstance() {
        if (sInstance == null) {
            sInstance = new SignUpRepository();
        }
        return sInstance;
    }

    public LiveData<DataState> getSignUpResult() {
        return mSignUpResult;
    }


    public void signUpStudent(String username, String password, String email,
                              ParseFile profilePicture,
                              String firstName, String middleName, String lastName, Date dob) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put(ExtendedParseUser.KEY_IS_STUDENT, true);

        // proceed to save the student object
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);

        // optional fields
        if (middleName != null) {
            student.setMiddleName(middleName);
        }
        if (dob != null) {
            student.setDob(dob);
        }
        if (profilePicture != null) {
            student.setProfilePicture(profilePicture);
        }
        // end of optional fields

        user.signUpInBackground(e -> {
            if (e == null) {
                student.put(Student.KEY_USER, user);
                student.saveInBackground(e1 -> {
                    if (e1 == null) {
                        user.put(ExtendedParseUser.KEY_STUDENT, student);
                        user.saveInBackground(e2 -> {
                            if (e2 == null) {
                                // Hurray!
                                mSignUpResult.postValue(new DataState.Success<>(user));
                            } else {
                                // Sign up didn't succeed. Look at the ParseException
                                // to figure out what went wrong
                                mSignUpResult.postValue(new DataState.Error(e2));
                            }
                        });
                    } else {
                        mSignUpResult.postValue(new DataState.Error(e1));
                    }
                });
            }
        });
    }
}
