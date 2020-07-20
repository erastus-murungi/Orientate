package com.erastus.orientate.student.event;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.erastus.orientate.R;
import com.erastus.orientate.utils.horizontalcalendar.HorizontalCalendar;
import com.erastus.orientate.utils.horizontalcalendar.HorizontalCalendarView;
import com.erastus.orientate.utils.horizontalcalendar.utils.HorizontalCalendarListener;

import java.util.Calendar;


public class EventFragment extends Fragment {
    private EventViewModel mViewModel;
    private HorizontalCalendarView mHorizontalCalendarView;
    private HorizontalCalendar mHorizontalCalendar;
    private TextView mNoEventsTextView;
    private RecyclerView mEventsRecyclerView;

    public static EventFragment newInstance() {
        return new EventFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        mHorizontalCalendarView = view.findViewById(R.id.horizontal_calendar_view_events);
        mNoEventsTextView = view.findViewById(R.id.text_view_no_events);
        mEventsRecyclerView = view.findViewById(R.id.recycler_view_events);

        /* starts before 1 month from now */
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        HorizontalCalendar.Builder builder = new HorizontalCalendar.Builder(view, R.id.horizontal_calendar_view_events)
                .range(startDate, endDate)
                .datesNumberOnScreen(7);
        builder.configure().textColor(requireContext().getColor(R.color.lightBlue),
                requireContext().getColor(R.color.white)).end();
        mHorizontalCalendar = builder.build();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        mHorizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                // if there are events on this date, then show them in a recycler view
                // otherwise show no events
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

        mViewModel.getEventsExist().observe(getViewLifecycleOwner(), eventsExist -> {
            if (eventsExist == null) {
                return;
            }
            if (eventsExist) {
                mNoEventsTextView.setVisibility(View.GONE);
            } else {
                mNoEventsTextView.setVisibility(View.VISIBLE);
            }
        });
    }


}