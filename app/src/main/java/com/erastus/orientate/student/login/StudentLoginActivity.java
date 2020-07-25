package com.erastus.orientate.student.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.erastus.orientate.R;
import com.erastus.orientate.databinding.ActivityStudentLoginBinding;
import com.erastus.orientate.databinding.ProgressLoginInButtonBinding;
import com.erastus.orientate.student.models.SimpleState;
import com.erastus.orientate.student.models.Student;
import com.erastus.orientate.student.navigation.StudentNavActivity;
import com.erastus.orientate.utils.Utils;
import com.erastus.orientate.utils.customindicators.AVLoadingIndicatorView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.parse.ParseUser;


public class StudentLoginActivity extends AppCompatActivity {

    private StudentLoginViewModel mLoginViewModel;
    private ActivityStudentLoginBinding activityStudentLoginBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoginViewModel = new ViewModelProvider(this)
                .get(StudentLoginViewModel.class);

        // no need for login in again
        if (ParseUser.getCurrentUser() != null) {
            // get the active student
            goToStudentNavActivity();
            mLoginViewModel.getStudent().observe(this, studentSimpleState -> {
                if (studentSimpleState == null) {
                    return;
                }
                if (studentSimpleState.getData() != null) {
                    goToStudentNavActivity();
                    finish();
                }
                if (studentSimpleState.getErrorMessage() != null) {
                    showLoginFailed(studentSimpleState.getErrorMessage());
                }

            });
            finish();
        }

        activityStudentLoginBinding = ActivityStudentLoginBinding.inflate(getLayoutInflater());
        setContentView(activityStudentLoginBinding.getRoot());

        final TextInputLayout usernameTextInputLayout = activityStudentLoginBinding.textInputLayoutUsername;
        final TextInputLayout passwordTextInputLayout = activityStudentLoginBinding.textInputLayoutPassword;
        final ProgressLoginInButtonBinding binding = activityStudentLoginBinding.buttonLogin;
        final View loginButton = binding.getRoot();
        final LoginButtonProgress loginButtonProgress = new LoginButtonProgress(binding);

        mLoginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameTextInputLayout.setError(getString(loginFormState.getUsernameError()));
                usernameTextInputLayout.getEditText()
                        .setBackground(getDrawable(R.drawable.text_view_background_signup_error));
            } else {
                usernameTextInputLayout.setError(null);
                usernameTextInputLayout.setErrorEnabled(false);
                usernameTextInputLayout.getEditText()
                        .setBackground(getDrawable(R.drawable.text_view_background_signup));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordTextInputLayout.setError(getString(loginFormState.getPasswordError()));
                passwordTextInputLayout.getEditText()
                        .setBackground(getDrawable(R.drawable.text_view_background_signup_error));
            } else {
                passwordTextInputLayout.setError(null);
                passwordTextInputLayout.setErrorEnabled(false);
                passwordTextInputLayout.getEditText()
                        .setBackground(getDrawable(R.drawable.text_view_background_signup));
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
                mLoginViewModel.loginDataChanged(usernameTextInputLayout.getEditText().getText().toString(),
                        passwordTextInputLayout.getEditText().getText().toString());
            }
        };
        usernameTextInputLayout.getEditText().addTextChangedListener(afterTextChangedListener);
        passwordTextInputLayout.getEditText().addTextChangedListener(afterTextChangedListener);
        passwordTextInputLayout.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginButtonProgress.buttonActivated();
                mLoginViewModel.login(usernameTextInputLayout.getEditText().getText().toString(),
                        passwordTextInputLayout.getEditText().getText().toString());
                Utils.forceHide(this, usernameTextInputLayout.getEditText());
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            loginButtonProgress.buttonActivated();
            mLoginViewModel.login(usernameTextInputLayout.getEditText().getText().toString(),
                    passwordTextInputLayout.getEditText().getText().toString());
            Utils.forceHide(this, usernameTextInputLayout.getEditText());
        });
    }

    private void goToStudentNavActivity() {
        // go the StudentNavigationActivity
        startActivity(new Intent(this, StudentNavActivity.class));
    }

    private void showLoginFailed(String errorString) {
        Snackbar.make(activityStudentLoginBinding.getRoot(), errorString, BaseTransientBottomBar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.darkBlue))
                .setTextColor(getColor(android.R.color.darker_gray))
                .show();
    }



    private static class LoginButtonProgress {
        private AVLoadingIndicatorView mProgressBar;
        private MaterialTextView mTextView;

        LoginButtonProgress(ProgressLoginInButtonBinding binding) {
            mProgressBar = binding.progressBarLoginIn;
            mTextView = binding.textViewLoginStudentLoading;
        }

        void buttonReset() {
            mProgressBar.setVisibility(View.GONE);
            mTextView.setText(R.string.continue_as_student);
            mProgressBar.hide();
        }

        void buttonActivated() {
            mProgressBar.show();
            mProgressBar.setVisibility(View.VISIBLE);
            mTextView.setText(R.string.login_in);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}