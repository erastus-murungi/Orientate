package com.erastus.orientate.student.chat.chatinfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.erastus.orientate.R;
import com.erastus.orientate.models.ExtendedParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.UserViewHolder> {
    private List<ExtendedParseUser> mItems;

    protected Context mContext;

    public ParticipantsAdapter(Context context) {
        mContext = context;
        mItems = new ArrayList<>();
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
        holder.bindData(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void update(List<ExtendedParseUser> newData) {
        Collections.sort(newData, (o1, o2) -> Boolean.compare(o1.isMe(), o2.isMe()) * (-1));
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(newData, mItems));
        diffResult.dispatchUpdatesTo(this);
        mItems.clear();
        mItems.addAll(newData);
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mUsername;
        TextView mStatus;
        TextView mFullName;

        ExtendedParseUser mUser;

        UserViewHolder(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.user_avatar);
            mUsername = itemView.findViewById(R.id.text_view_username);
            mFullName = itemView.findViewById(R.id.text_view_profile_full_name);

//            itemView.findViewById(R.id.user_status);
        }

        void bindData(ExtendedParseUser user) {
            this.mUser = user;
            mUsername.setText(mContext.getString(R.string.format_username, this.mUser.getUsername()));
            mFullName.setText(user.getStudent().getFullName());
//            mStatus.setText(this.mUser.getDesignation());

            Glide.with(this.itemView)
                    .load(user.getStudent().getProfileImageUrl())
                    .placeholder(R.drawable.ic_baseline_tag_faces_24)
                    .apply(RequestOptions.circleCropTransform())
                    .into(mAvatar);
        }
    }

    static class DiffCallback extends DiffUtil.Callback {

        List<ExtendedParseUser> newUsers;
        List<ExtendedParseUser> oldUsers;

        public DiffCallback(List<ExtendedParseUser> newChats, List<ExtendedParseUser> oldChats) {
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
            return oldUsers.get(i).getObjectId().equals(newUsers.get(i1).getObjectId());
        }

        @Override
        public boolean areContentsTheSame(int i, int i1) {
            return areItemsTheSame(i, i1);
        }

    }
}
