package com.erastus.orientate.student.announcements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.erastus.orientate.databinding.ItemAnnouncementBinding;
import com.erastus.orientate.student.announcements.models.LocalAnnouncement;
import com.erastus.orientate.utils.DateUtils;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder> {
    private List<LocalAnnouncement> mAnnouncements;
    private Context mContext;
    private ItemAnnouncementBinding mAnnouncementBinding;
    private LayoutInflater mLayoutInflater;

    public AnnouncementAdapter(Context context, List<LocalAnnouncement> announcementList) {
        this.mAnnouncements = announcementList;
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public void setAnnouncements(List<LocalAnnouncement> announcements) {
        this.mAnnouncements = announcements;
    }

    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mAnnouncementBinding =
                ItemAnnouncementBinding.inflate(mLayoutInflater, parent, false);
        return new AnnouncementViewHolder(mAnnouncementBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        holder.bind(mAnnouncements.get(position));
    }

    @Override
    public int getItemCount() {
        return mAnnouncements == null ? 0 : mAnnouncements.size();
    }

    public class AnnouncementViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView mPostedOnMaterialTextView;
        private MaterialTextView mPostedByMaterialTextView;
        private MaterialTextView mAnnouncementTitleMaterialTextView;
        private MaterialTextView mAnnouncementBodyMaterialTextView;
        private ImageView mProfilePictureImageView;

        public AnnouncementViewHolder(@NonNull ItemAnnouncementBinding announcementBinding) {
            super(announcementBinding.getRoot());
            mPostedByMaterialTextView = mAnnouncementBinding.textViewPostedBy;
            mPostedOnMaterialTextView = mAnnouncementBinding.textViewPostedOn;
            mAnnouncementTitleMaterialTextView = mAnnouncementBinding.textViewAnnouncementTitle;
            mAnnouncementBodyMaterialTextView = mAnnouncementBinding.textViewAnnouncementBody;
            mProfilePictureImageView = mAnnouncementBinding.imageViewProfilePicture;
        }

        public void bind(LocalAnnouncement localAnnouncement) {
            mPostedOnMaterialTextView.setText(DateUtils.getRelativeTimeAgo(localAnnouncement.getCreatedAt()));
            mPostedByMaterialTextView.setText(localAnnouncement.getPostedBy());
            mAnnouncementTitleMaterialTextView.setText(localAnnouncement.getTitle());
            mAnnouncementBodyMaterialTextView.setText(localAnnouncement.getBody());
            Glide.with(mContext)
                    .load(localAnnouncement.getOwner().getUser().getProfileImageUrl())
                    .centerCrop()
                    .into(mProfilePictureImageView);
        }
    }
}
