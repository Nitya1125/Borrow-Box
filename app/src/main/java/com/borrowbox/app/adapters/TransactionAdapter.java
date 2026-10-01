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

    private Context context;
    private List<Transaction> transactionList;
    private boolean isRequestsTab;
    private OnTransactionActionListener listener;

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
            reqHolder.tvTitle.setText(tx.getPerson() + " wants to borrow");
            reqHolder.tvItemName.setText(tx.getItem().getName());
            reqHolder.tvReturnDate.setText("Return: " + tx.getReturnDate());

            Glide.with(context)
                    .load(tx.getPersonAvatar())
                    .placeholder(R.drawable.ic_profile)
                    .into(reqHolder.ivPersonAvatar);

            reqHolder.btnAccept.setOnClickListener(v -> {
                reqHolder.layoutActions.setVisibility(View.GONE);
                reqHolder.tvResult.setVisibility(View.VISIBLE);
                reqHolder.tvResult.setText("✓ Request Accepted");
                reqHolder.tvResult.setTextColor(ContextCompat.getColor(context, R.color.teal_success));
                if (listener != null) listener.onAcceptRequest(tx, holder.getAdapterPosition());
            });

            reqHolder.btnReject.setOnClickListener(v -> {
                reqHolder.layoutActions.setVisibility(View.GONE);
                reqHolder.tvResult.setVisibility(View.VISIBLE);
                reqHolder.tvResult.setText("✗ Request Rejected");
                reqHolder.tvResult.setTextColor(ContextCompat.getColor(context, R.color.coral_error));
                if (listener != null) listener.onRejectRequest(tx, holder.getAdapterPosition());
            });
        } else if (holder instanceof TransactionViewHolder) {
            TransactionViewHolder txHolder = (TransactionViewHolder) holder;
            txHolder.tvItemName.setText(tx.getItem().getName());
            txHolder.tvPersonName.setText(tx.getPerson());

            String typePrefix = "borrowed".equalsIgnoreCase(tx.getType()) ? "Borrowed: " : "Lent: ";
            txHolder.tvDate.setText(typePrefix + tx.getBorrowDate());

            // Status badge styling
            String status = tx.getStatus().toLowerCase();
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
                    txHolder.btnQr.setVisibility(View.GONE);
                    break;
                default:
                    txHolder.tvStatus.setText(tx.getStatus());
                    txHolder.btnQr.setVisibility(View.GONE);
                    break;
            }

            Glide.with(context)
                    .load(tx.getItem().getImageUrl())
                    .placeholder(R.drawable.bg_card)
                    .into(txHolder.ivItemImage);

            Glide.with(context)
                    .load(tx.getPersonAvatar())
                    .placeholder(R.drawable.ic_profile)
                    .into(txHolder.ivPersonAvatar);

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
