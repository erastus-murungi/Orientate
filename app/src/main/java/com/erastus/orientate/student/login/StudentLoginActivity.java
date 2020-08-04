package com.erastus.orientate.student.login;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.erastus.orientate.R;
import com.erastus.orientate.databinding.ActivityStudentLoginBinding;
import com.erastus.orientate.databinding.ProgressLoginInButtonBinding;
import com.erastus.orientate.student.navigation.StudentNavActivity;
import com.erastus.orientate.student.signup.StudentSignUpActivity;
import com.erastus.orientate.utils.Utils;
import com.erastus.orientate.utils.customindicators.AVLoadingIndicatorView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.parse.ParseUser;


public class StudentLoginActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 0;
    private static final String TAG = "StudentLoginActivity";

    private StudentLoginViewModel mLoginViewModel;
    private ActivityStudentLoginBinding mActivityStudentLoginBinding;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoginViewModel = new ViewModelProvider(this)
                .get(StudentLoginViewModel.class);

        configureGoogleSignIn();

        // no need for login in again
        if (ParseUser.getCurrentUser() != null) {
            // get the active student
            goToStudentNavActivity();
        }

        mActivityStudentLoginBinding = ActivityStudentLoginBinding.inflate(getLayoutInflater());
        setContentView(mActivityStudentLoginBinding.getRoot());

        final TextInputLayout usernameTextInputLayout = mActivityStudentLoginBinding.textInputLayoutUsername;
        final TextInputLayout passwordTextInputLayout = mActivityStudentLoginBinding.textInputLayoutPassword;
        final ProgressLoginInButtonBinding binding = mActivityStudentLoginBinding.buttonLogin;
        final View loginButton = binding.getRoot();
        final LoginButtonProgress loginButtonProgress = new LoginButtonProgress(binding);
        final Button signUpButton = mActivityStudentLoginBinding.buttonSignUp;
        final ImageButton googleSignInButton = mActivityStudentLoginBinding.googlePlusLogin;
        linkGoogleSignInButton(googleSignInButton);


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

        signUpButton.setOnClickListener(view -> goToSignUpActivity());
    }

    private void linkGoogleSignInButton(ImageButton button) {
        button.setOnClickListener(view -> {
            if (view.getId() == R.id.google_plus_login) {
                signInWithGoogle();
            }
        });
    }

    private void signInWithGoogle() {
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account == null) {
            showLoginFailed("failed login");
        } else {
            mLoginViewModel.googleLoginDataChanged(account);
        }
    }

    private void goToSignUpActivity() {
        startActivity(new Intent(this, StudentSignUpActivity.class));
    }

    private void goToStudentNavActivity() {
        // go the StudentNavigationActivity
        startActivity(new Intent(this, StudentNavActivity.class));
    }

    private void showLoginFailed(String errorString) {
        Snackbar.make(mActivityStudentLoginBinding.getRoot(), errorString, BaseTransientBottomBar.LENGTH_LONG)
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

    private void configureGoogleSignIn() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null)
            goToStudentNavActivity();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}