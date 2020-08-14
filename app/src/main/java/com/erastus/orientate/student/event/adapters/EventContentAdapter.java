package com.erastus.orientate.student.event.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.erastus.orientate.R;
import com.erastus.orientate.databinding.ItemInnerEventBinding;
import com.erastus.orientate.student.event.models.Event;
import com.erastus.orientate.utils.DateUtils;

import java.util.List;

public class EventContentAdapter extends RecyclerView.Adapter<EventContentAdapter.EventContentViewHolder> {
    private List<Event> mEvents;
    private OnEventClickedListener mOnEventClickedListener;
    private Context mContext;


    public interface OnEventClickedListener {
        void onEventCLickedListener(Event event);
    }

    public EventContentAdapter(Context context, List<Event> eventsSpecificTime, OnEventClickedListener listener) {
        mEvents = eventsSpecificTime;
        mOnEventClickedListener = listener;
        mContext = context;
    }

    @NonNull
    @Override
    public EventContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInnerEventBinding binding = ItemInnerEventBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new EventContentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventContentViewHolder holder, int position) {
        holder.bind(mEvents.get(position));
    }
    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    class EventContentViewHolder extends RecyclerView.ViewHolder {
        private TextView mStartingOnTextView;
        private TextView mEndingOnTextView;
        private TextView mTitleTextView;
        private TextView mEventLocationTextView;
        private ImageView mMustAttendImageView;

        public EventContentViewHolder(@NonNull ItemInnerEventBinding binding) {
            super(binding.getRoot());
            mStartingOnTextView = binding.textViewStartingOn;
            mEndingOnTextView = binding.textViewEndingOn;
            mTitleTextView = binding.textViewEventTitle;
            mEventLocationTextView = binding.textViewEventLocation;
            mMustAttendImageView = binding.imageViewMustAttend;

            binding.getRoot().setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                mOnEventClickedListener.onEventCLickedListener(
                        position == RecyclerView.NO_POSITION ? null : mEvents.get(position));
            });
        }

        public void bind(Event event) {
            mStartingOnTextView.setText(DateUtils.getTimeAmPm(event.getStartingOn()));
            mEndingOnTextView.setText(DateUtils.getTimeAmPm(event.getEndingOn()));
            mTitleTextView.setText(event.getTitle());
            mEventLocationTextView.setText(event.getStringLocation());

            if (event.getIsMustAttend()) {
                mMustAttendImageView.setBackground(mContext.getDrawable(R.drawable.ic_circle_red_24dp));
            } else {
                mMustAttendImageView.setBackground(mContext.getDrawable(R.drawable.ic_circle_green_24dp));
            }
        }
    }
}
