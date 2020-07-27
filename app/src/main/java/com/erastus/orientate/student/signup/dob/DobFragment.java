package com.erastus.orientate.student.signup.dob;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.erastus.orientate.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Objects;


public class DobFragment extends Fragment {

    public static final String ARGS_USERNAME = "username";
    public static final String ARGS_NAME = "name";
    public static final String ARGS_EMAIL = "email";

    private TextInputLayout mDateTextInputLayout;
    private ImageButton mSelectDate;

    public static DobFragment newInstance() {
        return new DobFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dob_profile_picture, container, false);
        mDateTextInputLayout = view.findViewById(R.id.text_input_layout_dob);
        mSelectDate = view.findViewById(R.id.image_button_pick_date);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DobViewModel mViewModel = new ViewModelProvider(this).get(DobViewModel.class);

        mSelectDate.setOnClickListener(view -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                    R.style.signUpDateOfBirthDatePicker,
                    (datePicker, i, i1, i2) ->
                            Objects.requireNonNull(mDateTextInputLayout.getEditText())
                                    .setText(getString(R.string.format_date, i, i1, i2)), c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });

        mViewModel.getDateIsValid().observe(getViewLifecycleOwner(), dateIsValid -> {
            if (dateIsValid == null) {
                return;
            }
            if (!dateIsValid) {
                mDateTextInputLayout.setError("Invalid date");
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
                mViewModel.dateChanged(mDateTextInputLayout.getEditText().getText().toString());
            }
        };
        mDateTextInputLayout.getEditText().addTextChangedListener(afterTextChangedListener);
    }

}