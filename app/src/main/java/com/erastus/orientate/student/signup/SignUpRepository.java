package com.erastus.orientate.student.signup;

import androidx.lifecycle.MutableLiveData;

import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.student.models.Student;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.Date;

public class SignUpRepository {

    public static volatile SignUpRepository sInstance;

    private static MutableLiveData<DataState> mSignUpResult = new MutableLiveData<>();

    public static synchronized SignUpRepository getInstance() {
        if (sInstance == null) {
            sInstance = new SignUpRepository();
        }
        return sInstance;
    }


    public void signUpStudent(String username, String password, String email,
                              ParseFile profilePicture,
                              String firstName, String middleName, String lastName, Date dob) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put(ExtendedParseUser.KEY_IS_STUDENT, true);

        // other fields can be set just like with ParseObject

        user.signUpInBackground(e -> {
            if (e == null) {
                // Hooray! Let them use the app now.
                Student student = new Student();
                student.setFirstName(firstName);
                student.setLastName(lastName);
                if (middleName != null) {
                    student.setMiddleName(middleName);
                }
                if (dob != null) {
                    student.setDob(dob);
                }
                if (profilePicture != null) {
                    student.setProfilePicture(profilePicture);
                }
                student.saveInBackground(e1 ->
                        mSignUpResult.postValue(e1 == null ?
                                new DataState.Success<>(user) : new DataState.Error(e1)));
            } else {
                // Sign up didn't succeed. Look at the ParseException
                // to figure out what went wrong
                mSignUpResult.postValue(new DataState.Error(e));
            }
        });
    }
}
