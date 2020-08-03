package com.erastus.orientate.student.profile.editprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.erastus.orientate.R;
import com.erastus.orientate.databinding.FragmentEditProfileBinding;
import com.erastus.orientate.student.profile.changepassword.ChangePasswordFragment;
import com.erastus.orientate.utils.ParentActivityImpl;
import com.erastus.orientate.utils.circularimageview.CircularImageView;
import com.google.android.material.textfield.TextInputLayout;

public class EditProfileFragment extends Fragment {
    private FragmentEditProfileBinding mBinding;
    private EditProfileViewModel mViewModel;
    private Button mChangePasswordButton;
    private ParentActivityImpl mHostActivity;
    private TextInputLayout mFirstNameTextInputLayout;
    private TextInputLayout mMiddleNameTextInputLayout;
    private TextInputLayout mLastNameTextInputLayout;
    private TextInputLayout mUsernameTextInputLayout;
    private TextInputLayout mEmailTextInputLayout;
    private CircularImageView mProfilePictureImageView;

    public static Fragment getInstance() {
        return new EditProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentEditProfileBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);
        performBindings();
        loadCurrentData();
        setUpOnClickListeners();
        return mBinding.getRoot();
    }

    private void loadCurrentData() {
        mHostActivity.setToolbar(mBinding.toolbarStudentEditProfile);
        mFirstNameTextInputLayout.getEditText().setText(mViewModel.getCurrentUser().getStudent().getFirstName());
        mMiddleNameTextInputLayout.getEditText().setText(mViewModel.getCurrentUser().getStudent().getMiddleName());
        mLastNameTextInputLayout.getEditText().setText(mViewModel.getCurrentUser().getStudent().getLastName());
        mUsernameTextInputLayout.getEditText().setText(mViewModel.getCurrentUser().getUsername());
        mEmailTextInputLayout.getEditText().setText(mViewModel.getCurrentUser().getEmail());

        Glide.with(requireContext()).load(mViewModel.getCurrentUser().getStudent().getProfileImageUrl()).centerCrop().into(mProfilePictureImageView);
    }

    private void setUpOnClickListeners() {
        mChangePasswordButton.setOnClickListener(view -> mHostActivity.addFragment(ChangePasswordFragment.newInstance()));
    }

    private void performBindings() {
        mHostActivity = (ParentActivityImpl) requireActivity();
        mChangePasswordButton = mBinding.buttonChangePassword;
        mFirstNameTextInputLayout = mBinding.textInputLayoutFirstNameEdit;
        mMiddleNameTextInputLayout = mBinding.textInputLayoutMiddleNameEdit;
        mLastNameTextInputLayout = mBinding.textInputLayoutLastNameEdit;
        mUsernameTextInputLayout = mBinding.textInputLayoutUsername;
        mEmailTextInputLayout = mBinding.textInputLayoutEmailEdit;
        mProfilePictureImageView = mBinding.imageViewProfilePicture;
    }
}