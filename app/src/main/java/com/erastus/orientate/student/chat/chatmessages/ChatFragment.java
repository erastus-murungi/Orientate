package com.erastus.orientate.student.chat.chatmessages;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.erastus.orientate.R;
import com.erastus.orientate.databinding.FragmentChatBinding;
import com.erastus.orientate.student.chat.chatmessages.models.ChatMessage;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.models.SimpleState;
import com.erastus.orientate.utils.EmptyView;
import com.erastus.orientate.utils.EndlessRecyclerViewScrollListener;
import com.erastus.orientate.utils.MessageComposer;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatFragment extends ParentFragment implements MessageComposer.Listener {

    private static final String ARGS_CONVERSATION = "ARGS_CONVERSATION";

    private ChatViewModel mViewModel;

    private CoordinatorLayout mCoordinatorLayout;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mChatsRecyclerView;

    private MessageComposer mMessageComposer;

    private LinearLayoutManager mLinearLayoutManager;

    private EmptyView mEmptyView;

    private ChatAdapter mChatAdapter;

    private RecyclerView.OnScrollListener mOnScrollListener;

    public static ChatFragment newInstance(Parcelable conversation) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_CONVERSATION, conversation);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpObservers();
    }

    private void setUpObservers() {
        mEmptyView.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setRefreshing(false);

        mViewModel.getState().observe(getViewLifecycleOwner(), listSimpleState -> {
            if (listSimpleState == null || listSimpleState.isLoading()) {
                return;
            }
            if (listSimpleState.getErrorMessage() != null) {
                // display the error to the user and give the option to reload
                showErrorSnackBar(listSimpleState.getErrorMessage());
            }
            if (listSimpleState.getErrorCode() != null) {
                showErrorSnackBar(listSimpleState.getErrorCode());
            } else {
                if (listSimpleState.getInternalErrorOccurred() != null) {
                    showInformationSnackBar("Internal error occurred");
                } else {
                    // work with data
                    workWithData(listSimpleState.getData());
                }
            }
        });

        mViewModel.getNewMessages().observe(getViewLifecycleOwner(), messages -> {
            if (messages != null && messages.size() > 0) {
                if (mEmptyView.getVisibility() == View.VISIBLE) {
                    mEmptyView.setVisibility(View.GONE);
                }
                showInformationSnackBar("New message");
                mChatAdapter.update(messages);
                scrollChatToBottom();
            }
        });
    }

    private void workWithData(List<ChatMessage> data) {
        if (data == null || data.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mChatsRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mChatAdapter.update(data);
        }
    }

    @Override
    public int provideLayoutResourceId() {
        return R.layout.fragment_chat_thread;
    }

    @Override
    public void performBindings(View rootView) {
        mCoordinatorLayout = rootView.findViewById(R.id.coordinator);
        mSwipeRefreshLayout = rootView.findViewById(R.id.chat_swipe);
        mChatsRecyclerView = rootView.findViewById(R.id.chat_recycler_view);
        mMessageComposer = rootView.findViewById(R.id.chats_message_composer);
        mEmptyView = rootView.findViewById(R.id.chat_empty_view);
    }

    @Override
    public void setViewBehaviour(boolean viewFromCache) {
        if (!viewFromCache) {
            setHasOptionsMenu(true);
            prepareRecyclerView();
            initializeScrollListener(mLinearLayoutManager);
            mSwipeRefreshLayout.setRefreshing(true);
            mViewModel.subscribe();
        }
    }

    private void prepareRecyclerView() {

        mSwipeRefreshLayout.setColorSchemeColors(requireContext().getColor(R.color.colorPrimary));
        mSwipeRefreshLayout.setOnRefreshListener(mViewModel::fetchHistory);
        mLinearLayoutManager = new LinearLayoutManager(fragmentContext);
        mLinearLayoutManager.setReverseLayout(false);
        mLinearLayoutManager.setStackFromEnd(true);
        mChatsRecyclerView.setLayoutManager(mLinearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(fragmentContext, RecyclerView.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(requireContext().getDrawable(R.drawable.chats_divider)));
        mChatsRecyclerView.addItemDecoration(dividerItemDecoration);

        mChatsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mChatAdapter = new ChatAdapter(requireContext());
        mChatsRecyclerView.setAdapter(mChatAdapter);

        mMessageComposer.setListener(this);

        // tag::HIS-5.2[]

        // end::HIS-5.2[]
    }

    // tag::HIS-5.1[]
    private void initializeScrollListener(LinearLayoutManager linearLayoutManager) {
        mOnScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mViewModel.loadMoreChats();
            }
        };
        mChatsRecyclerView.addOnScrollListener(mOnScrollListener);
    }
    // end::HIS-5.1[]

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_chat_info, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_chat_info) {
//            hostActivity.addFragment(ChatInfoFragment.newInstance(mChannel));
//            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onReady() {
    }

    @Override
    public void extractArguments() {
        Conversation conversation = Parcels.unwrap(requireArguments().getParcelable(ARGS_CONVERSATION));
        mViewModel = new ViewModelProvider(this,
                new ChatViewModelFactory(conversation)).get(ChatViewModel.class);
    }


    @Override
    public void onDestroy() {
        mViewModel.removeListener();
        super.onDestroy();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel.initListener();
    }

    @Override
    public void onSentClick(String message) {

        mViewModel.sendMessage(message);
        if (TextUtils.isEmpty(message)) {
            return;
        }
        String finalMessage = message;

        mViewModel.sendMessage(message);

//        hostActivity.getPubNub()
//                .publish()
//                .channel(mChannel)
//                .shouldStore(true)
//                .message(Message.newBuilder().text(message).build())
//                .async(new PNCallback<PNPublishResult>() {
//                    @Override
//                    public void onResponse(PNPublishResult result, PNStatus status) {
//                        if (!status.isError()) {
//                            long newMessageTimetoken = result.getTimetoken();
//                        } else {
//                            Message msg = Message.createUnsentMessage(Message.newBuilder().text(finalMessage).build());
//                            mMessages.add(msg);
//                            History.chainMessages(mMessages, mMessages.size());
//                            runOnUiThread(() -> {
//                                if (mEmptyView.getVisibility() == View.VISIBLE) {
//                                    mEmptyView.setVisibility(View.GONE);
//                                }
//                                mChatAdapter.update(mMessages);
//                                scrollChatToBottom();
//
//                                Toast.makeText(fragmentContext, R.string.message_not_sent, Toast.LENGTH_SHORT).show();
//
//                            });
//                        }
//                    }
//                });
    }
    // end::SEND-2[]

    // tag::MSG-3[]
    private void scrollChatToBottom() {
        mChatsRecyclerView.scrollToPosition(mChatAdapter.getItemCount() - 1);
    }
    // end::MSG-3[]

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
