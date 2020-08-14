package com.erastus.orientate.student.chat.connections;

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
import com.erastus.orientate.applications.App;
import com.erastus.orientate.models.ExtendedParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConnectionsAdapter extends RecyclerView.Adapter<ConnectionsAdapter.ConnectionViewHolder> {
    private List<ExtendedParseUser> mConnectionList;
    private Context mContext;
    private OnConnectionClickedListener mOnConnectionClickedListener;

    public ConnectionsAdapter(Context context, OnConnectionClickedListener listener) {
        mContext = context;
        this.mConnectionList = new ArrayList<>();
        mOnConnectionClickedListener = listener;
    }

    public interface OnConnectionClickedListener {
        void onConnectionClicked(ExtendedParseUser user);
    }

    @NonNull
    @Override
    public ConnectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new ConnectionViewHolder(view);
    }

    public void update(List<ExtendedParseUser> newData) {
        Collections.sort(newData, (o1, o2) -> Boolean.compare(o1.isMe(), o2.isMe()) * (-1));
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(newData, mConnectionList));
        diffResult.dispatchUpdatesTo(this);
        mConnectionList.clear();
        mConnectionList.addAll(newData);
    }

    @Override
    public void onBindViewHolder(@NonNull ConnectionViewHolder holder, int position) {
        holder.bindData(mConnectionList.get(position));
    }

    @Override
    public int getItemCount() {
        return mConnectionList == null ? 0 : mConnectionList.size();
    }

    public class ConnectionViewHolder extends RecyclerView.ViewHolder {
        private ExtendedParseUser mUser;
        ImageView mAvatar;
        TextView mUsername;
        TextView mFullName;
        View mRootView;

        public ConnectionViewHolder(@NonNull View itemView) {
            super(itemView);
            mRootView = itemView;
            mAvatar = itemView.findViewById(R.id.user_avatar);
            mUsername = itemView.findViewById(R.id.text_view_username);
            mFullName = itemView.findViewById(R.id.text_view_profile_full_name);
        }

        public void bindData(ExtendedParseUser extendedParseUser) {
            this.mUser = extendedParseUser;
            mUsername.setText(mContext.getString(R.string.format_username, this.mUser.getUsername()));
            mFullName.setText(extendedParseUser.getObjectId().equals(App.get().getCurrentUser().getObjectId()) ?
                    mContext.getString(R.string.you) : extendedParseUser.getStudent().getFullName());
//            mStatus.setText(this.mUser.getDesignation());

            Glide.with(this.itemView)
                    .load(extendedParseUser.getStudent().getProfileImageUrl())
                    .placeholder(R.drawable.ic_baseline_tag_faces_24)
                    .apply(RequestOptions.circleCropTransform())
                    .into(mAvatar);
            mRootView.setOnClickListener(view ->
                    mOnConnectionClickedListener.onConnectionClicked(
                            mConnectionList.get(getBindingAdapterPosition())));
        }
    }

    private static class DiffCallback extends DiffUtil.Callback {

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
