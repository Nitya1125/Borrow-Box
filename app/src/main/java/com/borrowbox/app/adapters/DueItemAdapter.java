package com.borrowbox.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.borrowbox.app.R;
import com.borrowbox.app.models.Transaction;
import com.borrowbox.app.utils.DateTimeUtils;
import com.bumptech.glide.Glide;

import java.util.List;

public class DueItemAdapter extends RecyclerView.Adapter<DueItemAdapter.DueViewHolder> {

    public interface OnDueItemClickListener {
        void onItemClick(Transaction transaction);
    }

    private final Context context;
    private List<Transaction> list;
    private final OnDueItemClickListener listener;

    public DueItemAdapter(Context context, List<Transaction> list, OnDueItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_due_card, parent, false);
        return new DueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DueViewHolder holder, int position) {
        Transaction tx = list.get(position);

        holder.tvName.setText(tx.getItem() != null ? tx.getItem().getName() : "--");

        long dueAt = tx.getDueAt();
        if (dueAt > 0) {
            holder.tvDate.setText("Due: " + DateTimeUtils.formatDate(dueAt));
            if (DateTimeUtils.isOverdue(dueAt)) {
                int daysOverdue = DateTimeUtils.getDaysOverdue(dueAt);
                holder.tvChip.setText(daysOverdue + (daysOverdue == 1 ? " day overdue" : " days overdue"));
                holder.tvChip.setBackgroundResource(R.drawable.badge_borrowed);
                holder.tvChip.setTextColor(ContextCompat.getColor(context, R.color.coral_error));
            } else {
                int daysLeft = DateTimeUtils.getDaysRemaining(dueAt);
                holder.tvChip.setText(daysLeft + (daysLeft == 1 ? " day left" : " days left"));
                holder.tvChip.setBackgroundResource(R.drawable.badge_pending);
                holder.tvChip.setTextColor(ContextCompat.getColor(context, R.color.amber_warning));
            }
        } else {
            holder.tvDate.setText("--");
            holder.tvChip.setText("Active");
        }

        String photoPath = (tx.getItem() != null) ? tx.getItem().getImageUrl() : "";
        Glide.with(context)
                .load(photoPath)
                .placeholder(R.drawable.bg_card)
                .into(holder.ivImage);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(tx);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public void updateList(List<Transaction> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    public static class DueViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvChip, tvName, tvDate;

        public DueViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivDueItemImage);
            tvChip = itemView.findViewById(R.id.tvDueChip);
            tvName = itemView.findViewById(R.id.tvDueItemName);
            tvDate = itemView.findViewById(R.id.tvDueDateText);
        }
    }
}
