package com.erastus.orientate.student.announcements.adapter;

import androidx.recyclerview.widget.DiffUtil;

import com.erastus.orientate.student.announcements.models.LocalAnnouncement;

import java.util.List;

public class AnnouncementDiffUtilCallback extends DiffUtil.Callback {
    List<LocalAnnouncement> previous;
    List<LocalAnnouncement> current;

    public AnnouncementDiffUtilCallback(List<LocalAnnouncement> previous,
                                        List<LocalAnnouncement> current) {
        this.previous = previous;
        this.current = current;
    }

    @Override
    public int getOldListSize() {
        return previous == null ? 0 : previous.size();
    }

    @Override
    public int getNewListSize() {
        return current.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // compare object references
        return previous.get(oldItemPosition) == current.get(newItemPosition);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return previous.get(oldItemPosition).equals(current.get(newItemPosition));
    }
}
