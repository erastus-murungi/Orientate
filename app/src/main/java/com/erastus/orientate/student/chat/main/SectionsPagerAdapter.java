package com.erastus.orientate.student.chat.main;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.erastus.orientate.student.chat.connections.ConnectionsFragment;
import com.erastus.orientate.student.chat.conversations.ConversationFragment;

import org.jetbrains.annotations.NotNull;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStateAdapter {

    public static final int NUM_TABS = 2;

    public SectionsPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NotNull
    @Override
    public Fragment createFragment(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if (position == 0) {
            return ConversationFragment.newInstance();
        } else {
            return ConnectionsFragment.newInstance();
        }
    }

    @Override
    public int getItemCount() {
        // Show 2 total pages.
        return NUM_TABS;
    }
}