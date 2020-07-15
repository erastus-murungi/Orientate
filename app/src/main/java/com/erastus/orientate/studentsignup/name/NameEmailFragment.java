package com.erastus.orientate.studentsignup.name;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erastus.orientate.R;

public class NameEmailFragment extends Fragment {

    private NameEmailViewModel mNameEmailViewModel;

    public static NameEmailFragment newInstance() {
        return new NameEmailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_name_email, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mNameEmailViewModel = new ViewModelProvider(this).get(NameEmailViewModel.class);
        // TODO: Use the ViewModel
    }

}