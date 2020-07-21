package com.erastus.orientate.student.event.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.erastus.orientate.databinding.ItemInnerEventBinding;
import com.erastus.orientate.student.event.models.LocalEvent;
import com.erastus.orientate.utils.DateUtils;

import java.util.List;

public class EventContentAdapter extends RecyclerView.Adapter<EventContentAdapter.EventContentViewHolder> {
    private List<LocalEvent> mEvents;
    private OnEventClickedListener mOnEventClickedListener;


    public interface OnEventClickedListener {
        void onEventCLickedListener(LocalEvent event);
    }

    public EventContentAdapter(List<LocalEvent> eventsSpecificTime, OnEventClickedListener listener) {
        mEvents = eventsSpecificTime;
        mOnEventClickedListener = listener;
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

        public EventContentViewHolder(@NonNull ItemInnerEventBinding binding) {
            super(binding.getRoot());
            mStartingOnTextView = binding.textViewStartingOn;
            mEndingOnTextView = binding.textViewEndingOn;
            mTitleTextView = binding.textViewEventTitle;
            mEventLocationTextView = binding.textViewEventLocation;

            binding.getRoot().setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                mOnEventClickedListener.onEventCLickedListener(
                        position == RecyclerView.NO_POSITION ? null : mEvents.get(position));
            });
        }

        public void bind(LocalEvent localEvent) {
            mStartingOnTextView.setText(DateUtils.getTimeAmPm(localEvent.getStartingOn()));
            mEndingOnTextView.setText(DateUtils.getTimeAmPm(localEvent.getEndingOn()));
            mTitleTextView.setText(localEvent.getTitle());
            mEventLocationTextView.setText(localEvent.getStringLocation());
        }
    }
}
