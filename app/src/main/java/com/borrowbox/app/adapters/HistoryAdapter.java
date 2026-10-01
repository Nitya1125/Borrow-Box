package com.borrowbox.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.borrowbox.app.R;
import com.borrowbox.app.models.Transaction;
import com.bumptech.glide.Glide;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<Transaction> historyList;

    public HistoryAdapter(Context context, List<Transaction> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Transaction tx = historyList.get(position);

        holder.tvItemName.setText(tx.getItem().getName());
        holder.tvPersonName.setText(tx.getPerson());
        holder.tvBorrowDate.setText(tx.getBorrowDate());
        holder.tvReturnDate.setText(tx.getReturnDate());

        Glide.with(context)
                .load(tx.getItem().getImageUrl())
                .placeholder(R.drawable.bg_card)
                .into(holder.ivItemImage);

        Glide.with(context)
                .load(tx.getPersonAvatar())
                .placeholder(R.drawable.ic_profile)
                .into(holder.ivPersonAvatar);
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    public void updateList(List<Transaction> list) {
        this.historyList = list;
        notifyDataSetChanged();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItemImage, ivPersonAvatar;
        TextView tvItemName, tvPersonName, tvBorrowDate, tvReturnDate;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.ivHistImage);
            ivPersonAvatar = itemView.findViewById(R.id.ivHistPersonAvatar);
            tvItemName = itemView.findViewById(R.id.tvHistItemName);
            tvPersonName = itemView.findViewById(R.id.tvHistPersonName);
            tvBorrowDate = itemView.findViewById(R.id.tvHistBorrowDate);
            tvReturnDate = itemView.findViewById(R.id.tvHistReturnDate);
        }
    }
}
