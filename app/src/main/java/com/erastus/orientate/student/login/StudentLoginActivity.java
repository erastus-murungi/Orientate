package com.erastus.orientate.student.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.erastus.orientate.R;
import com.erastus.orientate.databinding.ActivityStudentLoginBinding;
import com.erastus.orientate.databinding.ProgressLoginInButtonBinding;
import com.erastus.orientate.student.navigation.StudentNavActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.parse.ParseUser;

import java.util.Objects;


public class StudentLoginActivity extends AppCompatActivity {

    private StudentLoginViewModel mLoginViewModel;
    private ActivityStudentLoginBinding activityStudentLoginBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
//        if (ParseUser.getCurrentUser() != null) {
//            goToStudentNavActivity();
//        }

        activityStudentLoginBinding = ActivityStudentLoginBinding.inflate(getLayoutInflater());
        setContentView(activityStudentLoginBinding.getRoot());
        mLoginViewModel = new ViewModelProvider(this, new StudentLoginViewModelFactory())
                .get(StudentLoginViewModel.class);

        final TextInputLayout usernameEditText = activityStudentLoginBinding.textInputLayoutUsername;
        final TextInputLayout passwordEditText = activityStudentLoginBinding.textInputLayoutPassword;
        final ProgressLoginInButtonBinding binding = activityStudentLoginBinding.buttonLogin;
        final View loginButton = binding.getRoot();
        final LoginButtonProgress loginButtonProgress = new LoginButtonProgress(binding);

        mLoginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                Objects.requireNonNull(usernameEditText.getEditText()).setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                Objects.requireNonNull(passwordEditText.getEditText()).setError(getString(loginFormState.getPasswordError()));
            }
        });

        mLoginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            if (loginResult.getErrorMessage() != null) {
                showLoginFailed(loginResult.getErrorMessage());
                loginButtonProgress.buttonReset();
            } else {
                loginButtonProgress.buttonActivated();
                goToStudentNavActivity();

                setResult(Activity.RESULT_OK);

                // Complete and destroy login activity once successful
                finish();
            }

        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
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
                mLoginViewModel.loginDataChanged(usernameEditText.getEditText().getText().toString(),
                        passwordEditText.getEditText().getText().toString());
            }
        };
        usernameEditText.getEditText().addTextChangedListener(afterTextChangedListener);
        passwordEditText.getEditText().addTextChangedListener(afterTextChangedListener);
        passwordEditText.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginButtonProgress.buttonActivated();
                mLoginViewModel.login(usernameEditText.getEditText().getText().toString(),
                        passwordEditText.getEditText().getText().toString());
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            loginButtonProgress.buttonActivated();
            mLoginViewModel.login(usernameEditText.getEditText().getText().toString(),
                    passwordEditText.getEditText().getText().toString());
        });
    }

    private void goToStudentNavActivity() {
        // go the StudentNavigationActivity
        startActivity(new Intent(this, StudentNavActivity.class));
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Snackbar.make(activityStudentLoginBinding.getRoot(), errorString, Snackbar.LENGTH_SHORT).show();
    }

    private void showLoginFailed(String errorString) {
        Snackbar.make(activityStudentLoginBinding.getRoot(), errorString, Snackbar.LENGTH_SHORT).show();
    }

    private class LoginButtonProgress {
        private ProgressBar mProgressBar;
        private MaterialTextView mTextView;

        LoginButtonProgress(ProgressLoginInButtonBinding binding) {
            mProgressBar = binding.progressBarLoginIn;
            mTextView = binding.textViewLoginStudentLoading;
        }

        void buttonReset() {
            mProgressBar.setVisibility(View.GONE);
            mTextView.setText(R.string.continue_as_student);
        }

        void buttonActivated() {
            mProgressBar.setVisibility(View.VISIBLE);
            mTextView.setText(R.string.login_in);
        }

        void buttonFinished() {
            mProgressBar.setVisibility(View.GONE);
            mTextView.setText(R.string.done);
        }
    }
}