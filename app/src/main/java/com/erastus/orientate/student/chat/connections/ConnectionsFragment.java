package com.erastus.orientate.student.chat.connections;

import androidx.annotation.StringRes;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.erastus.orientate.R;
import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.student.chat.chatmessages.ParentFragment;
import com.erastus.orientate.student.chat.conversations.ConversationFragment;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.chat.participants.ParticipantsAdapter;
import com.erastus.orientate.student.chat.participants.ParticipantsFragment;
import com.erastus.orientate.student.chat.participants.ParticipantsViewModel;
import com.erastus.orientate.student.chat.participants.ParticipantsViewModelFactory;
import com.erastus.orientate.student.models.SimpleState;
import com.erastus.orientate.utils.EmptyView;
import com.erastus.orientate.utils.ParentActivityImpl;
import com.erastus.orientate.utils.customindicators.AVLoadingIndicatorView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ConnectionsFragment extends ParentFragment {

    private ConnectionsViewModel mViewModel;

    RecyclerView mConnectionsRecyclerView;

    EmptyView mEmptyView;

    AVLoadingIndicatorView mIndicator;

    ConnectionsAdapter mAdapter;

    ParentActivityImpl mParentHostActivity;

    public static ConnectionsFragment newInstance() {
        return new ConnectionsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ConnectionsViewModel.class);
        mParentHostActivity = (ParentActivityImpl) requireActivity();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static ConnectionsFragment newInstance(Conversation conversation) {
        Bundle args = new Bundle();
        ConnectionsFragment fragment = new ConnectionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int provideLayoutResourceId() {
        return R.layout.connections_fragment;
    }

    @Override
    public void performBindings(View rootView) {
        mConnectionsRecyclerView = rootView.findViewById(R.id.info_recycler_view);
        mEmptyView = rootView.findViewById(R.id.info_empty_view);
        mIndicator = rootView.findViewById(R.id.av_loading_conversations_info);

    }

    private void setUpObservers() {
        mViewModel.getConnections().observe(getViewLifecycleOwner(), listSimpleState -> {
            if (listSimpleState == null || listSimpleState.isLoading()) {
                mIndicator.show();
                return;
            }
            mIndicator.hide();
            if (listSimpleState.getErrorMessage() != null) {
                showErrorSnackBar(listSimpleState.getErrorMessage());
            }
            if (listSimpleState.getErrorCode() != null) {
                showErrorSnackBar(requireContext().getString(listSimpleState.getErrorCode()));
            }
            if (listSimpleState.getInternalErrorOccurred() != null) {
                showInformationSnackBar("Internal error occurred");
            }
            else {
                dealWithData(listSimpleState.getData());
            }
        });

        mViewModel.getStartConversation().observe(getViewLifecycleOwner(), conversationSimpleState -> {
            if (conversationSimpleState == null)  {
                return;
            }
            if (conversationSimpleState.getData() != null) {
                mParentHostActivity.addFragment(ConversationFragment.newInstance(conversationSimpleState.getData()));
            }
            if (conversationSimpleState.getErrorMessage() != null) {
                showErrorSnackBar(conversationSimpleState.getErrorMessage());
            }
        });
    }

    private void dealWithData(List<ExtendedParseUser> data) {
        mAdapter.update(data);
    }

    @Override
    public void setViewBehaviour(boolean viewFromCache) {
        mConnectionsRecyclerView.setLayoutManager(new LinearLayoutManager(fragmentContext));
        mConnectionsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new ConnectionsAdapter(requireContext(),
                user -> mViewModel.startConversationWithUser(user));
        mConnectionsRecyclerView.setAdapter(mAdapter);
        setUpObservers();
    }

    @Override
    public void onReady() {
    }

    @Override
    public void extractArguments() {
    }

    private void showErrorSnackBar(String errorString) {
        Snackbar.make(getRootView(), errorString, BaseTransientBottomBar.LENGTH_INDEFINITE)
                .setBackgroundTint(requireContext().getColor(R.color.darkBlue))
                .setTextColor(requireContext().getColor(android.R.color.holo_red_light))
                .setAction(R.string.reload, view -> mViewModel.reload())
                .show();
    }

    private void showErrorSnackBar(@StringRes Integer errorCode) {
        Snackbar.make(getRootView(), errorCode, BaseTransientBottomBar.LENGTH_INDEFINITE)
                .setBackgroundTint(requireContext().getColor(R.color.darkBlue))
                .setTextColor(requireContext().getColor(android.R.color.holo_red_light))
                .setAction(R.string.reload, view -> mViewModel.reload())
                .show();
    }

    private void showInformationSnackBar(String errorString) {
        Snackbar.make(getRootView(), errorString, BaseTransientBottomBar.LENGTH_LONG)
                .setBackgroundTint(requireContext().getColor(R.color.darkBlue))
                .setTextColor(requireContext().getColor(android.R.color.white))
                .show();
    }

}