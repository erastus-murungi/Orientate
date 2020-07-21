package com.erastus.orientate.student.event.eventdetail;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.erastus.orientate.R;
import com.erastus.orientate.student.event.EventFragment;
import com.erastus.orientate.student.event.models.LocalEvent;
import com.erastus.orientate.student.navigation.ActionBarStatus;
import com.erastus.orientate.student.navigation.StudentNavViewModel;
import com.erastus.orientate.utils.DateUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.parceler.Parcels;

import java.time.LocalDateTime;
import java.util.Objects;

public class EventDetailFragment extends BottomSheetDialogFragment {
    private static final String TAG = "EventDetailFragment";
    public static final float DEFAULT_ZOOM = 10F;
    private EventDetailsViewModel mViewModel;
    private TextView mEventTitleTextView;
    private View mRootView;
    private TextView mDateTextView;
    private TextView mDurationTextView;
    private MapView mEventMapView;
    private GoogleMap mMap;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this,
                new EventDetailsViewModelFactory((LocalEvent) Parcels.unwrap(requireArguments()
                        .getParcelable(EventFragment.KEY))))
                .get(EventDetailsViewModel.class);
        StudentNavViewModel viewModel = new ViewModelProvider(this).get(StudentNavViewModel.class);
        viewModel.getActionBarStatus().postValue(new ActionBarStatus(null, false));

        mRootView = inflater.inflate(R.layout.fragment_event_detail, container, false);
        mEventTitleTextView = mRootView.findViewById(R.id.text_view_event_title);
        mDateTextView = mRootView.findViewById(R.id.text_view_date);
        mDurationTextView = mRootView.findViewById(R.id.text_view_duration);
        mEventMapView = mRootView.findViewById(R.id.map_view_event);
        mEventMapView.onCreate(savedInstanceState);

        if (Objects.requireNonNull(mViewModel.getLocalEvent().getValue()).getEventLocation() != null) {
            setUpMaps();
        }
        setUpObservers();
        return mRootView;
    }

    private void setUpMaps() {
        mEventMapView.getMapAsync(googleMap -> {

            mMap = googleMap;
//            mMap.getUiSettings().setMyLocationButtonEnabled(true);
//            if (ActivityCompat.checkSelfPermission(requireContext(),
//                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
//                            != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            mMap.setMyLocationEnabled(true);

            // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
            MapsInitializer.initialize(requireActivity());
        });
    }

    private void zoomToLoc(Address address) {
        // Updates the location and zoom of the MapView
        double latitude = address.getLatitude();
        double longitude = address.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        Log.d(TAG, "zoomToLoc: moving the camera to lat:" + latitude + "lng:" + longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.addMarker(new MarkerOptions().position(latLng).title(address.toString()));
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel.getLocalEvent().observe(this, event -> {
            mEventTitleTextView.setText(event.getTitle());
            LocalDateTime time = event.getStartingOn();
            mDateTextView.setText(getString(R.string.format_day_month_date,
                    time.getDayOfWeek().toString(), time.getMonth().toString(), time.getDayOfMonth()));
            if (event.getEndingOn() != null) {
                mDurationTextView.setText(getString(R.string.format_time_range,
                        DateUtils.getTimeAmPm(time), DateUtils.getTimeAmPm(event.getEndingOn())));
            } else {
                mDurationTextView.setText(DateUtils.getTimeAmPm(time));
            }
        });
    }


    @Override
    public void onResume() {
        mEventMapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mEventMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mEventMapView.onLowMemory();
    }


    private void checkPermissions() {
        String[] permissions  = {Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
//        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCE))

    }

    private void setUpObservers() {
        mViewModel.getAddress().observe(this, address -> {
            if (address != null) {
                zoomToLoc(address);
            }
        });
    }
}
