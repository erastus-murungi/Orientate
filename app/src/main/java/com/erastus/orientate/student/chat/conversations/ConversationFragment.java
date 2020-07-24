package com.erastus.orientate.student.chat.conversations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erastus.orientate.R;
import com.erastus.orientate.databinding.FragmentConversationBinding;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.utils.EmptyView;
import com.erastus.orientate.utils.customindicators.AVLoadingIndicatorView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class ConversationFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

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
    public static ConversationFragment newInstance(int columnCount) {
        ConversationFragment fragment = new ConversationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
        mAdapter = new ConversationAdapter(getContext(), new ArrayList<>());
        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), mColumnCount));
        }
        mRecyclerView.setAdapter(mAdapter);

    }

}