package com.erastus.orientate.student.event.eventdetail;

import android.Manifest;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.erastus.orientate.R;
import com.erastus.orientate.student.event.EventFragment;

import com.erastus.orientate.student.models.DataState;
import com.erastus.orientate.student.models.SimpleState;
import com.erastus.orientate.student.navigation.ActionBarStatus;
import com.erastus.orientate.student.navigation.StudentNavViewModel;
import com.erastus.orientate.utils.DateUtils;
import com.erastus.orientate.utils.FormatNumbers;
import com.erastus.orientate.utils.richlinkpreview.RichLinkView;
import com.erastus.orientate.utils.richlinkpreview.ViewListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.parceler.Parcels;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

public class EventDetailFragment extends Fragment {
    private static final String TAG = "EventDetailFragment";
    public static final String KEY = "LatLng";
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
    private RichLinkView mRichLinkView;
    private Button mUpVoteButton;


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
                new EventDetailsViewModelFactory(Parcels.unwrap(requireArguments()
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
        mRichLinkView = mRootView.findViewById(R.id.rich_link_preview);
        mUpVoteButton = mRootView.findViewById(R.id.button_upvote);

        initiateUrlChecker();
        setUpMaps(savedInstanceState);
        setUpBodyTextView();
        setUpVotesTextView();
        setUpVoteButton();
        setUpGoingBackTextView();
        setUpMapView();
        initiateUrlChecker();
        hideMainNavBar();

        return mRootView;
    }

    private void setUpMapView() {
        mEventMapView.setOnClickListener(view -> {
            LatLng latLng = Objects.requireNonNull(mViewModel.getEvent().getValue()).getWhere();
            String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latLng.latitude, latLng.longitude);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);
        });
    }

    private void initiateUrlChecker() {
        final String url = mViewModel.getEvent().getValue().getUrl();
        if (url != null) {
            mViewModel.checkUrlValidity(url);
            mViewModel.getUrlValidityState().observe(getViewLifecycleOwner(), uriDataState -> {
                if (uriDataState instanceof DataState.Success) {
                    URI uri = ((DataState.Success<URI>) uriDataState).getData();
                    mRichLinkView.setVisibility(View.VISIBLE);
                    mRichLinkView.setLink(uri.toString(), new ViewListener() {
                        @Override
                        public void onSuccess(boolean status) {
                            Log.d(TAG, "onSuccess: " + status);
                        }

                        @Override
                        public void onError(Exception e) {
                            showErrorSnackBar(e.getMessage());
                        }
                    });
                } else if (uriDataState instanceof DataState.Error) {
                    showErrorSnackBar(((DataState.Error) uriDataState).getError().getMessage());
                    Log.e(TAG, "setUpObservers: Exception", ((DataState.Error) uriDataState).getError());
                }
            });
        } else {
            mRichLinkView.setVisibility(View.GONE);
        }
    }

    private void setUpGoingBackTextView() {
        mGoBackTextView.setText(R.string.orientation_events);
    }

    private void setUpVotesTextView() {
        Integer voteCount = mViewModel.getEvent().getValue().getUpVoteCount();
        if (voteCount == null || voteCount == 0) {
            mUpVoteTextView.setText(R.string.no_votes);
        } else {
            mUpVoteTextView.setText(getString(R.string.going_to, FormatNumbers.format(voteCount)));
        }
    }

    private void setUpVoteButton() {
        mUpVoteButton.setOnClickListener(view -> {
            mViewModel.upVoteEvent();
            mUpVoteButton.setEnabled(false);
        });
    }

    private void setUpBodyTextView() {
        String textBody = mViewModel.getEvent().getValue().getBody();
        if (textBody == null) {
            mNoBodyTextView.setVisibility(View.VISIBLE);
            mBodyTextView.setVisibility(View.GONE);
        } else {
            mBodyTextView.setText(textBody);
            mBodyTextView.setVisibility(View.VISIBLE);
            mNoBodyTextView.setVisibility(View.GONE);
        }
    }


    private void setUpMaps(Bundle savedInstanceState) {
        if (Objects.requireNonNull(mViewModel.getEvent().getValue()).getWhere() != null) {
            mEventMapView.onCreate(savedInstanceState);
            mEventMapView.getMapAsync(googleMap -> {
                mMap = googleMap;
                MapsInitializer.initialize(requireActivity());
                mViewModel.getLocation(new Geocoder(requireContext()),
                        Objects.requireNonNull(mViewModel.getEvent().getValue()).getWhere());
            });
            mNoLocationTextView.setVisibility(View.GONE);
            mEventMapView.setVisibility(View.VISIBLE);
            mEventMapView.setOnClickListener(view -> {
                //TODO direct the user to google maps
            });
        } else {
            mEventMapView.setVisibility(View.GONE);
            mNoLocationTextView.setVisibility(View.VISIBLE);
        }
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
        mViewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
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

    @SuppressWarnings("unchecked")
    private void setUpObservers() {
        mViewModel.getLocationAddress().observe(getViewLifecycleOwner(), dataState -> {
            if (dataState instanceof DataState.Success) {
                Address address = ((DataState.Success<Address>) dataState).getData();
                if (address == null) {
                    showErrorSnackBar("No Addresses found");
                    Log.d(TAG, "setUpObservers: No addresses found");
                } else {
                    zoomToLoc(address);
                }
            } else if (dataState instanceof DataState.Error){
                showErrorSnackBar(((DataState.Error) dataState).getError().getMessage());
                Log.e(TAG, "setUpObservers: Exception", ((DataState.Error) dataState).getError());
            }
        });

        mViewModel.getUpVoteCount().observe(getViewLifecycleOwner(), integerSimpleState -> {
            if (integerSimpleState == null) {
                return;
            }
            if (integerSimpleState.getErrorMessage() != null) {
                showErrorSnackBar(integerSimpleState.getErrorMessage());
            } else if (integerSimpleState.getData() != null) {
                mUpVoteTextView.setText(requireContext().getString(R.string.going_to,
                        FormatNumbers.format(integerSimpleState.getData())));
                mUpVoteButton.setEnabled(true);
                if (mViewModel.isUpVoted()) {
                    mUpVoteButton.setCompoundDrawablesRelativeWithIntrinsicBounds(requireContext().getDrawable(R.drawable.ic_baseline_thump_up_blue), null, null, null);
                } else {
                    mUpVoteButton.setCompoundDrawablesRelativeWithIntrinsicBounds(requireContext().getDrawable(R.drawable.ic_baseline_thumb_up_24), null, null, null);
                }
            }
        });
    }

    private void showErrorSnackBar(String errorMessage) {
    }

}
