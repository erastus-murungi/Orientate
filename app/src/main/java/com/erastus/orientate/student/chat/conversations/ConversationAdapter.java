package com.erastus.orientate.student.chat.conversations;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.erastus.orientate.R;
import com.erastus.orientate.databinding.ItemConversationBinding;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.utils.circularimageview.CircularImageView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Conversation}.
 */
public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    private final List<Conversation> mConversations;
    private Context mContext;
    private OnConversationClicked mOnConversationClicked;

    public interface OnConversationClicked {
        void onConversationClicked(Conversation conversation);
    }

    public ConversationAdapter(Context context, List<Conversation> conversations, OnConversationClicked onConversationClicked) {
        mContext = context;
        mConversations = conversations;
        mOnConversationClicked = onConversationClicked;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(ItemConversationBinding.inflate(layoutInflater, parent, false));
    }

    public void update(List<Conversation> newData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ConversationsDiffCallBack(newData, mConversations));
        diffResult.dispatchUpdatesTo(this);
        mConversations.clear();
        mConversations.addAll(newData);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bind(mConversations.get(position));
    }

    @Override
    public int getItemCount() {
        return mConversations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CircularImageView mConversationImageView;
        private final TextView mConversationTitleTextView;
        private final TextView mLastMessageTextView;
        private final TextView mLastSeenTextView;
        private final ImageView mPinnedImageView;


        public ViewHolder(ItemConversationBinding binding) {
            super(binding.getRoot());
            mConversationImageView = binding.imageViewConversationProfilePicture;
            mConversationTitleTextView = binding.textViewConversationTitle;
            mLastMessageTextView = binding.textViewConversationLastMessage;
            mLastSeenTextView = binding.textViewLastSeen;
            mPinnedImageView = binding.pinned;
            binding.getRoot().setOnClickListener((view) ->
                    mOnConversationClicked.onConversationClicked(
                            mConversations.get(getBindingAdapterPosition())));
            setUpImageView(mConversationImageView);
        }

        private void setUpImageView(CircularImageView conversationImageView) {
            conversationImageView.setBorderWidth(5);
            conversationImageView.setBorderColor(R.color.colorPrimaryDark);
        }

        private void bind(Conversation conversation) {
            Glide.with(mContext)
                    .load(conversation.getProfileImageUrl())
                    .placeholder(R.drawable.ic_baseline_tag_faces_24)
                    .circleCrop()
                    .into(mConversationImageView);
            mConversationTitleTextView.setText(conversation.getTitle());
            mLastMessageTextView.setText(conversation.getLastMessageText());
            mLastSeenTextView.setText(conversation.getLastSeen());
        }
    }

    static class ConversationsDiffCallBack extends DiffUtil.Callback {

        List<Conversation> newConversations;
        List<Conversation> oldConversations;

        ConversationsDiffCallBack(List<Conversation> newConversations, List<Conversation> oldConversations) {
            this.newConversations = newConversations;
            this.oldConversations = oldConversations;
        }

        @Override
        public int getOldListSize() {
            return oldConversations.size();
        }

        @Override
        public int getNewListSize() {
            return newConversations.size();
        }

        @Override
        public boolean areItemsTheSame(int i, int i1) {
            return oldConversations.get(i).getObjectId().equals(newConversations.get(i1).getObjectId());
        }

        @Override
        public boolean areContentsTheSame(int i, int i1) {
            boolean titleSame = oldConversations.get(i).getTitle().equals(newConversations.get(i1).getTitle());
            boolean upDate = oldConversations.get(i).getUpdatedAt().equals(newConversations.get(i1).getUpdatedAt());
            return titleSame && upDate;
        }
    }
}