package com.erastus.orientate.student.signup.dob;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.erastus.orientate.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;


public class DobFragment extends Fragment {

    private DobViewModel mViewModel;

    private TextInputEditText mDateTextInput;

    public static DobFragment newInstance() {
        return new DobFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dob_fragment, container, false);
        mDateTextInput = view.findViewById(R.id.text_view_dob);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DobViewModel.class);
        // TODO: Use the ViewModel

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        final MaterialDatePicker<?> picker = builder.build();
        mDateTextInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picker.show(getParentFragmentManager(), picker.toString());
            }
        });
    }

}