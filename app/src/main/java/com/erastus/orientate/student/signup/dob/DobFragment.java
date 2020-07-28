package com.erastus.orientate.student.signup.dob;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.erastus.orientate.R;
import com.erastus.orientate.databinding.FragmentDobProfilePictureBinding;
import com.erastus.orientate.student.login.StudentLoginActivity;
import com.erastus.orientate.student.signup.InputValid;
import com.erastus.orientate.student.signup.ParentSignUpActivity;
import com.erastus.orientate.student.signup.StudentSignUpViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.util.Calendar;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


public class DobFragment extends Fragment implements ParentSignUpActivity {
    private static final String TAG = "DobFragment";
    public static final int PERMISSIONS_CODE = 1000;
    public static final int CAMERA_REQUEST_CODE = 1001;
    public static final String FILENAME = "profile_picture.jpg";

    private File mPhotoFile;

    private TextInputLayout mPasswordTextInputLayout;

    private TextInputLayout mConfirmPasswordTextInputLayout;

    private TextInputLayout mDateTextInputLayout;

    private ImageButton mSelectDateImageButton;

    private ImageView mSelectProfilePictureImageView;

    private ImageView mProfilePicturePreviewImageView;

    private Button mRetakeButton;

    private FragmentDobProfilePictureBinding mDobBinding;

    private DobViewModel mViewModel;

    private View mContinueButtonView;

    ParentSignUpActivity hostActivity;


    public static DobFragment newInstance() {
        return new DobFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mDobBinding = FragmentDobProfilePictureBinding.inflate(inflater, container, false);
        performBindings();
        mViewModel = new ViewModelProvider(this).get(DobViewModel.class);
        return mDobBinding.getRoot();
    }

    private void performBindings() {
        mDateTextInputLayout = mDobBinding.textInputLayoutDob;
        mSelectDateImageButton = mDobBinding.imageButtonPickDate;
        mSelectProfilePictureImageView = mDobBinding.imageViewSelectProfilePicture;
        mProfilePicturePreviewImageView = mDobBinding.imageViewProfilePicturePreview;
        mPasswordTextInputLayout = mDobBinding.textInputLayoutPassword;
        mConfirmPasswordTextInputLayout = mDobBinding.textInputLayoutConfirmPassword;
        mRetakeButton = mDobBinding.buttonRetakePicture;
        mContinueButtonView = mDobBinding.buttonProceedNameEmail.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DobViewModel.class);

