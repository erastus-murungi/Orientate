package com.erastus.orientate.student.announcements.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.erastus.orientate.R;
import com.erastus.orientate.databinding.ItemAnnouncementBinding;
import com.erastus.orientate.student.announcements.models.LocalAnnouncement;
import com.erastus.orientate.utils.DateUtils;
import com.erastus.orientate.utils.UrlValidator;
import com.erastus.orientate.utils.richlinkpreview.RichLinkView;
import com.erastus.orientate.utils.richlinkpreview.ViewListener;
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


    public void onNewProducts(List<LocalAnnouncement> newAnnouncements) {
        DiffUtil.DiffResult diffResult =
                DiffUtil.calculateDiff(
                        new AnnouncementDiffUtilCallback(mAnnouncements, newAnnouncements));
        diffResult.dispatchUpdatesTo(this);
        mAnnouncements = newAnnouncements;
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
        private static final String TAG = "AnnouncementViewHolder";

        private MaterialTextView mPostedOnMaterialTextView;
        private MaterialTextView mPostedByMaterialTextView;
        private MaterialTextView mAnnouncementTitleMaterialTextView;
        private MaterialTextView mAnnouncementBodyMaterialTextView;
        private ImageView mProfilePictureImageView;
        private RichLinkView mRichLinkView;
        private ImageView mImportanceImageView;

        public AnnouncementViewHolder(@NonNull ItemAnnouncementBinding announcementBinding) {
            super(announcementBinding.getRoot());
            mPostedByMaterialTextView = mAnnouncementBinding.textViewPostedBy;
            mPostedOnMaterialTextView = mAnnouncementBinding.textViewPostedOn;
            mAnnouncementTitleMaterialTextView = mAnnouncementBinding.textViewAnnouncementTitle;
            mAnnouncementBodyMaterialTextView = mAnnouncementBinding.textViewAnnouncementBody;
            mProfilePictureImageView = mAnnouncementBinding.imageViewProfilePicture;
            mRichLinkView = mAnnouncementBinding.richLinkViewAnnouncement;
            mImportanceImageView = mAnnouncementBinding.imageViewImportance;
        }

        public void bind(LocalAnnouncement localAnnouncement) {
            mPostedOnMaterialTextView.setText(DateUtils.getDate(localAnnouncement.getCreatedAt()));
            mPostedByMaterialTextView.setText(localAnnouncement.getPostedBy());
            mAnnouncementTitleMaterialTextView.setText(localAnnouncement.getTitle());
            mAnnouncementBodyMaterialTextView.setText(localAnnouncement.getBody());
            Glide.with(mContext)
                    .load(localAnnouncement.getOwner().getProfileImageUrl())
                    .fitCenter()
                    .circleCrop()
                    .placeholder(R.drawable.ic_education)
                    .into(mProfilePictureImageView);
            String stringUrl = localAnnouncement.getUrl();
            if (stringUrl != null && UrlValidator.isUrlValid(stringUrl)) {
                mRichLinkView.setVisibility(View.VISIBLE);
                mRichLinkView.setLink(stringUrl, new ViewListener() {
                    @Override
                    public void onSuccess(boolean status) {
                        //
                    }

                    @Override
                    public void onError(Exception e) {
                        //
                    }
                });
            } else {
                mRichLinkView.setVisibility(View.GONE);
            }
            switch (localAnnouncement.getUrgencyLevel()) {
                case CASUAL:
                    mImportanceImageView.setBackground(mContext.getDrawable(R.drawable.ic_circle_green_24dp));
                    break;
                case IMPORTANT:
                    mImportanceImageView.setBackground(mContext.getDrawable(R.drawable.ic_circle_yellow_24dp));
                    break;
                case VERY_IMPORTANT:
                    mImportanceImageView.setBackground(mContext.getDrawable(R.drawable.ic_circle_red_24dp));
                    break;
                default:
                    Log.e(TAG, "bind: announcement" + localAnnouncement + "has no importance level");
            }
        }
    }
}
