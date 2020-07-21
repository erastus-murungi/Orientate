package com.erastus.orientate.student.event.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erastus.orientate.R;
import com.erastus.orientate.databinding.ItemOuterEventBinding;
import com.erastus.orientate.student.event.EventViewModel;
import com.erastus.orientate.student.event.models.LocalEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class EventTimeAdapter extends RecyclerView.Adapter<EventTimeAdapter.EventTimeViewHolder> {
    private Map<String, Set<LocalEvent>> mStartTimes = new HashMap<>();
    private List<String> mKeys = new ArrayList<>();
    private Context mContext;
    private EventViewModel mEventViewModel;
    private RecyclerView.RecycledViewPool mViewPool = new RecyclerView.RecycledViewPool();
    private EventContentAdapter.OnEventClickedListener mEventClickedListener;


    public EventTimeAdapter(Context context,
                            EventViewModel viewModel,
                            EventContentAdapter.OnEventClickedListener listener) {
        this.mContext = context;
        Map<String, Set<LocalEvent>> m = viewModel.getEvents().getValue();
        if (m == null) {
            m = new HashMap<>();
        }
        mergeMaps(mStartTimes, Objects.requireNonNull(m));
        this.mEventViewModel = viewModel;
        this.mEventClickedListener = listener;
    }

    public <K, V> void mergeMaps(Map<K, Set<V>> a, Map<K, Set<V>> b) {
        b.forEach((k, v) -> a.merge(k, v, (Set<V> va, Set<V> vb) -> {
            va.addAll(vb);
            return va;
        }));
        mKeys = new ArrayList<>(mStartTimes.keySet());
    }

    public void setEvents(Map<String, Set<LocalEvent>> events) {
        mStartTimes.clear();
        mergeMaps(mStartTimes, events);
    }

    @NonNull
    @Override
    public EventTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOuterEventBinding binding = ItemOuterEventBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent, false);
        EventTimeViewHolder holder = new EventTimeViewHolder(binding);
        holder.mRecyclerView.setRecycledViewPool(mViewPool);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventTimeViewHolder holder, int position) {
        holder.bind(mKeys.get(position));
    }

    @Override
    public int getItemCount() {
        return mStartTimes == null ? 0 : mStartTimes.size();
    }

    class EventTimeViewHolder extends RecyclerView.ViewHolder {
        private TextView mStartingOnTextView;
        private RecyclerView mRecyclerView;

        public EventTimeViewHolder(@NonNull ItemOuterEventBinding binding) {
            super(binding.getRoot());
            mStartingOnTextView = binding.textViewEventStartHeader;
            mRecyclerView = binding.recyclerViewInner;
        }

        public void bind(String localTime) {
            mStartingOnTextView.setText(localTime);
            initRecyclerView(localTime);
        }

        private void initRecyclerView(String time) {
            mRecyclerView.setAdapter(new EventContentAdapter(
                    new ArrayList<>(Objects.requireNonNull(mStartTimes.get(time))),
                    mEventClickedListener));

            mRecyclerView.setLayoutManager(new
                    LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
            final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mContext,
                    DividerItemDecoration.HORIZONTAL);
            dividerItemDecoration.setDrawable(Objects.requireNonNull(
                    mContext.getDrawable(R.drawable.dark_blue_divider)));
            mRecyclerView.addItemDecoration(dividerItemDecoration);
        }
    }
}
