package com.erastus.orientate.student.chat.chatmessages;

import android.app.Notification;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.erastus.orientate.R;
import com.erastus.orientate.applications.App;
import com.erastus.orientate.student.chat.participants.ParticipantsFragment;
import com.erastus.orientate.student.chat.chatmessages.models.Message;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.utils.EmptyView;
import com.erastus.orientate.utils.EndlessRecyclerViewScrollListener;
import com.erastus.orientate.utils.MessageComposer;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;
import java.util.Objects;

public class ChatFragment extends ParentFragment {
    private static final String ARGS_CONVERSATION = "ARGS_CONVERSATION";
    private static final String TAG = "ChatFragment";

    private ChatViewModel mViewModel;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mChatsRecyclerView;

    private MessageComposer mMessageComposer;

    private LinearLayoutManager mLinearLayoutManager;

    private EmptyView mEmptyView;

    private ChatAdapter mChatAdapter;

    private RecyclerView.OnScrollListener mOnScrollListener;

    private NotificationManagerCompat mNotificationManager;

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

    private void setSwipeRefreshLayoutBehavior() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.darkBlue, R.color.white);
        mSwipeRefreshLayout.setOnRefreshListener(mViewModel::fetchHistory);
        mSwipeRefreshLayout.setRefreshing(false);
    }



    private void setUpObservers() {

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
                    mSwipeRefreshLayout.setRefreshing(false);
                    workWithData(listSimpleState.getData());
                }
            }
        });

        mViewModel.getNewMessages().observe(getViewLifecycleOwner(), messages -> {
            if (messages != null && messages.size() > 0) {
                if (mEmptyView.getVisibility() == View.VISIBLE) {
                    mEmptyView.setVisibility(View.GONE);
                }
                mChatAdapter.update(messages);
            }
        });


        mViewModel.getNewMessageArrived().observe(getViewLifecycleOwner(), message -> {
            if (message.getConversationId().equals(mViewModel.getMyConversationId())) {
                scrollChatToBottom();
            } else {
                addToNotification(message.getContent(), mViewModel.getConversationTitle());
            }
        });
    }

    private void addToNotification(String newMessage, String conversationTitle) {
        Notification notification = new NotificationCompat.Builder(requireContext(), App.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_icon)
                .setContentTitle(conversationTitle)
                .setContentText(newMessage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        mNotificationManager.notify(1, notification);
    }


    private void workWithData(List<Message> data) {
        if (data == null || data.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mChatsRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mChatAdapter.update(data);
            Log.d(TAG, "workWithData: updated from " + mViewModel.getConversationTitle());
        }
    }

    @Override
    public int provideLayoutResourceId() {
        return R.layout.fragment_chat_thread;
    }

    @Override
    public void performBindings(View rootView) {
        mSwipeRefreshLayout = rootView.findViewById(R.id.chat_swipe);
        mChatsRecyclerView = rootView.findViewById(R.id.chat_recycler_view);
        mMessageComposer = rootView.findViewById(R.id.chats_message_composer);
        mEmptyView = rootView.findViewById(R.id.chat_empty_view);

        mNotificationManager = NotificationManagerCompat.from(requireContext());
    }

    @Override
    public void setViewBehaviour(boolean viewFromCache) {
        if (!viewFromCache) {
            setHasOptionsMenu(true);
            prepareRecyclerView();
            setSwipeRefreshLayoutBehavior();
            setMessageComposerBehavior();
            initializeScrollListener(mLinearLayoutManager);
            mSwipeRefreshLayout.setRefreshing(true);
            mViewModel.subscribe();
        }
    }

    private void prepareRecyclerView() {

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

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_chat_info, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_chat_info) {
            hostActivity.addFragment(ParticipantsFragment.newInstance(mViewModel.getConversation()));
            hostActivity.setTitle(mViewModel.getConversationTitle());
            hostActivity.setTabsVisibility(View.GONE);
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
        setUpToolBarBehavior();
    }

    private void setUpToolBarBehavior() {
        hostActivity.setTitle(mViewModel.getConversationTitle());
        hostActivity.setTabsVisibility(View.GONE);
    }

    private void setMessageComposerBehavior() {
        mMessageComposer.setListener(message -> mViewModel.sendMessage(message));
    }

    private void scrollChatToBottom() {
        mChatsRecyclerView.scrollToPosition(mChatAdapter.getItemCount());
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
