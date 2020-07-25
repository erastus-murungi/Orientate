package com.erastus.orientate.student.chat.chatmessages;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.erastus.orientate.R;
import com.erastus.orientate.student.chat.chatmessages.models.ChatMessage;
import com.erastus.orientate.student.chat.chatmessages.models.MessageType;
import com.erastus.orientate.student.login.StudentLoginRepository;
import com.erastus.orientate.utils.DateUtils;
import com.erastus.orientate.utils.Utils;
import com.erastus.orientate.utils.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
    private List<ChatMessage> mItems;

    private Context mContext;

    public ChatAdapter(Context context) {
        mItems = new ArrayList<>();
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getMessageType();
    }

    // tag::BIND-1[]
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case MessageType.OWN_HEADER_FULL:
            case MessageType.OWN_HEADER_SERIES:
            case MessageType.OWN_MIDDLE:
            case MessageType.OWN_END:
                View sentMessageView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_sent, parent, false);
                return new MessageViewHolder(sentMessageView, viewType);
            case MessageType.REC_HEADER_FULL:
            case MessageType.REC_HEADER_SERIES:
            case MessageType.REC_MIDDLE:
            case MessageType.REC_END:
                View receivedMessageView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_received, parent, false);
                return new MessageViewHolder(receivedMessageView, viewType);
        }
        throw new IllegalStateException("No applicable view type found.");
    }
    // end::BIND-1[]

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bindData(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getCreatedAt().getTime();
    }

    // tag::BIND-4[]
    public void update(List<ChatMessage> newData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(newData, mItems));
        diffResult.dispatchUpdatesTo(this);
        mItems.clear();
        mItems.addAll(newData);
    }
    // end::BIND-4[]

    class DateViewHolder extends RecyclerView.ViewHolder {

        TextView mDateTextView;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            mDateTextView = itemView.findViewById(R.id.item_date);
        }

        void bindData(Long key) {
            mDateTextView.setText(DateUtils.parseDateTime(key));
        }
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        private int mType;

        RelativeLayout mRoot;

        CircularImageView mAvatar;

        TextView mSender;

        TextView mBubble;

        TextView mTimestamp;

        ChatMessage mMessage;

        MessageViewHolder(View itemView, int type) {
            super(itemView);

            mType = type;

            mRoot = itemView.findViewById(R.id.root);

            mBubble = itemView.findViewById(R.id.message_bubble);

            mTimestamp = itemView.findViewById(R.id.message_timestamp);

            mAvatar = itemView.findViewById(R.id.message_avatar);

            mSender = itemView.findViewById(R.id.message_sender);

        }

        void bindData(ChatMessage message) {
            this.mMessage = message;

            handleType();

            mBubble.setText(mMessage.getContent());

            mSender.setText(mMessage.getSender().getFirstName());

            mTimestamp.setText(DateUtils.parseTime(mMessage.getCreatedAt().getTime(), mContext));

            if (this.mMessage.isSent()) {
                mBubble.setAlpha(1.0f);
            } else {
                mBubble.setAlpha(0.5f);
            }

            Glide.with(mContext)
                    .load(message.getSender().getProfileImageUrl())
                    .into(mAvatar);

        }

        private void handleType() {
            switch (mType) {
                case MessageType.OWN_HEADER_FULL:
                case MessageType.REC_HEADER_FULL:
                    mAvatar.setVisibility(View.VISIBLE);
                    mSender.setVisibility(View.VISIBLE);
                    mTimestamp.setVisibility(View.VISIBLE);
                    break;
                case MessageType.OWN_HEADER_SERIES:
                case MessageType.REC_HEADER_SERIES:
                    mAvatar.setVisibility(View.VISIBLE);
                    mSender.setVisibility(View.VISIBLE);
                    mTimestamp.setVisibility(View.GONE);
                    break;
                case MessageType.OWN_MIDDLE:
                case MessageType.REC_MIDDLE:
                    mAvatar.setVisibility(View.INVISIBLE);
                    mSender.setVisibility(View.GONE);
                    mTimestamp.setVisibility(View.GONE);
                    break;
                case MessageType.OWN_END:
                case MessageType.REC_END:
                    mAvatar.setVisibility(View.INVISIBLE);
                    mSender.setVisibility(View.GONE);
                    mTimestamp.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    class DiffCallback extends DiffUtil.Callback {

        List<ChatMessage> newMessages;
        List<ChatMessage> oldMessages;

        DiffCallback(List<ChatMessage> newMessages, List<ChatMessage> oldMessages) {
            this.newMessages = newMessages;
            this.oldMessages = oldMessages;
        }

        @Override
        public int getOldListSize() {
            return oldMessages.size();
        }

        @Override
        public int getNewListSize() {
            return newMessages.size();
        }

        @Override
        public boolean areItemsTheSame(int i, int i1) {
            return oldMessages.get(i).getObjectId().equals(newMessages.get(i1).getObjectId());
        }

        @Override
        public boolean areContentsTheSame(int i, int i1) {
            boolean type = oldMessages.get(i).getMessageType() == newMessages.get(i1).getMessageType();
            boolean sent = oldMessages.get(i).getContent().equals(newMessages.get(i1).getContent());
            return type && sent;
        }
    }

    private void showMessageInfoDialog(Context context, ChatMessage ChatMessage) {

        String contentBuilder = "" + Utils.emphasizeText("Sender: ") +
                ChatMessage.getSenderId() +
                Utils.newLine() +
                Utils.emphasizeText("Date time: ") +
                DateUtils.parseDateTime(ChatMessage.getCreatedAt().getTime() / 10_000L) +
                Utils.newLine() +
                Utils.emphasizeText("Relative: ") +
                DateUtils.getRelativeTimeAgo(ChatMessage.getCreatedAt().getTime() / 10_000L) +
                Utils.newLine() +
                Utils.emphasizeText("Own ChatMessage: ") +
                ChatMessage.getSender().getObjectId().equals(StudentLoginRepository.getInstance().getLoggedInStudent().getObjectId()) +
                Utils.newLine() +
                Utils.emphasizeText("Type: ") +
                ChatMessage.getMessageType() +
                Utils.newLine() +
                Utils.emphasizeText("Is sent: ") +
                ChatMessage.isSent();
        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title(R.string.message_info)
                .content(Html.fromHtml(contentBuilder, Html.FROM_HTML_OPTION_USE_CSS_COLORS))
                .positiveText(android.R.string.ok)
                .build();
        materialDialog.show();
    }

}
