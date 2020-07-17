package com.erastus.orientate.student.announcements;

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
import com.google.android.material.snackbar.Snackbar;


public class AnnouncementFragment extends Fragment {

    private AnnouncementViewModel mViewModel;
    private RecyclerView mAnnouncementRecyclerView;
    private AnnouncementFragmentBinding mBinding;
    private AnnouncementAdapter mAdapter;
    private ProgressBar mProgressBar;

    public static AnnouncementFragment newInstance() {
        return new AnnouncementFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = AnnouncementFragmentBinding.inflate(getLayoutInflater());
        View view = inflater.inflate(R.layout.announcement_fragment, container, false);
        mAnnouncementRecyclerView = mBinding.recyclerViewAnnouncement;
        mProgressBar = mBinding.progressBarAnnouncements;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AnnouncementViewModel.class);
        initRecyclerView();
        setUpObservers();
    }

    private void initRecyclerView() {
        mAdapter = new AnnouncementAdapter(mViewModel.getAnnouncements().getValue());
        mAnnouncementRecyclerView.setAdapter(mAdapter);
        mAnnouncementRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setUpObservers() {
        mViewModel.getAnnouncements().observe(getViewLifecycleOwner(), localAnnouncements -> mAdapter.notifyDataSetChanged());
        mViewModel.getState().observe(getViewLifecycleOwner(), announcementState -> {
            if (announcementState == null) {
                return;
            }
            if (announcementState.isLoading()) {
                mProgressBar.setVisibility(View.VISIBLE);
            } else {
                mProgressBar.setVisibility(View.GONE);
            }
            if (announcementState.getErrorMessage() != null) {
                showErrorMessage(announcementState.getErrorMessage());
            }
        });
    }

    private void showErrorMessage(String errorMessage) {
    }

}