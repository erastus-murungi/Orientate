package com.erastus.orientate.student.event.eventdetail;

import android.Manifest;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.erastus.orientate.R;
import com.erastus.orientate.student.event.EventFragment;
import com.erastus.orientate.student.event.models.LocalEvent;
import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.student.navigation.ActionBarStatus;
import com.erastus.orientate.student.navigation.StudentNavViewModel;
import com.erastus.orientate.utils.DateUtils;
import com.erastus.orientate.utils.FormatNumbers;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.util.Objects;

public class EventDetailFragment extends Fragment {
    private static final String TAG = "EventDetailFragment";
    public static final float DEFAULT_ZOOM = 20.0f;
    private EventDetailsViewModel mViewModel;
    private TextView mEventTitleTextView;
    private View mRootView;
    private TextView mDateTextView;
    private TextView mDurationTextView;
    private MapView mEventMapView;
    private GoogleMap mMap;
    private TextView mNoLocationTextView;
    private TextView mBodyTextView;
    private TextView mUpVoteTextView;
    private TextView mNoBodyTextView;
    private TextView mGoBackTextView;


    private void hideMainNavBar() {
        StudentNavViewModel viewModel = new ViewModelProvider(this).get(StudentNavViewModel.class);
        viewModel.getActionBarStatus().postValue(new ActionBarStatus(null, false));
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        mViewModel = new ViewModelProvider(this,
                new EventDetailsViewModelFactory((LocalEvent) Parcels.unwrap(requireArguments()
                        .getParcelable(EventFragment.KEY))))
                .get(EventDetailsViewModel.class);

        mRootView = inflater.inflate(R.layout.fragment_event_detail, container, false);

        mEventTitleTextView = mRootView.findViewById(R.id.text_view_event_title);
        mDateTextView = mRootView.findViewById(R.id.text_view_date);
        mDurationTextView = mRootView.findViewById(R.id.text_view_duration);
        mEventMapView = mRootView.findViewById(R.id.map_view_event);
        mNoLocationTextView = mRootView.findViewById(R.id.text_view_no_location_provided);
        mBodyTextView = mRootView.findViewById(R.id.text_view_event_body);
        mUpVoteTextView = mRootView.findViewById(R.id.text_view_votes);
        mNoBodyTextView = mRootView.findViewById(R.id.text_view_no_event_body);
        mGoBackTextView = mRootView.findViewById(R.id.text_view_go_back_to_main_events);

        setUpBodyTextView();
        setUpVotesTextView();
        setUpGoingBackTextView();


        if (Objects.requireNonNull(mViewModel.getLocalEvent().getValue()).getEventLocation() != null) {
            setUpMaps(savedInstanceState);
            mNoLocationTextView.setVisibility(View.GONE);
            mEventMapView.setVisibility(View.VISIBLE);
        } else {
            mEventMapView.setVisibility(View.GONE);
            mNoLocationTextView.setVisibility(View.VISIBLE);
        }
        hideMainNavBar();

        return mRootView;
    }

    private void setUpGoingBackTextView() {
        mGoBackTextView.setText(R.string.orientation_events);
    }

    private void setUpVotesTextView() {
        Integer voteCount = mViewModel.getLocalEvent().getValue().getUpVoteCount();
        if (voteCount == null || voteCount == 0) {
            mUpVoteTextView.setText(null);
        } else {
            mUpVoteTextView.setText(FormatNumbers.format(voteCount));
        }
    }

    private void setUpBodyTextView() {
        String textBody = mViewModel.getLocalEvent().getValue().getBody();
        if (textBody == null) {
            mNoBodyTextView.setVisibility(View.VISIBLE);
            mBodyTextView.setVisibility(View.GONE);
        } else {
            mBodyTextView.setVisibility(View.VISIBLE);
            mNoBodyTextView.setVisibility(View.GONE);
        }
    }


    private void setUpMaps(Bundle savedInstanceState) {
        mEventMapView.onCreate(savedInstanceState);
        mEventMapView.getMapAsync(googleMap -> {
            mMap = googleMap;
            MapsInitializer.initialize(requireActivity());
            mViewModel.getLocation(new Geocoder(requireContext()),
                    Objects.requireNonNull(mViewModel.getLocalEvent().getValue()).getEventLocation());
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
        mMap.addMarker(new MarkerOptions().position(latLng).title(address.getFeatureName()));
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel.getLocalEvent().observe(getViewLifecycleOwner(), event -> {
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
        setUpObservers();
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
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
//        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCE))

    }

    @SuppressWarnings("unchecked")
    private void setUpObservers() {
        mViewModel.getLocationAddress().observe(getViewLifecycleOwner(), dataState -> {
            if (dataState instanceof DataState.Success) {
                Address address = ((DataState.Success<Address>) dataState).getData();
                if (address == null) {
                    notifyUserOfErrorUsingSnackBar("No Addresses found");
                    Log.d(TAG, "setUpObservers: No addresses found");
                } else {
                    zoomToLoc(address);
                }
            } else if (dataState instanceof DataState.Error){
                notifyUserOfErrorUsingSnackBar(((DataState.Error) dataState).getError().getMessage());
                Log.e(TAG, "setUpObservers: Exception", ((DataState.Error) dataState).getError());
            }
        });
    }

    private void notifyUserOfErrorUsingSnackBar(String errorMessage) {
    }


}
