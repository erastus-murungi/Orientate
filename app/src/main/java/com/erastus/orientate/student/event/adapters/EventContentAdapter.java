package com.erastus.orientate.student.event.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.erastus.orientate.databinding.ItemInnerEventBinding;
import com.erastus.orientate.student.event.eventdetail.EventDetailFragment;
import com.erastus.orientate.student.event.models.LocalEvent;
import com.erastus.orientate.utils.DateUtils;

import org.parceler.Parcels;

import java.util.List;

public class EventContentAdapter extends RecyclerView.Adapter<EventContentAdapter.EventContentViewHolder> {
    private List<LocalEvent> mEvents;
    private Context mContext;

    public EventContentAdapter(Context context,
                               List<LocalEvent> eventsSpecificTime) {
        mEvents = eventsSpecificTime;
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
        private TextView mLocation;

        public EventContentViewHolder(@NonNull ItemInnerEventBinding binding) {
            super(binding.getRoot());
            mStartingOnTextView = binding.textViewStartingOn;
            mEndingOnTextView = binding.textViewEndingOn;
            mTitleTextView = binding.textViewEventTitle;
            mLocation = binding.textViewEventLocation;

            View.OnClickListener listener = view -> {
                int position = getBindingAdapterPosition();
                if (getBindingAdapterPosition() != RecyclerView.NO_POSITION) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("DATA", Parcels.wrap(mEvents.get(position)));
                    EventDetailFragment fragmentEventDetail = new EventDetailFragment();
                    fragmentEventDetail.setArguments(bundle);
                }
            };
            binding.getRoot().setOnClickListener(listener);
        }

        public void bind(LocalEvent localEvent) {
            mStartingOnTextView.setText(DateUtils.getTimeAmPm(localEvent.getStartingOn()));
            mEndingOnTextView.setText(DateUtils.getTimeAmPm(localEvent.getEndingOn()));
            mTitleTextView.setText(localEvent.getTitle());
            mLocation.setText(localEvent.getStringLocation());
        }
    }
}
