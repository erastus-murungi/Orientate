package com.erastus.orientate.student.chat.chatmessages;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.erastus.orientate.R;
import com.erastus.orientate.models.ExtendedParseUser;
import com.erastus.orientate.student.chat.conversations.models.Conversation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final Conversation mConversation;

    private List<ExtendedParseUser> mUsers;

    public UserAdapter(Conversation conversation) {
        mConversation = conversation;
        mUsers = new ArrayList<>();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View receivedMessageView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(receivedMessageView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bindData(mUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void update(List<ExtendedParseUser> newData) {
        Collections.sort(newData, (o1, o2) -> Boolean.compare(o1.isMe(), o2.isMe()) * (-1));
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new UserDiffCallback(newData, mUsers));
        diffResult.dispatchUpdatesTo(this);
        mUsers.clear();
        mUsers.addAll(newData);
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        ImageView mAvatar;

        TextView mUsername;

        TextView mStatus;

        ExtendedParseUser mUser;

        UserViewHolder(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.user_avatar);
            mUsername = itemView.findViewById(R.id.user_username);
            mStatus = itemView.findViewById(R.id.user_status);
        }

        void bindData(ExtendedParseUser user) {
            this.mUser = user;

            mUsername.setText(this.mUser.getUsername());
            mStatus.setText(String.valueOf(this.mUser.isOnline()));

//            Glide.with(this.itemView)
//                    .load(user.getUser().getProfilePictureUrl())
//                    .apply(RequestOptions.circleCropTransform())
//                    .into(mAvatar);
        }
    }

    static class UserDiffCallback extends DiffUtil.Callback {

        List<ExtendedParseUser> newUsers;
        List<ExtendedParseUser> oldUsers;

        public UserDiffCallback(List<ExtendedParseUser> newChats, List<ExtendedParseUser> oldChats) {
            this.newUsers = newChats;
            this.oldUsers = oldChats;
        }

        @Override
        public int getOldListSize() {
            return oldUsers.size();
        }

        @Override
        public int getNewListSize() {
            return newUsers.size();
        }

        @Override
        public boolean areItemsTheSame(int i, int i1) {
            return oldUsers.get(i).equals(newUsers.get(i1));
        }

        @Override
        public boolean areContentsTheSame(int i, int i1) {
            return areItemsTheSame(i, i1);
        }

    }
}