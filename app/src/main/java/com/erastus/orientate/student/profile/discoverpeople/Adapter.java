package com.erastus.orientate.student.profile.discoverpeople;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.erastus.orientate.R;
import com.erastus.orientate.databinding.ItemSystemGeneratedConversationBinding;
import com.erastus.orientate.student.chat.conversations.ConversationAdapter;
import com.erastus.orientate.student.chat.conversations.models.Conversation;
import com.erastus.orientate.utils.DateUtils;
import com.erastus.orientate.utils.circularimageview.CircularImageView;

import static com.erastus.orientate.student.chat.conversations.ConversationAdapter.ConversationsDiffCallBack;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ConversationViewHolder> {
    @NonNull
    private final List<Conversation> mConversations;
    @NonNull
    private Context mContext;
    private ConversationAdapter.OnConversationClicked mOnConversationClicked;

    public interface OnConversationClicked {
        void onConversationClicked(Conversation conversation);
    }

    public Adapter(@NotNull Context context,
                   @NotNull List<Conversation> conversations,
                   ConversationAdapter.OnConversationClicked onConversationClicked) {
        mContext = context;
        mConversations = conversations;
        mOnConversationClicked = onConversationClicked;
    }

    public void update(List<Conversation> newData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new ConversationsDiffCallBack(newData, mConversations)
        );
        diffResult.dispatchUpdatesTo(this);
        mConversations.clear();
        mConversations.addAll(newData);
    }


    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new ConversationViewHolder(
                ItemSystemGeneratedConversationBinding.inflate(layoutInflater, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        holder.bind(mConversations.get(position));
    }

    @Override
    public int getItemCount() {
        return mConversations.size();
    }

    public class ConversationViewHolder extends RecyclerView.ViewHolder {
        @NonNull private final CircularImageView mConversationImageView;
        @NonNull private final TextView mConversationTitleTextView;
        @NonNull private final TextView mLastMessageTextView;
        @NonNull private final TextView mLastSeenTextView;
        @NonNull private final ImageView mPinnedImageView;

        public ConversationViewHolder(@NonNull ItemSystemGeneratedConversationBinding binding) {
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

        public void bind(Conversation conversation) {
            Glide.with(mContext)
                    .load(conversation.getProfileImageUrl())
                    .placeholder(R.drawable.ic_baseline_tag_faces_24)
                    .circleCrop()
                    .into(mConversationImageView);
            mConversationTitleTextView.setText(conversation.getTitle());
            mLastMessageTextView.setText(conversation.getLastMessageText());
            mLastSeenTextView.setText(DateUtils.formatDate(conversation.getUpdatedAt(), mContext));
        }
    }
}
