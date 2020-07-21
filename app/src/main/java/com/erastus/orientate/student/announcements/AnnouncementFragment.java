package com.erastus.orientate.student.announcements;

import androidx.annotation.ColorRes;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.erastus.orientate.R;
import com.erastus.orientate.databinding.AnnouncementFragmentBinding;
import com.erastus.orientate.student.announcements.models.LocalAnnouncement;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;


public class AnnouncementFragment extends Fragment {

    private AnnouncementViewModel mViewModel;
    private RecyclerView mAnnouncementRecyclerView;
    private AnnouncementFragmentBinding mBinding;
    private AnnouncementAdapter mAdapter;
    private ProgressBar mProgressBar;

    public AnnouncementFragment() {
    }

    public static AnnouncementFragment newInstance() {
        return new AnnouncementFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this,
                new AnnouncementViewModelFactory()).get(AnnouncementViewModel.class);

        mBinding = AnnouncementFragmentBinding.inflate(getLayoutInflater());
        View rootView = inflater.inflate(R.layout.announcement_fragment, container, false);
        mProgressBar = mBinding.progressBarAnnouncements;
        mAnnouncementRecyclerView = rootView.findViewById(R.id.recycler_view_announcement);
        initRecyclerView();
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpObservers();
        mAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        mAdapter = new AnnouncementAdapter(getActivity(), mViewModel.getAnnouncements().getValue());
        mAnnouncementRecyclerView.setAdapter(mAdapter);
        mAnnouncementRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setUpObservers() {

        mViewModel.getAnnouncements().observe(getViewLifecycleOwner(), localAnnouncements -> {
            mAdapter.setAnnouncements(localAnnouncements);
            mAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.GONE);
        });

        mViewModel.getState().observe(getViewLifecycleOwner(), announcementState -> {
            if (announcementState == null) {
                return;
            }
            if (announcementState.isLoading()) {
                mProgressBar.setVisibility(View.VISIBLE);
            } else if (announcementState.getErrorMessage() != null) {
                mProgressBar.setVisibility(View.GONE);
                promptReloadWithSnackBar(announcementState.getErrorMessage());
            } else {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void promptReloadWithSnackBar(String errorMessage) {
        Snackbar.make(requireActivity().findViewById(R.id.drawer_layout_student), errorMessage,
                BaseTransientBottomBar.LENGTH_INDEFINITE)
                .setBackgroundTint(requireContext().getColor(R.color.white))
                .setTextColor(requireContext().getColor(R.color.darkBlue))
                .setActionTextColor(requireContext().getColor(R.color.darkBlue))
                .setAction(R.string.reload, view -> mViewModel.requestReload());
    }

}