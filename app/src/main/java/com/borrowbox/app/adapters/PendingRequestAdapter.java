package com.borrowbox.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.borrowbox.app.R;
import com.borrowbox.app.models.Transaction;
import com.borrowbox.app.utils.AvatarDrawable;
import com.borrowbox.app.utils.DateTimeUtils;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class PendingRequestAdapter extends RecyclerView.Adapter<PendingRequestAdapter.RequestViewHolder> {

    public interface OnRequestActionListener {
        void onAccept(Transaction tx, int position);
        void onReject(Transaction tx, int position);
    }

    private final Context context;
    private List<Transaction> list;
    private final OnRequestActionListener listener;

    public PendingRequestAdapter(Context context, List<Transaction> list, OnRequestActionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_pending_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Transaction tx = list.get(position);

        String borrower = tx.getPerson();
        holder.tvBorrowerName.setText(borrower);
        holder.tvItemName.setText("Wants: " + (tx.getItem() != null ? tx.getItem().getName() : "--"));
        holder.tvDays.setText(DateTimeUtils.formatTimeAgo(tx.getRequestedAt()));

        String avatar = tx.getPersonAvatar();
        if (avatar != null && !avatar.isEmpty()) {
            Glide.with(context)
                    .load(avatar)
                    .placeholder(new AvatarDrawable(borrower))
                    .into(holder.ivAvatar);
        } else {
            holder.ivAvatar.setImageDrawable(new AvatarDrawable(borrower));
        }

        holder.layoutActions.setVisibility(View.VISIBLE);
        holder.tvResult.setVisibility(View.GONE);

        holder.btnAccept.setOnClickListener(v -> {
            holder.layoutActions.setVisibility(View.GONE);
            holder.tvResult.setVisibility(View.VISIBLE);
            holder.tvResult.setText(R.string.request_accepted_notif);
            holder.tvResult.setTextColor(ContextCompat.getColor(context, R.color.teal_success));
            if (listener != null) listener.onAccept(tx, holder.getAdapterPosition());
        });

        holder.btnReject.setOnClickListener(v -> {
            holder.layoutActions.setVisibility(View.GONE);
            holder.tvResult.setVisibility(View.VISIBLE);
            holder.tvResult.setText(R.string.request_rejected_notif);
            holder.tvResult.setTextColor(ContextCompat.getColor(context, R.color.coral_error));
            if (listener != null) listener.onReject(tx, holder.getAdapterPosition());
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

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvBorrowerName, tvItemName, tvDays, tvResult;
        LinearLayout layoutActions;
        MaterialButton btnAccept, btnReject;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivReqBorrowerAvatar);
            tvBorrowerName = itemView.findViewById(R.id.tvReqBorrowerName);
            tvItemName = itemView.findViewById(R.id.tvReqItemRequested);
            tvDays = itemView.findViewById(R.id.tvReqDaysLeft);
            layoutActions = itemView.findViewById(R.id.layoutHomeReqActions);
            tvResult = itemView.findViewById(R.id.tvHomeReqResult);
            btnAccept = itemView.findViewById(R.id.btnHomeAcceptReq);
            btnReject = itemView.findViewById(R.id.btnHomeRejectReq);
        }
    }
}
