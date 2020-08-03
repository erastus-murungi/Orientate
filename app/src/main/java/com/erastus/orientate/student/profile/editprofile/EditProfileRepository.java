package com.erastus.orientate.student.profile.editprofile;

public class EditProfileRepository {

    private static volatile EditProfileRepository sInstance;


    public static synchronized EditProfileRepository getInstance() {
        if (sInstance == null) {
            sInstance = new EditProfileRepository();
        }
        return sInstance;
    }
}
