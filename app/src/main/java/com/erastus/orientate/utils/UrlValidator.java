package com.erastus.orientate.utils;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class UrlValidator {
    private static final String TAG = "UrlValidator";

    public static boolean isUrlValid(String stringUrl) {
        try {
            new URL(stringUrl).toURI();
            return true;
        } catch (URISyntaxException | MalformedURLException e) {
            Log.d(TAG, "isUrlValid: ", e);
            return false;
        }
    }
}
