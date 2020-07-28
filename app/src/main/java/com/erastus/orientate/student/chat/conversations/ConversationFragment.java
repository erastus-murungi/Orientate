package com.erastus.orientate.student.chat.conversations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.StringRes;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erastus.orientate.R;
import com.erastus.orientate.databinding.FragmentConversationBinding;
import com.erastus.orientate.student.chat.chatmessages.ChatFragment;
import com.erastus.orientate.student.chat.chatmessages.ParentFragment;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.utils.EmptyView;
import com.erastus.orientate.utils.customindicators.AVLoadingIndicatorView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ConversationFragment extends ParentFragment
        implements ConversationAdapter.OnConversationClicked {

    private FragmentConversationBinding mBinding;
    private RecyclerView mRecyclerView;
    private EmptyView mEmptyView;
    private AVLoadingIndicatorView mLoadingIndicator;
    private ConversationViewModel mViewModel;
    private ConversationAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ConversationFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ConversationFragment newInstance() {
        return new ConversationFragment();
    }

    @Override
    public int provideLayoutResourceId() {
        return R.id.relative_layout_conversations;
    }

    @Override
    public void performBindings(View rootView) {

    }

    @Override
    public void setViewBehaviour(boolean viewFromCache) {

    }

    @Override
    public void onReady() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = FragmentConversationBinding.inflate(getLayoutInflater(), container, false);
        mViewModel = new ViewModelProvider(this).get(ConversationViewModel.class);
        mRecyclerView = mBinding.recyclerViewConversations;
        mEmptyView = mBinding.emptyViewNoConversations;
        mLoadingIndicator = mBinding.avLoadingConversations;
        mLoadingIndicator.show();
        setUpRecyclerView();
        setUpObservers();
        return mBinding.getRoot();
    }

    private void setUpObservers() {
        mViewModel.getState().observe(getViewLifecycleOwner(), listSimpleState -> {
            if (!listSimpleState.isLoading()) {
                mLoadingIndicator.hide();
            }
            mLoadingIndicator.hide();
            if (listSimpleState.getErrorMessage() != null) {
                showErrorSnackBar(listSimpleState.getErrorMessage());
            }
            if (listSimpleState.getErrorCode() != null) {
                showErrorSnackBar(listSimpleState.getErrorCode());
            }
            if (listSimpleState.getData() != null) {
                if (listSimpleState.getData() == null || listSimpleState.getData().size() == 0) {
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    notifyRecyclerViewer(listSimpleState.getData());
                }
            }
        });
    }

    private void notifyRecyclerViewer(List<Conversation> data) {
        mAdapter.update(data);
    }

    private void showErrorSnackBar(String errorString) {
        Snackbar.make(mBinding.getRoot(), errorString, BaseTransientBottomBar.LENGTH_LONG)
                .setBackgroundTint(requireContext().getColor(R.color.darkBlue))
                .setTextColor(requireContext().getColor(android.R.color.holo_red_light))
                .show();
    }

    private void showErrorSnackBar(@StringRes Integer errorCode) {
        Snackbar.make(mBinding.getRoot(), errorCode, BaseTransientBottomBar.LENGTH_LONG)
                .setBackgroundTint(requireContext().getColor(R.color.darkBlue))
                .setTextColor(requireContext().getColor(android.R.color.holo_red_light))
                .show();
    }

    private void setUpRecyclerView() {

        // Set the adapter
        mAdapter = new ConversationAdapter(getContext(), new ArrayList<>(), this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        mRecyclerView.setAdapter(mAdapter);
        linearLayoutManager.setReverseLayout(false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), RecyclerView.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(requireContext().getDrawable(R.drawable.chats_divider)));
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void hideFloatingButton() {
        FloatingActionButton mFab = requireActivity().findViewById(R.id.fab_compose);
        mFab.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onConversationClicked(Conversation conversation) {
        ChatFragment chatFragment = ChatFragment.newInstance(Parcels.wrap(conversation));
        hideFloatingButton();
        hostActivity.addFragment(chatFragment);
        hostActivity.setTabsVisibility(View.GONE);
        hostActivity.setTitle("Chats");
    }
}