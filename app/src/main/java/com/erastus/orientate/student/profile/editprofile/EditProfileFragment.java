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

import com.erastus.orientate.R;
import com.erastus.orientate.databinding.FragmentEditProfileBinding;
import com.erastus.orientate.student.profile.changepassword.ChangePasswordFragment;
import com.erastus.orientate.utils.ParentActivityImpl;

public class EditProfileFragment extends Fragment {
    private FragmentEditProfileBinding mBinding;
    private Button mChangePasswordButton;
    private ParentActivityImpl mHostActivity;

    public static Fragment getInstance() {
        return new EditProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentEditProfileBinding.inflate(inflater, container, false);
        performBindings();
        setUpOnClickListeners();
        return mBinding.getRoot();
    }

    private void setUpOnClickListeners() {
        mChangePasswordButton.setOnClickListener(view -> mHostActivity.addFragment(ChangePasswordFragment.newInstance()));
    }

    private void performBindings() {
        mHostActivity = (ParentActivityImpl) requireActivity();
        mChangePasswordButton = mBinding.buttonChangePassword;
    }
}