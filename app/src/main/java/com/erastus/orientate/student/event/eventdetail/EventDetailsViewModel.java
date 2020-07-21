package com.erastus.orientate.student.event.eventdetail;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.erastus.orientate.student.event.EventViewModel;
import com.erastus.orientate.student.event.models.LocalEvent;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import kotlin.coroutines.CoroutineContext;

public class EventDetailsViewModel extends ViewModel {
    private static final String TAG = "EventDetailsViewModel";
    public static final int ERROR_DIALOG_REQUEST = 9001;
    private MutableLiveData<LocalEvent> mLocalEvent;
    private MutableLiveData<Address> mAddress = new MutableLiveData<>();

    public LiveData<Address> getAddress() {
        return mAddress;
    }

    public EventDetailsViewModel(LocalEvent event) {
        mLocalEvent = new MutableLiveData<>(event);
    }

    public MutableLiveData<LocalEvent> getLocalEvent() {
        return mLocalEvent;
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

    private Address getLocation(Geocoder geocoder, LatLng latLng) {
        try {
            List<Address> matches = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            return (matches.isEmpty() ? null : matches.get(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
