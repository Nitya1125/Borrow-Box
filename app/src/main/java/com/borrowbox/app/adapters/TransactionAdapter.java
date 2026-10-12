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

public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_TRANSACTION = 0;
    private static final int TYPE_REQUEST = 1;

    public interface OnTransactionActionListener {
        void onShowQr(Transaction transaction);
        void onAcceptRequest(Transaction transaction, int position);
        void onRejectRequest(Transaction transaction, int position);
    }

    private final Context context;
    private List<Transaction> transactionList;
    private boolean isRequestsTab;
    private final OnTransactionActionListener listener;

    public TransactionAdapter(Context context, List<Transaction> transactionList, boolean isRequestsTab, OnTransactionActionListener listener) {
        this.context = context;
        this.transactionList = transactionList;
        this.isRequestsTab = isRequestsTab;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return isRequestsTab ? TYPE_REQUEST : TYPE_TRANSACTION;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_REQUEST) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
            return new RequestViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
            return new TransactionViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Transaction tx = transactionList.get(position);

        if (holder instanceof RequestViewHolder) {
            RequestViewHolder reqHolder = (RequestViewHolder) holder;
            String personName = tx.getPerson();
            String itemName = (tx.getItem() != null) ? tx.getItem().getName() : "--";

            reqHolder.tvTitle.setText(personName + " wants to borrow");
            reqHolder.tvItemName.setText(itemName);

            if (tx.getDueAt() > 0) {
                reqHolder.tvReturnDate.setText("Return: " + DateTimeUtils.formatDate(tx.getDueAt()));
            } else {
                reqHolder.tvReturnDate.setText("Requested " + DateTimeUtils.formatTimeAgo(tx.getRequestedAt()));
            }

            String avatarUrl = tx.getPersonAvatar();
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(context)
                        .load(avatarUrl)
                        .placeholder(new AvatarDrawable(personName))
                        .error(new AvatarDrawable(personName))
                        .into(reqHolder.ivPersonAvatar);
            } else {
                reqHolder.ivPersonAvatar.setImageDrawable(new AvatarDrawable(personName));
            }

            reqHolder.layoutActions.setVisibility(View.VISIBLE);
            reqHolder.tvResult.setVisibility(View.GONE);

            reqHolder.btnAccept.setOnClickListener(v -> {
                reqHolder.layoutActions.setVisibility(View.GONE);
                reqHolder.tvResult.setVisibility(View.VISIBLE);
                reqHolder.tvResult.setText(R.string.request_accepted_notif);
                reqHolder.tvResult.setTextColor(ContextCompat.getColor(context, R.color.teal_success));
                if (listener != null) listener.onAcceptRequest(tx, holder.getAdapterPosition());
            });

            reqHolder.btnReject.setOnClickListener(v -> {
                reqHolder.layoutActions.setVisibility(View.GONE);
                reqHolder.tvResult.setVisibility(View.VISIBLE);
                reqHolder.tvResult.setText(R.string.request_rejected_notif);
                reqHolder.tvResult.setTextColor(ContextCompat.getColor(context, R.color.coral_error));
                if (listener != null) listener.onRejectRequest(tx, holder.getAdapterPosition());
            });
        } else if (holder instanceof TransactionViewHolder) {
            TransactionViewHolder txHolder = (TransactionViewHolder) holder;
            String itemName = (tx.getItem() != null) ? tx.getItem().getName() : "--";
            txHolder.tvItemName.setText(itemName);
            txHolder.tvPersonName.setText(tx.getPerson());

            String typePrefix = "borrowed".equalsIgnoreCase(tx.getType()) ? "Borrowed: " : "Shared: ";
            if (tx.getRequestedAt() > 0) {
                txHolder.tvDate.setText(typePrefix + DateTimeUtils.formatDate(tx.getRequestedAt()));
            } else {
                txHolder.tvDate.setText(typePrefix + "--");
            }

            // Status badge styling
            String status = tx.getStatus() != null ? tx.getStatus().toLowerCase() : "pending";
            switch (status) {
                case "pending":
                    txHolder.tvStatus.setText("Pending");
                    txHolder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.amber_warning));
                    txHolder.tvStatus.setBackgroundResource(R.drawable.badge_pending);
                    txHolder.btnQr.setVisibility(View.GONE);
                    break;
                case "approved":
                    txHolder.tvStatus.setText("Approved");
                    txHolder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.primary));
                    txHolder.tvStatus.setBackgroundResource(R.drawable.badge_approved);
                    txHolder.btnQr.setVisibility(View.VISIBLE);
                    break;
                case "borrowed":
                    txHolder.tvStatus.setText("Borrowed");
                    txHolder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.teal_success));
                    txHolder.tvStatus.setBackgroundResource(R.drawable.badge_available);
                    txHolder.btnQr.setVisibility(View.VISIBLE);
                    break;
                case "returned":
                    txHolder.tvStatus.setText("Returned");
                    txHolder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.text_tertiary));
                    txHolder.tvStatus.setBackgroundResource(R.drawable.badge_approved);
                    txHolder.btnQr.setVisibility(View.GONE);
                    break;
                case "overdue":
                    txHolder.tvStatus.setText("Overdue");
                    txHolder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.coral_error));
                    txHolder.tvStatus.setBackgroundResource(R.drawable.badge_borrowed);
                    txHolder.btnQr.setVisibility(View.VISIBLE);
                    break;
                default:
                    txHolder.tvStatus.setText(tx.getStatus());
                    txHolder.btnQr.setVisibility(View.GONE);
                    break;
            }

            String photo = (tx.getItem() != null) ? tx.getItem().getImageUrl() : "";
            Glide.with(context)
                    .load(photo)
                    .placeholder(R.drawable.bg_card)
                    .into(txHolder.ivItemImage);

            String avatar = tx.getPersonAvatar();
            if (avatar != null && !avatar.isEmpty()) {
                Glide.with(context)
                        .load(avatar)
                        .placeholder(new AvatarDrawable(tx.getPerson()))
                        .error(new AvatarDrawable(tx.getPerson()))
                        .into(txHolder.ivPersonAvatar);
            } else {
                txHolder.ivPersonAvatar.setImageDrawable(new AvatarDrawable(tx.getPerson()));
            }

            txHolder.btnQr.setOnClickListener(v -> {
                if (listener != null) listener.onShowQr(tx);
            });
        }
    }

    @Override
    public int getItemCount() {
        return transactionList != null ? transactionList.size() : 0;
    }

    public void updateData(List<Transaction> newList, boolean isRequests) {
        this.transactionList = newList;
        this.isRequestsTab = isRequests;
        notifyDataSetChanged();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItemImage, ivPersonAvatar;
        TextView tvItemName, tvPersonName, tvStatus, tvDate;
        MaterialButton btnQr;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.ivTxItemImage);
            ivPersonAvatar = itemView.findViewById(R.id.ivTxPersonAvatar);
            tvItemName = itemView.findViewById(R.id.tvTxItemName);
            tvPersonName = itemView.findViewById(R.id.tvTxPersonName);
            tvStatus = itemView.findViewById(R.id.tvTxStatusBadge);
            tvDate = itemView.findViewById(R.id.tvTxDate);
            btnQr = itemView.findViewById(R.id.btnTxQr);
        }
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPersonAvatar;
        TextView tvTitle, tvItemName, tvReturnDate, tvResult;
        LinearLayout layoutActions;
        MaterialButton btnAccept, btnReject;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPersonAvatar = itemView.findViewById(R.id.ivReqPersonAvatar);
            tvTitle = itemView.findViewById(R.id.tvReqTitle);
            tvItemName = itemView.findViewById(R.id.tvReqItemName);
            tvReturnDate = itemView.findViewById(R.id.tvReqReturnDate);
            tvResult = itemView.findViewById(R.id.tvReqStatusResult);
            layoutActions = itemView.findViewById(R.id.layoutActionButtons);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
