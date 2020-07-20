package com.erastus.orientate.student.event.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.erastus.orientate.R;
import com.erastus.orientate.databinding.ItemOuterEventBinding;
import com.erastus.orientate.student.event.EventViewModel;

import java.time.LocalDateTime;
import java.util.List;

public class EventTimeAdapter extends RecyclerView.Adapter<EventTimeAdapter.EventTimeViewHolder> {
    private List<LocalDateTime> mStartTimes;
    private Context mContext;
    private ItemOuterEventBinding binding;
    private EventViewModel mEventViewModel;
    private RecyclerView.RecycledViewPool mViewPool;


    public EventTimeAdapter(Context context, List<LocalDateTime> mStartTimes) {
        this.mContext = context;
        this.mStartTimes = mStartTimes;
        mViewPool = new RecyclerView.RecycledViewPool();

    }

    @NonNull
    @Override
    public EventTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemOuterEventBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent, false);
        EventTimeViewHolder holder = new EventTimeViewHolder(binding);
        holder.mRecyclerViewer.setRecycledViewPool(mViewPool);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventTimeViewHolder holder, int position) {
        holder.bind(mStartTimes.get(position));
    }

    @Override
    public int getItemCount() {
        return mStartTimes.size();
    }

    class EventTimeViewHolder extends RecyclerView.ViewHolder {
        private TextView mStartingOnTextView;
        private RecyclerView mRecyclerViewer;

        public EventTimeViewHolder(@NonNull ItemOuterEventBinding binding) {
            super(binding.getRoot());
            mStartingOnTextView = binding.textViewEventStartHeader;
            mRecyclerViewer = binding.recyclerViewInner;
        }

        public void bind(LocalDateTime localDateTime) {
            mStartingOnTextView.setText(mContext.getString(R.string.format_time_hour_minute,
                    localDateTime.getHour(), localDateTime.getMinute()));
        }

        private void initRecyclerView(LocalDateTime localDateTime) {
            mRecyclerViewer.setAdapter(new EventContentAdapter(
                    mEventViewModel.getEventsSpecificTime(localDateTime)));
        }
    }
}
