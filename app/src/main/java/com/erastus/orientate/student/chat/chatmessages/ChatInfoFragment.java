package com.erastus.orientate.student.chat.chatmessages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.erastus.orientate.R;
import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.student.chat.chatinfo.ChatInfoViewModel;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.utils.EmptyView;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ChatInfoFragment extends ParentFragment {
    private static final String ARGS_CONVERSATION = "ARGS_CONVERSATION";

    ImageView mImage;

    TextView mDescription;

    RecyclerView mUsersRecyclerView;
    ChatInfoViewModel mViewModel;

    EmptyView mEmptyView;

    UserAdapter mUserAdapter;
    List<ExtendedParseUser> mUsers = new ArrayList<>();

    private Conversation mConversation;


    static ChatInfoFragment newInstance(String channel) {
        Bundle args = new Bundle();
        args.putString(ARGS_CONVERSATION, channel);
        ChatInfoFragment fragment = new ChatInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ChatInfoViewModel.class);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public int provideLayoutResourceId() {
        return R.layout.fragment_chat_info;
    }

    @Override
    public void performBindings(View rootView) {
        mImage = rootView.findViewById(R.id.info_image);
        mDescription = rootView.findViewById(R.id.info_description);
        mUsersRecyclerView = rootView.findViewById(R.id.info_recycler_view);
        mEmptyView = rootView.findViewById(R.id.info_empty_view);
    }

    @Override
    public void setViewBehaviour(boolean viewFromCache) {
        mUsersRecyclerView.setLayoutManager(new LinearLayoutManager(fragmentContext));
        mUsersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mUserAdapter = new UserAdapter(mConversation);
        mUsersRecyclerView.setAdapter(mUserAdapter);

        Glide.with(fragmentContext)
                .load(R.drawable.ic_icon)
                .apply(RequestOptions.circleCropTransform())
                .into(mImage);

        mViewModel.fetchAvailableUsers();
    }

    @Override
    public void onReady() {
        mViewModel.initListener();
    }

    private void handleUiVisibility() {
        int viewState = -1;

        if (mUsers.size() > 0) {
            if (mEmptyView.getVisibility() != View.GONE)
                viewState = View.GONE;
        } else {
            if (mEmptyView.getVisibility() != View.VISIBLE)
                viewState = View.VISIBLE;
        }

        if (viewState != -1) {
            int finalViewState = viewState;
            mEmptyView.setVisibility(finalViewState);
        }
    }

    @Override
    public void extractArguments() {
        super.extractArguments();
        assert getArguments() != null;
        mConversation = Parcels.unwrap(getArguments().getParcelable(ARGS_CONVERSATION));
    }

    // tag::ONL-1[
    // end::ONL-1[]
}
