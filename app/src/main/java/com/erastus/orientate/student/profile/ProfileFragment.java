package com.erastus.orientate.student.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.erastus.orientate.R;
import com.erastus.orientate.applications.App;
import com.erastus.orientate.databinding.FragmentProfileBinding;
import com.erastus.orientate.student.chat.ChatPreviewFragment;
import com.erastus.orientate.student.login.StudentLoginRepository;
import com.erastus.orientate.student.models.Student;
import com.erastus.orientate.utils.circularimageview.CircularImageView;
import com.erastus.orientate.utils.customindicators.AVLoadingIndicatorView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private ProfileViewModel mViewModel;
    private ProfileBottomFragment mBottomSheet;
    private TextView mUsernameTextView;
    private TextView mFullNameTextView;
    private FragmentProfileBinding mBinding;
    private AVLoadingIndicatorView mLoadingIndicator;
    private CircularImageView mProfilePictureCircularImageView;
    private Button mMessagesButton;
    private Button mConnectionsButton;
    private Button mInterestsEventsButton;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        setUpImageView();
        setUpButtons();
        setUpObservers();
        setUpToolBar();
        bindData();
        return mBinding.getRoot();
    }

    private void setUpButtons() {
        mMessagesButton = mBinding.textViewGoToMessages;
        mMessagesButton.setOnClickListener(view -> {
            goToChatPreviewFragment();
        });

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
        inflateMiddleFragment();
    }

    private void setUpObservers() {
        mViewModel.getShouldLogOut().observe(getViewLifecycleOwner(), shouldLogOut -> {
            if (shouldLogOut == null) {
                return;
            }
            if (shouldLogOut) {
                Log.d(TAG, "setUpObservers: logout");
                showErrorSnackBar("You have been logged out");

                mBottomSheet.dismiss();

                requireActivity().finish();
            }
        });
    }

    private void setUpImageView() {
        mProfilePictureCircularImageView = mBinding.imageViewStudentProfilePicture;
        mProfilePictureCircularImageView.setBorderColor(R.color.colorPrimaryDark);
        mProfilePictureCircularImageView.setBorderWidth(10);
        mProfilePictureCircularImageView.setShadow(2, 10, 10, R.color.blue_grey_300);
    }

    private void setUpBottomSheet() {
        mBottomSheet = new ProfileBottomFragment();
        mBottomSheet.show(getChildFragmentManager(),
                mBottomSheet.getTag());
    }

    private void bindData() {
        mUsernameTextView = mBinding.textViewUsername;
        mFullNameTextView = mBinding.textViewProfileFullName;
        mLoadingIndicator = mBinding.avLoadingIndicatorProfileFullName;

        mLoadingIndicator.show();

        Student s = StudentLoginRepository.getInstance().getLoggedInStudent();
        mUsernameTextView.setText(getString(R.string.format_username,
                App.get().getCurrentUser().getUsername()));

        if (s.getMiddleName() == null) {
            mFullNameTextView.setText(getString(R.string.format_two_names, s.getFirstName(), s.getLastName()));
        } else {
            mFullNameTextView.setText(getString(R.string.format_three_names, s.getFirstName(), s.getMiddleName(), s.getLastName()));
        }

        Glide.with(requireContext())
                .load(s.getProfileImageUrl())
                .placeholder(R.drawable.ic_baseline_tag_faces_24)
                .into(mProfilePictureCircularImageView);
        mLoadingIndicator.hide();
    }


    private void showErrorSnackBar(String errorString) {
        Snackbar.make(mBinding.getRoot(), errorString, BaseTransientBottomBar.LENGTH_LONG)
                .setBackgroundTint(requireContext().getColor(R.color.darkBlue))
                .setTextColor(requireContext().getColor(android.R.color.holo_red_light))
                .show();
    }

    private void showInformationSnackBar(String errorString) {
        Snackbar.make(mBinding.getRoot(), errorString, BaseTransientBottomBar.LENGTH_LONG)
                .setBackgroundTint(requireContext().getColor(R.color.darkBlue))
                .setTextColor(requireContext().getColor(android.R.color.white))
                .show();
    }

    private void goToChatPreviewFragment() {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout_profile,
                        ChatPreviewFragment.newInstance())
                .commit();
    }

    private void inflateMiddleFragment() {
        // TODO if no messages inflate with the empty messages view
        goToChatPreviewFragment();
    }
}