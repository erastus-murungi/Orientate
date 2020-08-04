package com.erastus.orientate.student.chat.chatmessages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.erastus.orientate.R;
import com.erastus.orientate.applications.App;
import com.erastus.orientate.student.chat.chatmessages.models.Message;
import com.erastus.orientate.student.chat.chatmessages.models.MessageType;
import com.erastus.orientate.utils.DateUtils;
import com.erastus.orientate.utils.Utils;
import com.erastus.orientate.utils.circularimageview.CircularImageView;
import com.erastus.orientate.utils.reaction.ReactionPopup;
import com.erastus.orientate.utils.reaction.ReactionsConfig;
import com.erastus.orientate.utils.reaction.ReactionsConfigBuilder;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
    private static final String TAG = "ChatAdapter";
    private List<Message> mMessages;

    private Context mContext;

    public ChatAdapter(Context context) {
        mMessages = new ArrayList<>();
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        return mMessages.get(position).getMessageType();
    }

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

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bindData(mMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public long getItemId(int position) {
        return mMessages.get(position).getCreatedAt().getTime();
    }

    public void update(List<Message> newData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(newData, mMessages));
        diffResult.dispatchUpdatesTo(this);
        mMessages.clear();
        mMessages.addAll(newData);
    }

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

        View mRootLayout;

        CircularImageView mAvatarImageView;

        TextView mSenderTextView;

        TextView mBubbleTextView;

        TextView mTimestampTextView;

        Message mMessage;

        View mRootView;

        MessageViewHolder(View itemView, int type) {
            super(itemView);

            mType = type;

            mRootView = itemView;

            mRootLayout = itemView.findViewById(R.id.root);

            mBubbleTextView = itemView.findViewById(R.id.message_bubble);

            mTimestampTextView = itemView.findViewById(R.id.message_timestamp);

            mAvatarImageView = itemView.findViewById(R.id.message_avatar);

            mSenderTextView = itemView.findViewById(R.id.message_sender);

        }

        void bindData(Message message) {
            this.mMessage = message;

            handleType();

            switch (this.mMessage.getMessageType()) {
                case MessageType.REC_END:
                    case MessageType.REC_HEADER_FULL:
                        case MessageType.REC_MIDDLE:
                            case MessageType.REC_HEADER_SERIES:
                                mSenderTextView.setText(mMessage.getSender().getFirstName());

                                Glide.with(mContext)
                                        .load(message.getSender().getProfileImageUrl())
                                        .into(mAvatarImageView);
                                break;
            }

            mBubbleTextView.setText(mMessage.getContent());
            mTimestampTextView.setText(DateUtils.parseTime(mMessage.getCreatedAt().getTime(), mContext));
            mBubbleTextView.setOnLongClickListener(view -> {showMessageInfoDialog(mContext, message); return true;});
            showReaction();
        }

        @SuppressLint("ClickableViewAccessibility")
        private void showReaction() {
            final int[] reactions = new int[]{
                    R.drawable.ic_like,
                    R.drawable.ic_heart,
                    R.drawable.ic_happy,
                    R.drawable.ic_surprise,
                    R.drawable.ic_sad,
                    R.drawable.ic_angry};

            final ReactionsConfig config =
                    new ReactionsConfigBuilder(mContext)
                    .withReactions(reactions)
                    .build();

            ReactionPopup popup = new
                    ReactionPopup(mContext, config, (position) -> {
                if (position != -1) {
                    View reaction = mRootView.findViewById(R.id.reaction);
                    reaction.setVisibility(View.VISIBLE);
                    reaction.setBackground(mContext.getDrawable(reactions[position]));

                    // user can only hide their own messages
                    if (mMessage.isOwn()) {
                        reaction.setOnTouchListener(new View.OnTouchListener() {
                            private GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                                @Override
                                public boolean onDoubleTap(MotionEvent e) {
                                    Log.d(TAG, "onDoubleTap");
                                    reaction.setVisibility(View.GONE);
                                    return super.onDoubleTap(e);
                                }// implement here other callback methods like onFling, onScroll as necessary
                            });

                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                Log.d("TEST", "Raw event: " + motionEvent.getAction() + ", (" + motionEvent.getRawX() + ", " + motionEvent.getRawY() + ")");
                                gestureDetector.onTouchEvent(motionEvent);
                                return true;
                            }
                        });
                    }
                }
                return true; // true is closing popup, false is requesting a new selection
            });

            mRootLayout.setOnTouchListener(popup);
        }

        private void handleType() {
            switch (mType) {
                case MessageType.REC_HEADER_FULL:
                    mAvatarImageView.setVisibility(View.VISIBLE);
                    mSenderTextView.setVisibility(View.VISIBLE);
                    mTimestampTextView.setVisibility(View.VISIBLE);
                    break;
                case MessageType.REC_HEADER_SERIES:
                    mAvatarImageView.setVisibility(View.VISIBLE);
                    mSenderTextView.setVisibility(View.VISIBLE);
                    mTimestampTextView.setVisibility(View.GONE);
                    break;
                case MessageType.REC_MIDDLE:
                    mAvatarImageView.setVisibility(View.INVISIBLE);
                    mSenderTextView.setVisibility(View.GONE);
                    mTimestampTextView.setVisibility(View.GONE);
                    break;
                case MessageType.REC_END:
                    mAvatarImageView.setVisibility(View.INVISIBLE);
                    mSenderTextView.setVisibility(View.GONE);
                    mTimestampTextView.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    static class DiffCallback extends DiffUtil.Callback {

        List<Message> newMessages;
        List<Message> oldMessages;

        DiffCallback(List<Message> newMessages, List<Message> oldMessages) {
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

    private void showMessageInfoDialog(Context context, Message Message) {
        String contentBuilder = "" + Utils.emphasizeText("Sender: ") +
                Message.getSender().getFullName() +
                Utils.newLine() +
                Utils.emphasizeText("Date time: ") +
                DateUtils.parseDateTime(Message.getCreatedAt().getTime()) +
                Utils.newLine() +
                Utils.emphasizeText("Relative: ") +
                DateUtils.getRelativeTimeAgo(Message.getCreatedAt().getTime()) +
                Utils.newLine() +
                Utils.emphasizeText("Own Message: ") +
                Message.getSender().getObjectId().equals(App.get().getCurrentUser().getStudent().getObjectId());

        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title(R.string.message_info)
                .content(Html.fromHtml(contentBuilder, Html.FROM_HTML_OPTION_USE_CSS_COLORS))
                .contentColor(mContext.getColor(R.color.white))
                .positiveText(android.R.string.ok)
                .backgroundColor(mContext.getColor(R.color.colorPrimary))
                .positiveColor(mContext.getColor(R.color.black))
                .build();
        materialDialog.show();
    }

}
