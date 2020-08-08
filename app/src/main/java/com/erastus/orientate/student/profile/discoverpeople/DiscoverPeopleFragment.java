package com.erastus.orientate.student.profile.discoverpeople;

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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.erastus.orientate.R;
import com.erastus.orientate.databinding.DiscoverPeopleFragmentBinding;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.models.SimpleState;
import com.erastus.orientate.student.profile.editprofile.EditProfileFragment;
import com.erastus.orientate.utils.ParentActivityImpl;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

public class DiscoverPeopleFragment extends Fragment {
    DiscoverPeopleFragmentBinding mBinding;

    private DiscoverPeopleViewModel mViewModel;

    private SwitchMaterial mDiscoverSwitch;

    private ParentActivityImpl mHostActivity;

    private Button mMoreButton;

    private View mDiscoverableView;

    private TextView mUnDiscoverableTextView;

    private RecyclerView mPreviousConversationMatchesRecyclerView;

    private Adapter mAdapter;

    public static DiscoverPeopleFragment newInstance() {
        return new DiscoverPeopleFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DiscoverPeopleFragmentBinding.inflate(inflater, container, false);
        mHostActivity = (ParentActivityImpl) requireActivity();
        bindViews();
        return mBinding.getRoot();
    }

    private void bindViews() {
        mDiscoverSwitch = mBinding.switchDiscover;
        mMoreButton = mBinding.buttonAddPreferences;
        mUnDiscoverableTextView = mBinding.textViewUndiscoverable;
        mDiscoverableView = mBinding.getRoot().findViewById(R.id.root_switch_on);
        mPreviousConversationMatchesRecyclerView = mBinding.recyclerViewPreviousConversationMatches;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DiscoverPeopleViewModel.class);
        // TODO: Use the ViewModel

        mDiscoverSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                mViewModel.findMatch();
                mDiscoverableView.setVisibility(View.VISIBLE);
                mUnDiscoverableTextView.setVisibility(View.GONE);
                compoundButton.setEnabled(false);
            } else {
                mUnDiscoverableTextView.setVisibility(View.VISIBLE);
                mDiscoverableView.setVisibility(View.GONE);
            }
        });

        mMoreButton.setOnClickListener(view -> {
            mHostActivity.hideToolbar(true);
            mHostActivity.addFragment(EditProfileFragment.getInstance());
        });

        setUpObservers();

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        mAdapter = new Adapter(requireContext(), new ArrayList<>(), null);
        mPreviousConversationMatchesRecyclerView.setAdapter(mAdapter);
        mPreviousConversationMatchesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void setUpObservers() {
        mViewModel.getPreviousConversationMatchesState().observe(
                getViewLifecycleOwner(), listSimpleState -> {
                    if (listSimpleState == null) {
                        return;
                    }
                    if (listSimpleState.getData() != null) {
                        mAdapter.update(listSimpleState.getData());
                    }
                    if (listSimpleState.getErrorMessage() != null) {
                        showErrorAndReloadSnackbar(listSimpleState.getErrorMessage());
                    }

                });
    }

    private void showErrorAndReloadSnackbar(String errorMessage) {
        Snackbar.make(mBinding.getRoot(), errorMessage, BaseTransientBottomBar.LENGTH_LONG)
                .setAction(R.string.reload, view -> mViewModel.reload())
                .setBackgroundTint(requireActivity().getColor(R.color.colorPrimaryDark))
                .show();
    }

}