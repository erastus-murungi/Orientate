package com.erastus.orientate.student.signup.name;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.erastus.orientate.R;
import com.erastus.orientate.databinding.ButtonContinueSignupBinding;
import com.erastus.orientate.databinding.FragmentNameEmailBinding;
import com.erastus.orientate.databinding.ProgressSignUpButtonBinding;
import com.erastus.orientate.student.signup.ParentSignUpActivity;
import com.erastus.orientate.utils.customindicators.AVLoadingIndicatorView;
import com.google.android.material.textfield.TextInputLayout;

public class NameEmailFragment extends Fragment {

    private static final String TAG = "NameEmailFragment";

    private NameEmailViewModel mNameEmailViewModel;

    private FragmentNameEmailBinding mFragmentNameEmailBinding;

    private TextInputLayout mFullNameTextInputLayout;

    private TextInputLayout mUsernameTextInputLayout;

    private TextInputLayout mEmailTextInputLayout;

    private View mContinueButtonView;

    private NameEmailViewModel mViewModel;

    private ShouldContinueButton mContinueButton;

    protected ParentSignUpActivity hostActivity;

    public static NameEmailFragment newInstance() {
        return new NameEmailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mFragmentNameEmailBinding = FragmentNameEmailBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this).get(NameEmailViewModel.class);
        performBindings();

        return mFragmentNameEmailBinding.getRoot();
    }

    private void performBindings() {
        mFullNameTextInputLayout = mFragmentNameEmailBinding.textInputLayoutFullName;
        mUsernameTextInputLayout = mFragmentNameEmailBinding.textInputLayoutUsernameSignUp;
        mEmailTextInputLayout = mFragmentNameEmailBinding.textInputLayoutEmailSignUp;
        mContinueButton = new ShouldContinueButton(mFragmentNameEmailBinding.buttonProceedNameEmail);
        mContinueButtonView = mFragmentNameEmailBinding.buttonProceedNameEmail.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mNameEmailViewModel = new ViewModelProvider(this).get(NameEmailViewModel.class);

        setUpObservers();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        hostActivity = (ParentSignUpActivity) context;
    }

    private void setUpObservers() {
        setUpTextChangedListeners();
        setUpUserNameObserver();
        setUpFullNameObserver();
        setUpEmailObserver();
        setUpContinueButtonBehavior();
    }

    private void setUpContinueButtonBehavior() {
        mViewModel.getInputAcceptable().observe(getViewLifecycleOwner(), enabled -> {
            if (enabled) {
                // persist data to next fragment
                mContinueButtonView.setEnabled(true);
                mFragmentNameEmailBinding
                        .buttonProceedNameEmail
                        .constraintLayoutInner
                        .setBackgroundColor(requireContext().getColor(R.color.colorPrimaryDark));
            } else {
                mContinueButtonView.setEnabled(false);
                mFragmentNameEmailBinding
                        .buttonProceedNameEmail
                        .constraintLayoutInner
                        .setBackgroundColor(requireContext().getColor(R.color.blue));
            }
        });

        mContinueButtonView.setOnClickListener(view -> {
            Log.d(TAG, "setUpContinueButtonBehavior: done");

            hostActivity.setTab(1);
            hostActivity.getViewModel().emailChanged(mEmailTextInputLayout.getEditText().getText().toString());
            hostActivity.getViewModel().fullNameChanged(mFullNameTextInputLayout.getEditText().getText().toString());
            hostActivity.getViewModel().userNameChanged(mUsernameTextInputLayout.getEditText().getText().toString());
        });
    }

    private void setUpEmailObserver() {
        mViewModel.getEmailAcceptable().observe(getViewLifecycleOwner(), inputValid -> {
            if (inputValid.getErrorCode() != null) {
                mEmailTextInputLayout.setError(getString(inputValid.getErrorCode()));
                mEmailTextInputLayout.getEditText()
                        .setBackground(requireContext().getDrawable(R.drawable.text_view_background_signup_error));
            } else if (inputValid.getErrorString() != null) {
                mEmailTextInputLayout.setError(inputValid.getErrorString());
                mEmailTextInputLayout.getEditText()
                        .setBackground(requireContext().getDrawable(R.drawable.text_view_background_signup_error));
            }
            else if (inputValid.isDataValid()){
                mEmailTextInputLayout.setError(null);
                mEmailTextInputLayout.setErrorEnabled(false);
                mEmailTextInputLayout.getEditText()
                        .setBackground(requireContext().getDrawable(R.drawable.text_view_background_signup));
            }
        });
    }

    private void setUpFullNameObserver() {
        mViewModel.getFullNameAcceptable().observe(getViewLifecycleOwner(), inputValid -> {
            if (inputValid.getErrorCode() != null) {
                mFullNameTextInputLayout.setError(getString(inputValid.getErrorCode()));
                mFullNameTextInputLayout.getEditText()
                        .setBackground(requireContext().getDrawable(R.drawable.text_view_background_signup_error));
            } else if (inputValid.isDataValid()){
                mFullNameTextInputLayout.setError(null);
                mFullNameTextInputLayout.setErrorEnabled(false);
                mFullNameTextInputLayout.getEditText()
                        .setBackground(requireContext().getDrawable(R.drawable.text_view_background_signup));
            }
        });
    }

    private void setUpUserNameObserver() {
        mViewModel.getUserNameAcceptable().observe(getViewLifecycleOwner(), inputValid -> {
            if (inputValid.getErrorCode() != null) {
                mUsernameTextInputLayout.setError(getString(inputValid.getErrorCode()));
                mUsernameTextInputLayout.getEditText()
                        .setBackground(requireContext().getDrawable(R.drawable.text_view_background_signup_error));
            } else if (inputValid.isDataValid()){
                mUsernameTextInputLayout.setError(null);
                mUsernameTextInputLayout.setErrorEnabled(false);
                mUsernameTextInputLayout.getEditText()
                        .setBackground(requireContext().getDrawable(R.drawable.text_view_background_signup));
            }
        });
    }


    private void setUpTextChangedListeners() {
        TextWatcher userNameAfterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                mViewModel.usernameInputChanged(mUsernameTextInputLayout.getEditText().getText().toString());
            }
        };

        TextWatcher fullNameAfterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                mViewModel.fullNameInputChanged(mFullNameTextInputLayout.getEditText().getText().toString());
            }
        };

        TextWatcher emailAfterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                mViewModel.emailInputChanged(mEmailTextInputLayout.getEditText().getText().toString());
            }
        };

        mUsernameTextInputLayout.getEditText().addTextChangedListener(userNameAfterTextChangedListener);
        mFullNameTextInputLayout.getEditText().addTextChangedListener(fullNameAfterTextChangedListener);
        mEmailTextInputLayout.getEditText().addTextChangedListener(emailAfterTextChangedListener);
    }

    private static class ShouldContinueButton {
        private AVLoadingIndicatorView mProgressBar;

        ShouldContinueButton(ButtonContinueSignupBinding binding) {
            mProgressBar = binding.progressBarLoginIn;
        }

        void buttonReset() {
            mProgressBar.setVisibility(View.GONE);
            mProgressBar.hide();
        }

        void buttonActivated() {
            mProgressBar.show();
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

}