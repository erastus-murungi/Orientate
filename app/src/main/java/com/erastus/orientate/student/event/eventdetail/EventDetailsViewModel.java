package com.erastus.orientate.student.event.eventdetail;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.erastus.orientate.student.event.models.LocalEvent;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.utils.TaskRunner;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseUser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;

public class EventDetailsViewModel extends ViewModel {
    private static final String TAG = "EventDetailsViewModel";
    public static final int ERROR_DIALOG_REQUEST = 9001;
    private MutableLiveData<LocalEvent> mLocalEvent;

    private MutableLiveData<DataState> mUrlValidityState = new MutableLiveData<>();

    private MutableLiveData<DataState> mGetLocationAddress = new MutableLiveData<>();

    public LiveData<DataState> getLocationAddress() {
        return mGetLocationAddress;
    }

    public EventDetailsViewModel(LocalEvent event) {
        mLocalEvent = new MutableLiveData<>(event);
    }

    public MutableLiveData<LocalEvent> getLocalEvent() {
        return mLocalEvent;
    }

    public void upVoteEvent() {
    }

//    public boolean isServicesOk(Context context) {
//        Log.d(TAG, "isServicesOk: checking the google services version");
//        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
//        if (available == ConnectionResult.SUCCESS) {
//            Log.d(TAG, "isServicesOk: Google Play Services is OK");
//            return true;
//        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
//            Dialog googleMapsDialog = GoogleApiAvailability.getInstance().getErrorDialog((Activity) context, available, ERROR_DIALOG_REQUEST);
//            googleMapsDialog.show();;
//        }
//        else {
//
//        }

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
