package com.erastus.orientate.student.event.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.erastus.orientate.R;
import com.erastus.orientate.student.event.models.LocalEvent;

import java.util.List;

public class EventContentAdapter extends RecyclerView.Adapter<EventContentAdapter.EventContentViewHolder> {
    private List<LocalEvent> mEvents;

    public EventContentAdapter(List<LocalEvent> eventsSpecificTime) {
        mEvents = eventsSpecificTime;
    }

    @NonNull
    @Override
    public EventContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EventContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inner_event, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EventContentViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    class EventContentViewHolder extends RecyclerView.ViewHolder {

        public EventContentViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
