package com.erastus.orientate.student.announcements;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.erastus.orientate.student.announcements.models.LocalAnnouncement;
import com.erastus.orientate.utils.DateUtils;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder> {

    private List<LocalAnnouncement> mAnnouncements;

    public AnnouncementAdapter(List<LocalAnnouncement> announcementList) {
        this.mAnnouncements = mAnnouncements;
    }




    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        holder.bind(mAnnouncements.get(position));
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class AnnouncementViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView mPostedOnMaterialTextView;
        private MaterialTextView mPostedByMaterialTextView;
        private MaterialTextView mAnnouncementTitleMaterialTextView;
        private MaterialTextView mAnnouncementBodyMaterialTextView;
        private ImageView mProfilePictureImageView;

        public AnnouncementViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(LocalAnnouncement localAnnouncement) {
            mPostedOnMaterialTextView.setText(DateUtils.getRelativeTimeAgo(localAnnouncement.getCreatedAt()));
            mPostedByMaterialTextView.setText(localAnnouncement.getPostedBy());
            mAnnouncementTitleMaterialTextView.setText(localAnnouncement.getTitle());

        }
    }
}
