package com.erastus.orientate.student.event;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erastus.orientate.R;
import com.erastus.orientate.student.event.dailyevent.DailyEventFragment;
import com.erastus.orientate.utils.horizontalcalendar.HorizontalCalendar;
import com.erastus.orientate.utils.horizontalcalendar.HorizontalCalendarView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Calendar;


public class EventFragment extends Fragment {
    private EventViewModel mViewModel;
    private HorizontalCalendarView mHorizontalCalendarView;

    public static EventFragment newInstance() {
        return new EventFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        mHorizontalCalendarView = view.findViewById(R.id.horizontal_calendar_view_events);

        /* starts before 1 month from now */
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        HorizontalCalendar.Builder builder = new HorizontalCalendar.Builder(view, R.id.horizontal_calendar_view_events)
                .range(startDate, endDate)
                .datesNumberOnScreen(7);

//        builder.configure().showTopText(false);
        builder.build();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        // TODO: Use the ViewModel
    }


}