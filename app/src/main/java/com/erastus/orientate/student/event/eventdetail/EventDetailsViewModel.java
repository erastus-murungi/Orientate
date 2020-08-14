package com.erastus.orientate.student.event.eventdetail;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.erastus.orientate.student.event.models.Event;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.student.models.SimpleState;
import com.erastus.orientate.utils.TaskRunner;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

public class EventDetailsViewModel extends ViewModel {
    private static final String TAG = "EventDetailsViewModel";
    private MutableLiveData<Event> mEvent;

    private MutableLiveData<DataState> mUrlValidityState = new MutableLiveData<>();

    private MutableLiveData<DataState> mGetLocationAddress = new MutableLiveData<>();

    public LiveData<DataState> getLocationAddress() {
        return mGetLocationAddress;
    }

    public MutableLiveData<SimpleState<Integer>> mUpvoteCount = new MutableLiveData<>();

    private Integer upVoteCount;

    public boolean isUpVoted() {
        return mUpVoted;
    }

    private boolean mUpVoted = false;


    public LiveData<SimpleState<Integer>> getUpVoteCount() {
        return mUpvoteCount;
    }

    public EventDetailsViewModel(Event event) {
        mEvent = new MutableLiveData<>(event);
        upVoteCount = event.getUpVoteCount();
    }

    public LiveData<Event> getEvent() {
        return mEvent;
    }

    private void saveUpVotes(Integer newUpVoteCount) {
        mEvent.getValue().setUpVoteCount(newUpVoteCount);
        Objects.requireNonNull(mEvent.getValue(), "Null event").saveInBackground(
                e -> mUpvoteCount.postValue(
                        e == null ?
                                new SimpleState<>(mEvent.getValue().getUpVoteCount()):
                                new SimpleState<>(e.getMessage())
                )
        );
    }
    public void upVoteEvent() {
        if (mUpVoted) {
            saveUpVotes(--upVoteCount);
            mUpVoted = false;
        } else {
            saveUpVotes(++upVoteCount);
            mUpVoted = true;
        }
    }
    static class ReverseGeoCodeAsync implements Callable<DataState> {
        private final LatLng latLng;
        private final Geocoder geocoder;

        ReverseGeoCodeAsync(Geocoder geocoder, LatLng latLng) {
            this.latLng = latLng;
            this.geocoder = geocoder;
        }

        @Override
        public DataState call() {
            try {
                List<Address> matches = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                Log.d(TAG, "call: " + matches);
                return new DataState.Success<>(matches.isEmpty() ? null : matches.get(0));
            } catch (IOException e) {
                Log.e(TAG, "call: IOException", e);
                return new DataState.Error(e);
            }
        }
    }

    /**
     * Here we use {@link MutableLiveData#postValue(Object)} because TaskRunner operates in a different thread
     */
    public void getLocation(Geocoder geocoder, LatLng latLng) {
        TaskRunner.getInstance().executeAsync(new ReverseGeoCodeAsync(geocoder, latLng),
                (data) -> mGetLocationAddress.postValue(data));
    }


    public LiveData<DataState> getUrlValidityState() {
        return mUrlValidityState;
    }

    public void checkUrlValidity(String stringUrl) {
        try {
            mUrlValidityState.setValue(new DataState.Success<>(new URL(stringUrl).toURI()));
        } catch (URISyntaxException | MalformedURLException e) {
            Log.d(TAG, "checkUrlValidity: invalid url" + stringUrl, e);
            mUrlValidityState.setValue(new DataState.Error(e));
        }
    }

}
