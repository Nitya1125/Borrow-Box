package com.borrowbox.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.borrowbox.app.R;
import com.borrowbox.app.models.NotificationItem;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationItem item, int position);
    }

    private Context context;
    private List<NotificationItem> notificationList;
    private OnNotificationClickListener listener;

    public NotificationAdapter(Context context, List<NotificationItem> notificationList, OnNotificationClickListener listener) {
        this.context = context;
        this.notificationList = notificationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem notif = notificationList.get(position);

        holder.tvTitle.setText(notif.getTitle());
        holder.tvMessage.setText(notif.getMessage());
        holder.tvTime.setText(notif.getTime());

        // Unread badge and icon styling
        if (notif.isRead()) {
            holder.unreadDot.setVisibility(View.GONE);
            holder.cardView.setStrokeColor(Color.parseColor("#F0F2FA"));
        } else {
            holder.unreadDot.setVisibility(View.VISIBLE);
            holder.cardView.setStrokeColor(Color.parseColor("#4F6AF0"));
        }

        switch (notif.getType().toLowerCase()) {
            case "request":
                holder.ivIcon.setImageResource(R.drawable.ic_notif_request);
                holder.ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.primary));
                holder.layoutIconBg.setBackgroundResource(R.drawable.badge_approved);
                break;
            case "accepted":
                holder.ivIcon.setImageResource(R.drawable.ic_notif_accepted);
                holder.ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.teal_success));
                holder.layoutIconBg.setBackgroundResource(R.drawable.badge_available);
                break;
            case "returned":
                holder.ivIcon.setImageResource(R.drawable.ic_notif_returned);
                holder.ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.purple_accent));
                holder.layoutIconBg.setBackgroundResource(R.drawable.badge_purple);
                break;
            case "due":
                holder.ivIcon.setImageResource(R.drawable.ic_notif_due);
                holder.ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.coral_error));
                holder.layoutIconBg.setBackgroundResource(R.drawable.badge_borrowed);
                break;
            case "reminder":
            default:
                holder.ivIcon.setImageResource(R.drawable.ic_notif_reminder);
                holder.ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.amber_warning));
                holder.layoutIconBg.setBackgroundResource(R.drawable.badge_pending);
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            notif.setRead(true);
            notifyItemChanged(holder.getAdapterPosition());
            if (listener != null) {
                listener.onNotificationClick(notif, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList != null ? notificationList.size() : 0;
    }

    public void updateList(List<NotificationItem> list) {
        this.notificationList = list;
        notifyDataSetChanged();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        FrameLayout layoutIconBg;
        ImageView ivIcon;
        TextView tvTitle, tvMessage, tvTime;
        View unreadDot;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            layoutIconBg = itemView.findViewById(R.id.layoutNotifIconBg);
            ivIcon = itemView.findViewById(R.id.ivNotifIcon);
            tvTitle = itemView.findViewById(R.id.tvNotifTitle);
            tvMessage = itemView.findViewById(R.id.tvNotifMessage);
            tvTime = itemView.findViewById(R.id.tvNotifTime);
            unreadDot = itemView.findViewById(R.id.viewUnreadDot);
        }
    }
}
