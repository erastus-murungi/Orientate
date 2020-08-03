package com.erastus.orientate.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;

@ParseClassName("UserInfo")
public class UserInfo extends ParseObject {
    public static final String KEY_COUNTRY = "country";
    public static final String KEY_MAJOR = "major";
    public static final String KEY_INTERESTS = "interests";
    public static final String KEY_HOBBIES = "hobbies";
    public static final String KEY_MOVIES = "movies";
    public static final String KEY_TV_SHOWS = "tv_shows";

    public String getCountry() {
        return getString(KEY_COUNTRY);
    }

    public String getMajor() {
        return getString(KEY_MAJOR);
    }

    public JSONArray getInterests() {
        return getJSONArray(KEY_INTERESTS);
    }

    public JSONArray getHobbies() {
        return getJSONArray(KEY_HOBBIES);
    }

    public JSONArray getMovies() {
        return getJSONArray(KEY_MOVIES);
    }

    public JSONArray getTvShows() {
        return getJSONArray(KEY_TV_SHOWS);
    }

    public void setCountry(String country) {
        put(KEY_COUNTRY, country);
    }
}
