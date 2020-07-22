package com.erastus.orientate.student.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.erastus.orientate.R;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private ProfileViewModel mViewModel;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setUpToolBar();
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    private void setUpToolBar() {
        Toolbar fragmentToolbar = requireActivity().findViewById(R.id.toolbar_student_nav);
        fragmentToolbar.inflateMenu(R.menu.menu_student_profile);
        fragmentToolbar.setTitle(requireActivity().getString(R.string.profile));
        fragmentToolbar.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.action_pull_up_menu) {
                setUpBottomSheet();
            }
            return true;
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        setUpObservers();
    }

    private void setUpObservers() {
        mViewModel.getShouldLogOut().observe(getViewLifecycleOwner(), shouldLogOut -> {
            if (shouldLogOut == null) {
                return;
            }
            if (shouldLogOut){
                Log.d(TAG, "setUpObservers: logout");
                requireActivity().finish();
            }
        });
    }

    private void setUpBottomSheet() {
        ProfileBottomFragment fragment = new ProfileBottomFragment();
        fragment.show(getParentFragmentManager(), fragment.getTag());
    }

}