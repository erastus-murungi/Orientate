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
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class EventFragment extends Fragment {
    private EventViewModel mViewModel;
    private ViewPager2 mViewPager2;
    private TabLayout mTabLayout;
    private EventViewPagerAdapter mAdapter;

    public static EventFragment newInstance() {
        return new EventFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_fragment, container, false);
        mViewPager2 = view.findViewById(R.id.view_pager_events);
        mTabLayout = view.findViewById(R.id.tab_layout_events);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        // TODO: Use the ViewModel
        mAdapter = new EventViewPagerAdapter(getParentFragmentManager(), getLifecycle());
        mViewPager2.setAdapter(mAdapter);

        TabLayoutMediator mediator = new TabLayoutMediator(mTabLayout, mViewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
            }
        });
        mediator.attach();
    }


    private class EventViewPagerAdapter extends FragmentStateAdapter {

        public EventViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return DailyEventFragment.newInstance(position);
        }

        @Override
        public int getItemCount() {
            return mViewModel.getNumPages();
        }
    }

}