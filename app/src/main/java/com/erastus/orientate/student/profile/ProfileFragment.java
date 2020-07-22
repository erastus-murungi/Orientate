package com.erastus.orientate.student.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.erastus.orientate.R;
import com.erastus.orientate.databinding.FragmentProfileBinding;
import com.erastus.orientate.models.GenericUser;
import com.erastus.orientate.student.models.Student;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private ProfileViewModel mViewModel;
    private ProfileBottomFragment mBottomSheet;
    private TextView mUsernameTextView;
    private TextView mFullNameTextView;
    private FragmentProfileBinding mBinding;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        setUpObservers();
        setUpToolBar();
        setUpNamesObservers();
        return mBinding.getRoot();
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
    }

    private void setUpObservers() {
        mViewModel.getShouldLogOut().observe(getViewLifecycleOwner(), shouldLogOut -> {
            if (shouldLogOut == null) {
                return;
            }
            if (shouldLogOut) {
                Log.d(TAG, "setUpObservers: logout");

                mBottomSheet.dismiss();

                requireActivity().finish();
            }
        });
    }

    private void setUpBottomSheet() {
        mBottomSheet = new ProfileBottomFragment();
        mBottomSheet.show(getParentFragmentManager(),
                mBottomSheet.getTag());
    }

    private void setUpNamesObservers() {
        mUsernameTextView = mBinding.textViewUsername;
        mFullNameTextView = mBinding.textViewProfileFullName;
        mViewModel.getStudent().observe(getViewLifecycleOwner(), studentSimpleState -> {
            Log.d(TAG, "setUpNamesObservers: called");
            if (studentSimpleState == null) {
                Log.d(TAG, "setUpNamesObservers: called with null");
                return;
            }
            if (studentSimpleState.getData() != null) {
                Log.d(TAG, "setUpNamesObservers: called with data");
                Student s = studentSimpleState.getData();
                if (s.getMiddleName() == null) {
                    mFullNameTextView.setText(getString(R.string.format_two_names, s.getFirstName(), s.getLastName()));
                } else {
                    mFullNameTextView.setText(getString(R.string.format_three_names, s.getFirstName(), s.getMiddleName(), s.getLastName()));
                }
            }
            if (studentSimpleState.getErrorMessage() != null) {
                Log.e(TAG, "setUpNamesObservers: " + studentSimpleState.getErrorMessage());
            }
        });

        mViewModel.getUser().observe(getViewLifecycleOwner(), genericUser -> {
            if (genericUser != null) {
                mUsernameTextView.setText(getString(R.string.format_username,
                        genericUser.getUsername()));
            }
        });
    }
}