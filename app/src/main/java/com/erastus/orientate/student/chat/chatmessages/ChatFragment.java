package com.erastus.orientate.student.chat.chatmessages;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.erastus.orientate.R;
import com.erastus.orientate.databinding.FragmentChatBinding;
import com.erastus.orientate.student.chat.chatmessages.models.ChatMessage;
import com.erastus.orientate.utils.EmptyView;
import com.erastus.orientate.utils.EndlessRecyclerViewScrollListener;
import com.erastus.orientate.utils.MessageComposer;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatFragment extends ParentFragment implements MessageComposer.Listener {

    private static final String ARGS_CHANNEL = "ARGS_CHANNEL";

    private ChatViewModel mViewModel;

    private FragmentChatBinding mBinding;

    private CoordinatorLayout mCoordinatorLayout;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mChatsRecyclerView;

    private MessageComposer mMessageComposer;

    private LinearLayoutManager mLinearLayoutManager;
    private EmptyView mEmptyView;

    // tag::HIS-4.2[]
    private ChatAdapter mChatAdapter;
    private List<ChatMessage> mMessages = new ArrayList<>();
    // end::HIS-4.2[]

    private String mChannel;

    private RecyclerView.OnScrollListener mOnScrollListener;

    public static ChatFragment newInstance(String channel) {
        Bundle args = new Bundle();
        args.putString(ARGS_CHANNEL, channel);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
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
            initializeScrollListener(mLinearLayoutManager);
            prepareRecyclerView();
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

        mChatAdapter = new ChatAdapter();
        mChatsRecyclerView.setAdapter(mChatAdapter);

        mMessageComposer.setListener(this);

        // tag::HIS-5.2[]

        mChatsRecyclerView.addOnScrollListener(mOnScrollListener);
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
    public String setScreenTitle() {
//        hostActivity.enableBackButton(false);
//        scrollChatToBottom();
//        loadCurrentOccupancy();
//        return mChannel;
        return "";
    }

    @Override
    public void extractArguments() {
        super.extractArguments();
        mChannel = Parcels.unwrap(getArguments().getParcelable(ARGS_CHANNEL));
    }

    @Override
    public void onReady() {
        mViewModel.initListener();

        // tag::FRG-2[]
        // tag::ignore[]
        /*
        // end::ignore[]
        hostActivity.getPubNub();
        // tag::ignore[]
        */
        // end::ignore[]
        // end::FRG-2[]
    }

    // tag::SUB-2[]
//    private void initListener() {
//        mPubNubListener = new SubscribeCallback() {
//            @Override
//            public void status(PubNub pubnub, PNStatus status) {
//                if (status.getOperation() == PNOperationType.PNSubscribeOperation && status.getAffectedChannels()
//                        .contains(mChannel)) {
//                    mSwipeRefreshLayout.setRefreshing(false);
//                    fetchHistory();
//                }
//            }
//
//            @Override
//            public void message(PubNub pubnub, PNMessageResult message) {
//                handleNewMessage(message);
//            }
//
//            @Override
//            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
//                if (presence.getChannel().equals(mChannel)) {
//                    int members = presence.getOccupancy();
//                    runOnUiThread(() -> hostActivity.setSubtitle(fragmentContext.getResources()
//                            .getQuantityString(R.plurals.members_online, members, members)));
//                }
//            }
//        };
//    }
    // end::SUB-2[]

//    private void loadCurrentOccupancy() {
//        hostActivity.getPubNub()
//                .hereNow()
//                .channels(Arrays.asList(mChannel))
//                .async(new PNCallback<PNHereNowResult>() {
//                    @Override
//                    public void onResponse(PNHereNowResult result, PNStatus status) {
//                        if (!status.isError()) {
//                            int members = result.getTotalOccupancy();
//                            hostActivity.setSubtitle(fragmentContext.getResources()
//                                    .getQuantityString(R.plurals.members_online, members, members));
//                        }
//                    }
//                });
//    }

    // tag::MSG-1[]
//    private void handleNewMessage(PNMessageResult message) {
//        if (message.getChannel().equals(mChannel)) {
//            Message msg = Message.serialize(message);
//            mMessages.add(msg);
//            History.chainMessages(mMessages, mMessages.size());
//            runOnUiThread(() -> {
//                if (mEmptyView.getVisibility() == View.VISIBLE) {
//                    mEmptyView.setVisibility(View.GONE);
//                }
//                mChatAdapter.update(mMessages);
//                scrollChatToBottom();
//            });
//        }
//    }
    // end::MSG-1[]

    // tag::SUB-1[]
//    private void subscribe() {
//        hostActivity.getPubNub()
//                .subscribe()
//                .channels(Collections.singletonList(mChannel))
//                .withPresence()
//                .execute();
//    }
    // end::SUB-1[]

    // tag::HIS-1[]
//    private void fetchHistory() {
//        if (History.isLoading()) {
//            return;
//        }
//        History.setLoading(true);
//        mSwipeRefreshLayout.setRefreshing(true);
//        History.getAllMessages(hostActivity.getPubNub(), mChannel, getEarliestTimestamp(),
//                new History.CallbackSkeleton() {
//                    @Override
//                    public void handleResponse(List<Message> newMessages) {
//                        if (!newMessages.isEmpty()) {
//                            mMessages.addAll(0, newMessages);
//                            History.chainMessages(mMessages, mMessages.size());
//                            runOnUiThread(() -> mChatAdapter.update(mMessages));
//                        } else if (mMessages.isEmpty()) {
//                            runOnUiThread(() -> mEmptyView.setVisibility(View.VISIBLE));
//                        } else {
//                            runOnUiThread(() -> Toast.makeText(fragmentContext, getString(R.string.no_more_messages),
//                                    Toast.LENGTH_SHORT).show());
//                        }
//                        runOnUiThread(() -> {
//                            mSwipeRefreshLayout.setRefreshing(false);
//                            Log.d("new_arrival", "size: " + mMessages.size());
//                        });
//                        History.setLoading(false);
//                    }
//                });
//    }
    // end::HIS-1[]

    private Long getEarliestTimestamp() {
        if (mMessages != null && !mMessages.isEmpty()) {
            return mMessages.get(0).getCreatedAt().getTime();
        }
        return null;
    }

    @Override
    public void onDestroy() {
        mViewModel.removeListener();
        super.onDestroy();
    }

    // tag::SEND-2[]
    @Override
    public void onSentClick(String message) {
        // tag::ignore[]
        if (TextUtils.isEmpty(message)) {
            return;
        }
        // end::ignore[]
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
        mChatsRecyclerView.scrollToPosition(mMessages.size() - 1);
    }
    // end::MSG-3[]
}
