package com.erastus.orientate.student.chat.participants;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.erastus.orientate.R;
import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.student.chat.chatmessages.ParentFragment;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.utils.EmptyView;
import com.erastus.orientate.utils.customindicators.AVLoadingIndicatorView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ParticipantsFragment extends ParentFragment {
    private static final String ARGS_CONVERSATION = "ARGS_CONVERSATION";
    private static final String TAG = "ParticipantsFragment";

    ImageView mImage;

    TextView mDescription;

    RecyclerView mUsersRecyclerView;

    ParticipantsViewModel mViewModel;

    EmptyView mEmptyView;

    List<ExtendedParseUser> mUsers = new ArrayList<>();

    AVLoadingIndicatorView mIndicator;

    Conversation mConversation;

    ParticipantsAdapter mAdapter;


    public static ParticipantsFragment newInstance(Conversation conversation) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_CONVERSATION, Parcels.wrap(conversation));
        ParticipantsFragment fragment = new ParticipantsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this, new ParticipantsViewModelFactory(mConversation)).get(ParticipantsViewModel.class);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public int provideLayoutResourceId() {
        return R.layout.fragment_chat_info;
    }

    @Override
    public void performBindings(View rootView) {
        mImage = rootView.findViewById(R.id.image_view_info_image);
        mDescription = rootView.findViewById(R.id.info_description);
        mUsersRecyclerView = rootView.findViewById(R.id.info_recycler_view);
        mEmptyView = rootView.findViewById(R.id.info_empty_view);
        mIndicator = rootView.findViewById(R.id.av_loading_conversations_info);

    }

    private void loadDataIntoViews() {
        String imageUrl = mViewModel.getConversation().getProfileImageUrl();
        Log.d(TAG, "loadDataIntoViews: " + imageUrl);

        Glide.with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_icon)
                .centerCrop()
                .into(mImage);
    }

    private void setUpObservers() {
        mViewModel.getParticipants().observe(getViewLifecycleOwner(), listSimpleState -> {
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
    }

    private void dealWithData(List<ExtendedParseUser> data) {
        mAdapter.update(data);
    }

    @Override
    public void setViewBehaviour(boolean viewFromCache) {
        mUsersRecyclerView.setLayoutManager(new LinearLayoutManager(fragmentContext));
        mUsersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new ParticipantsAdapter(requireContext());
        mUsersRecyclerView.setAdapter(mAdapter);

        loadDataIntoViews();

        setUpObservers();
    }

    @Override
    public void onReady() {
    }

    @Override
    public void extractArguments() {
        super.extractArguments();
        assert getArguments() != null;
        mConversation = Parcels.unwrap(getArguments().getParcelable(ARGS_CONVERSATION));
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
