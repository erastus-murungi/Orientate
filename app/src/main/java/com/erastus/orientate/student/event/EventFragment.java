package com.erastus.orientate.student.event;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erastus.orientate.R;
import com.erastus.orientate.student.event.adapters.EventContentAdapter;
import com.erastus.orientate.student.event.adapters.EventTimeAdapter;
import com.erastus.orientate.student.event.eventdetail.EventDetailFragment;
import com.erastus.orientate.student.event.models.Event;
import com.erastus.orientate.utils.horizontalcalendar.HorizontalCalendar;
import com.erastus.orientate.utils.horizontalcalendar.HorizontalCalendarView;
import com.erastus.orientate.utils.horizontalcalendar.utils.HorizontalCalendarListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.parceler.Parcels;

import java.util.Calendar;


public class EventFragment extends Fragment {
    public static final String KEY = Event.class.getSimpleName();
    private EventViewModel mViewModel;
    private HorizontalCalendar mHorizontalCalendar;
    private TextView mNoEventsTextView;
    private RecyclerView mEventsRecyclerView;
    private EventTimeAdapter mEventTimeAdapter;
    private ProgressBar mEventsLoadingProgressBar;
    // for displaying the snack-bar
    private View mRootView;

    public static EventFragment newInstance() {
        return new EventFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        mRootView = inflater.inflate(R.layout.fragment_event, container, false);
        mNoEventsTextView = mRootView.findViewById(R.id.text_view_no_events);
        mEventsLoadingProgressBar = mRootView.findViewById(R.id.progress_bar_events);
        mEventsRecyclerView = mRootView.findViewById(R.id.recycler_view_events);

        initRecyclerView();

        /* starts before 1 month from now */
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        HorizontalCalendar.Builder builder = new HorizontalCalendar.Builder(mRootView, R.id.horizontal_calendar_view_events)
                .range(startDate, endDate)
                .datesNumberOnScreen(7);
        builder.configure().textColor(requireContext().getColor(R.color.lightBlue),
                requireContext().getColor(R.color.white)).end();
        mHorizontalCalendar = builder.build();

        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mHorizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                // if there are events on this date, then show them in a recycler view
                // otherwise show no events
                mViewModel.requestEventsSpecificDate(date);
            }

            @Override
            public void onCalendarScroll(HorizontalCalendarView calendarView,
                                         int dx, int dy) {
                //
            }

            @Override
            public boolean onDateLongClicked(Calendar date, int position) {
                //
                return true;
            }
        });

        mViewModel.getEventsResult().observe(getViewLifecycleOwner(), eventResult -> {
            if (eventResult == null) {
                return;
            }
            if (eventResult.isLoading()) {
                mEventsLoadingProgressBar.setVisibility(View.VISIBLE);
                mNoEventsTextView.setVisibility(View.GONE);
                mEventsRecyclerView.setVisibility(View.GONE);
            } else if (eventResult.getEventsExist()) {
                mEventsLoadingProgressBar.setVisibility(View.GONE);
                mEventsRecyclerView.setVisibility(View.VISIBLE);
                mNoEventsTextView.setVisibility(View.GONE);
            } else if (!eventResult.getEventsExist() && eventResult.getErrorMessage() == null) {
                mNoEventsTextView.setVisibility(View.VISIBLE);
                mEventsLoadingProgressBar.setVisibility(View.GONE);
                mEventsRecyclerView.setVisibility(View.GONE);
            } else if (eventResult.getErrorMessage() != null) {
                mNoEventsTextView.setVisibility(View.GONE);
                mEventsLoadingProgressBar.setVisibility(View.GONE);
            mEventsRecyclerView.setVisibility(View.GONE);
            shoWReloadSnackBar(eventResult.getErrorMessage());
        }
        });

        mViewModel.getEvents().observe(getViewLifecycleOwner(), localEvents -> {
            mEventTimeAdapter.setEvents(localEvents);
            mEventTimeAdapter.notifyDataSetChanged();
        });

    }

    private void shoWReloadSnackBar(String errorMessage) {
        Snackbar.make(mRootView, errorMessage, BaseTransientBottomBar.LENGTH_LONG)
                .setBackgroundTint(requireContext().getColor(R.color.darkBlue))
                .setTextColor(requireContext().getColor(android.R.color.darker_gray))
                .setActionTextColor(requireContext().getColor(R.color.white))
                .setAction(R.string.reload, view -> mViewModel.reload(mHorizontalCalendar.getSelectedDate())).show();
    }

    private void initRecyclerView() {
        EventContentAdapter.OnEventClickedListener listener = event -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable(KEY, Parcels.wrap(event));
            EventDetailFragment fragmentEventDetail = new EventDetailFragment();
            fragmentEventDetail.setArguments(bundle);
            getParentFragmentManager().beginTransaction().replace(R.id.frame_layout_student_content,
                    fragmentEventDetail).addToBackStack(null).commit();
        };

        mEventTimeAdapter = new EventTimeAdapter(getContext(),
                mViewModel, listener);
        mEventsRecyclerView.setAdapter(mEventTimeAdapter);
        mEventsRecyclerView.setLayoutManager(new
                LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false));
    }
}