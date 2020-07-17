package com.erastus.orientate.student.event;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.erastus.orientate.R;
import com.erastus.orientate.student.event.dailyevent.DailyEventViewModel;
import com.erastus.orientate.student.event.models.Event;

import java.util.List;
import java.util.Objects;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Event}.
 */
public class MyDailyEventRecyclerViewAdapter
        extends RecyclerView.Adapter<MyDailyEventRecyclerViewAdapter.ViewHolder> {

    private final DailyEventViewModel mDailyEventViewModel;

    public MyDailyEventRecyclerViewAdapter(DailyEventViewModel dailyEventViewModel) {
        mDailyEventViewModel = dailyEventViewModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final List<Event> events = mDailyEventViewModel.getEvents().getValue();
        holder.bind(events.get(position));
    }

    @Override
    public int getItemCount() {
        return Objects.requireNonNull(mDailyEventViewModel.getEvents().getValue()).size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Event mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

        public void bind(Event event) {
            mItem = event;
        }
    }
}