        mSelectDateImageButton.setOnClickListener(view -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                    R.style.signUpDateOfBirthDatePicker,
                    (datePicker, i, i1, i2) ->
                            Objects.requireNonNull(mDateTextInputLayout.getEditText())
                                    .setText(getString(R.string.format_date, i, i1, i2)), c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });

        setUpTextWatchers();
        setUpObservers();
        setUpCameraBehavior();
        setUpSignUpResultBehavior();
    }

    private void setUpSignUpResultBehavior() {
        hostActivity.getViewModel().getSignUpResult().observe(getViewLifecycleOwner(),
                inputValid -> {
                    if (inputValid == null) {
                        return;
                    }
                    if (inputValid.getErrorCode() != null) {
                        showErrorSnackBar(requireContext().getString(inputValid.getErrorCode()));
                    }
                    else if (inputValid.getErrorString() != null) {
                        showErrorSnackBar(inputValid.getErrorString());
                    }
                    else if (inputValid.isDataValid()) {
                        goToLoginActivity();
                    }

                });
    }

    private void goToLoginActivity() {
        startActivity(new Intent(requireContext(), StudentLoginActivity.class));
    }

    private void setUpCameraBehavior() {
        mSelectProfilePictureImageView.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (requireActivity().checkSelfPermission(Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED ||
                        requireActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_DENIED) {
                    String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PERMISSIONS_CODE);
                } else {
                    openCamera();
                }
            } else {
                openCamera();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                showErrorSnackBar("Permissions denied!");
            }
        }
    }

    private void showErrorSnackBar(String s) {
        Snackbar.make(mDobBinding.getRoot(), s, BaseTransientBottomBar.LENGTH_LONG)
                .setBackgroundTint(requireContext().getColor(R.color.darkBlue))
                .setTextColor(requireContext().getColor(android.R.color.white))
                .show();
    }

    private void openCamera() {

        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        mPhotoFile = getPhotoFileUri();

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(requireContext(), "com.erastus.orientate", mPhotoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }

    private File getPhotoFileUri() {
        File mediaStorageDir = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename

        return new File(mediaStorageDir.getPath() + File.separator + FILENAME);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            // by this point we have the camera photo on disk
            mViewModel.bitmapChanged(BitmapFactory.decodeFile(mPhotoFile.getAbsolutePath()));
            // RESIZE BITMAP, see section below
            // Load the taken image into a preview
        } else { // Result was a failure
            showErrorSnackBar("Picture wasn't taken!");
        }
    }

    private void setUpObservers() {
        observeDateOfBirthInput();
        observePasswordInput();
        observeConfirmPasswordInput();
        observeImagePreview();
        observeContinueButtonBehavior();
    }

    private void observeImagePreview() {
        mViewModel.getImageBitMap().observe(getViewLifecycleOwner(), bitmap -> {
            if (bitmap == null) {
                mRetakeButton.setVisibility(View.GONE);
                return;
            }
            mProfilePicturePreviewImageView.setImageBitmap(bitmap);
            mRetakeButton.setVisibility(View.VISIBLE);
        });
    }

    private void observeConfirmPasswordInput() {
        mViewModel.getConfirmPasswordValid().observe(getViewLifecycleOwner(), inputValid -> {
            if (inputValid.getErrorCode() != null) {
                mConfirmPasswordTextInputLayout.setError(getString(inputValid.getErrorCode()));
                mConfirmPasswordTextInputLayout.getEditText()
                        .setBackground(requireContext().getDrawable(R.drawable.text_view_background_signup_error));
            }
            if (inputValid.isDataValid()) {
                mConfirmPasswordTextInputLayout.setError(null);
                mConfirmPasswordTextInputLayout.setErrorEnabled(false);
                mConfirmPasswordTextInputLayout.getEditText()
                        .setBackground(requireContext().getDrawable(R.drawable.text_view_background_signup));
            }
        });
    }

    private void observePasswordInput() {
        mViewModel.getPasswordValid().observe(getViewLifecycleOwner(), inputValid -> {
            if (inputValid.getErrorCode() != null) {
                mPasswordTextInputLayout.setError(getString(inputValid.getErrorCode()));
                mPasswordTextInputLayout.getEditText()
                        .setBackground(requireContext().getDrawable(R.drawable.text_view_background_signup_error));
            } else if (inputValid.isDataValid()) {
                mPasswordTextInputLayout.setError(null);
                mPasswordTextInputLayout.setErrorEnabled(false);
                mPasswordTextInputLayout.getEditText()
                        .setBackground(requireContext().getDrawable(R.drawable.text_view_background_signup));
            }
        });

    }

    private void observeDateOfBirthInput() {
        mViewModel.getDateIsValid().observe(getViewLifecycleOwner(), inputValid -> {
            if (inputValid.getErrorCode() != null) {
                mDateTextInputLayout.setError(getString(inputValid.getErrorCode()));
                mDateTextInputLayout.getEditText()
                        .setBackground(requireContext().getDrawable(R.drawable.text_view_background_signup_error));
            } else if (inputValid.isDataValid()) {
                mDateTextInputLayout.setError(null);
                mDateTextInputLayout.setErrorEnabled(false);
                mDateTextInputLayout.getEditText()
                        .setBackground(requireContext().getDrawable(R.drawable.text_view_background_signup));
            }
        });
    }

    private void observeContinueButtonBehavior() {
        mViewModel.getInputAcceptable().observe(getViewLifecycleOwner(), enabled -> {
            if (enabled) {
                // persist data to next fragment
                mContinueButtonView.setEnabled(true);
                mDobBinding
                        .buttonProceedNameEmail
                        .constraintLayoutInner
                        .setBackgroundColor(requireContext().getColor(R.color.colorPrimaryDark));
            } else {
                mContinueButtonView.setEnabled(false);
                mDobBinding
                        .buttonProceedNameEmail
                        .constraintLayoutInner
                        .setBackgroundColor(requireContext().getColor(R.color.blue));
            }
        });

        mContinueButtonView.setOnClickListener(view -> {
            Log.d(TAG, "setUpContinueButtonBehavior: done");

            hostActivity.setTab(1);
            hostActivity.getViewModel().passwordChanged(mPasswordTextInputLayout.getEditText().getText().toString());
            hostActivity.getViewModel().dateOfBirthChanged(mDateTextInputLayout.getEditText().getText().toString());
            hostActivity.getViewModel().profilePictureChanged(mPhotoFile);
            hostActivity.getViewModel().signUp();
        });
    }

    private void setUpTextWatchers() {

        TextWatcher dateAfterTextChangedListener = new TextWatcher() {
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
                mViewModel.dateChanged(mDateTextInputLayout.getEditText().getText().toString());
            }
        };
        mDateTextInputLayout.getEditText().addTextChangedListener(dateAfterTextChangedListener);


        TextWatcher passwordAfterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mViewModel.passwordChanged(mPasswordTextInputLayout.getEditText().getText().toString());
            }
        };

        TextWatcher confirmPasswordTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mViewModel.confirmPasswordChanged(mPasswordTextInputLayout.getEditText().getText().toString(),
                        mConfirmPasswordTextInputLayout.getEditText().getText().toString());
            }
        };

        mPasswordTextInputLayout.getEditText().addTextChangedListener(passwordAfterTextChangedListener);
        mConfirmPasswordTextInputLayout.getEditText().addTextChangedListener(confirmPasswordTextChangedListener);
        mDateTextInputLayout.getEditText().addTextChangedListener(dateAfterTextChangedListener);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        hostActivity = (ParentSignUpActivity) getActivity();
    }

    @Override
    public void setTab(int position) {

    }

    @Override
    public StudentSignUpViewModel getViewModel() {
        return null;
    }
}