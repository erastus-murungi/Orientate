package com.erastus.orientate.student.profile.discoverpeople;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Notification;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.erastus.orientate.R;
import com.erastus.orientate.applications.App;
import com.erastus.orientate.databinding.DiscoverPeopleFragmentBinding;
import com.erastus.orientate.student.chat.ChatActivity;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.student.models.SimpleState;
import com.erastus.orientate.student.profile.editprofile.EditProfileFragment;
import com.erastus.orientate.utils.DateUtils;
import com.erastus.orientate.utils.ParentActivityImpl;
import com.erastus.orientate.utils.circularimageview.CircularImageView;
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

    private NotificationManagerCompat mNotificationManager;


    public static DiscoverPeopleFragment newInstance() {
        return new DiscoverPeopleFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DiscoverPeopleFragmentBinding.inflate(inflater, container, false);
        mHostActivity = (ParentActivityImpl) requireActivity();
        mNotificationManager = NotificationManagerCompat.from(requireContext());
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

        mViewModel.getConversationFound().observe(getViewLifecycleOwner(), conversationSimpleState -> {
            if (conversationSimpleState == null) {
                return;
            }
            if (conversationSimpleState.getData() != null) {
                Conversation conversation = conversationSimpleState.getData();
                Notification notification = new NotificationCompat.Builder(requireContext(), App.CHANNEL_ID_1)
                        .setSmallIcon(R.drawable.ic_icon)
                        .setContentTitle("We found a group for you! We hope you like them!")
                        .setContentText(conversation.getTitle())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .build();
                mNotificationManager.notify(2, notification);

                TextView undiscoverable = mBinding.textViewUndiscoverable;
                undiscoverable.setVisibility(View.GONE);
                View layout = mBinding.layoutConversationFound;
                layout.setVisibility(View.VISIBLE);
                mDiscoverableView.setVisibility(View.GONE);

                @NonNull final CircularImageView mConversationImageView;
                @NonNull final TextView mConversationTitleTextView;
                @NonNull final TextView mLastMessageTextView;
                @NonNull final TextView mLastSeenTextView;

                mConversationImageView = mBinding.imageViewConversationProfilePicture;
                mConversationTitleTextView = mBinding.textViewConversationTitle;
                mLastMessageTextView = mBinding.textViewConversationLastMessage;
                mLastSeenTextView = mBinding.textViewLastSeen;
                mBinding.getRoot().setOnClickListener((view) -> goToChatActivity());
                setUpImageView(mConversationImageView);

                Glide.with(requireContext())
                        .load(conversation.getProfileImageUrl())
                        .placeholder(R.drawable.ic_baseline_tag_faces_24)
                        .circleCrop()
                        .into(mConversationImageView);
                mConversationTitleTextView.setText(conversation.getTitle());
                mLastMessageTextView.setText(conversation.getLastMessageText());
                mLastSeenTextView.setText(DateUtils.formatDate(conversation.getUpdatedAt(), requireContext()));
            }
        });
    }

    private void goToChatActivity() {
        startActivity(new Intent(requireContext(), ChatActivity.class));
    }

    private void setUpImageView(CircularImageView circularImageView) {
        circularImageView.setBorderWidth(5);
        circularImageView.setBorderColor(R.color.colorPrimaryDark);
    }

    private void showErrorAndReloadSnackbar(String errorMessage) {
        Snackbar.make(mBinding.getRoot(), errorMessage, BaseTransientBottomBar.LENGTH_LONG)
                .setAction(R.string.reload, view -> mViewModel.reload())
                .setBackgroundTint(requireActivity().getColor(R.color.colorPrimaryDark))
                .show();
    }

}