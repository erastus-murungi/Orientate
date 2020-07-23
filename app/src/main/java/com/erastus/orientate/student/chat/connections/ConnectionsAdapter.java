package com.erastus.orientate.student.chat.connections;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ConnectionsAdapter extends RecyclerView.Adapter<ConnectionsAdapter.ConnectionViewHolder> {
    private List<Connection> connectionList;

    public ConnectionsAdapter(List<Connection> connectionList) {
        this.connectionList = connectionList;
    }

    @NonNull
    @Override
    public ConnectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ConnectionViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return connectionList == null ? 0 : connectionList.size();
    }

    public class ConnectionViewHolder extends RecyclerView.ViewHolder {

        public ConnectionViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(Connection connection) {

        }
    }
}
