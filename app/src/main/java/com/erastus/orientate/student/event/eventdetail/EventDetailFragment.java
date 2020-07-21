package com.erastus.orientate.student.event.eventdetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.erastus.orientate.R;
import com.erastus.orientate.student.event.models.LocalEvent;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class EventDetailFragment extends BottomSheetDialogFragment {
    private EventDetailsViewModel mViewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this,
                new EventDetailsViewModelFactory((LocalEvent) requireArguments()
                        .getParcelable("DATA")))
                        .get(EventDetailsViewModel.class);
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        return view;
    }
}
