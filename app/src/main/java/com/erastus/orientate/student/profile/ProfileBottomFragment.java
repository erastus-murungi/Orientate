package com.erastus.orientate.student.profile;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.erastus.orientate.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ProfileBottomFragment extends BottomSheetDialogFragment {
    private View mRootView;
    private TextView mLogOutTextView;
    private TextView mAboutTextView;
    private TextView mEditProfileTextView;
    private ProfileViewModel mProfileViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {



        mRootView = getLayoutInflater().inflate(R.layout.fragment_bottom_student_profile, container, false);
        mProfileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        bindTextViews();
        return mRootView;
    }

    private void bindTextViews() {
        mLogOutTextView = mRootView.findViewById(R.id.text_view_log_out);
        mAboutTextView = mRootView.findViewById(R.id.text_view_about);
    }

    private void setUpOnClickListeners() {
        mLogOutTextView.setOnClickListener(view ->
                mProfileViewModel.notifyEventOptionSelected(ProfileOption.LOG_OUT));
        mAboutTextView.setOnClickListener(view ->
                mProfileViewModel.notifyEventOptionSelected(ProfileOption.ABOUT));
        mEditProfileTextView.setOnClickListener(view ->
                mProfileViewModel.notifyEventOptionSelected(ProfileOption.EDIT_PROFILE));
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        Dialog dialog = new BottomSheetDialog(requireActivity(), getTheme());
//        dialog.setOnShowListener(new Dialog.OnShowListener() {
//
//            @Override
//            public void onShow(DialogInterface dialogInterface) {
//            }
//        });
        return super.onCreateDialog(savedInstanceState);

    }
}
