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
import com.borrowbox.app.models.Item;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MyItemAdapter extends RecyclerView.Adapter<MyItemAdapter.MyItemViewHolder> {

    public interface OnMyItemActionListener {
        void onEdit(Item item);
        void onToggleActive(Item item, int position);
    }

    private Context context;
    private List<Item> itemList;
    private OnMyItemActionListener listener;

    public MyItemAdapter(Context context, List<Item> itemList, OnMyItemActionListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_item, parent, false);
        return new MyItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyItemViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.tvName.setText(item.getName());
        holder.tvCategory.setText(item.getCategory());

        String status = item.getStatus() != null ? item.getStatus() : (item.isAvailable() ? "available" : "borrowed");

        switch (status.toLowerCase()) {
            case "available":
                holder.tvStatusBadge.setText("Available");
                holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.teal_success));
                holder.tvStatusBadge.setBackgroundResource(R.drawable.badge_available);
                holder.tvBorrower.setVisibility(View.GONE);
                holder.btnDeactivate.setText("Deactivate");
                break;
            case "borrowed":
                holder.tvStatusBadge.setText("Borrowed");
                holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.primary));
                holder.tvStatusBadge.setBackgroundResource(R.drawable.badge_approved);
                if (item.getCurrentBorrower() != null) {
                    holder.tvBorrower.setVisibility(View.VISIBLE);
                    holder.tvBorrower.setText("Borrower: " + item.getCurrentBorrower());
                } else {
                    holder.tvBorrower.setVisibility(View.GONE);
                }
                holder.btnDeactivate.setText("Deactivate");
                break;
            case "inactive":
            default:
                holder.tvStatusBadge.setText("Inactive");
                holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.text_tertiary));
                holder.tvStatusBadge.setBackgroundResource(R.drawable.badge_approved);
                holder.tvBorrower.setVisibility(View.GONE);
                holder.btnDeactivate.setText("Delete");
                break;
        }

        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.bg_card)
                .into(holder.ivImage);

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(item);
        });

        holder.btnDeactivate.setOnClickListener(v -> {
            if (listener != null) listener.onToggleActive(item, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public void updateList(List<Item> list) {
        this.itemList = list;
        notifyDataSetChanged();
    }

    public static class MyItemViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvCategory, tvStatusBadge, tvBorrower;
        MaterialButton btnEdit, btnDeactivate;

        public MyItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivMyItemImage);
            tvName = itemView.findViewById(R.id.tvMyItemName);
            tvCategory = itemView.findViewById(R.id.tvMyItemCategory);
            tvStatusBadge = itemView.findViewById(R.id.tvMyItemStatusBadge);
            tvBorrower = itemView.findViewById(R.id.tvMyItemBorrower);
            btnEdit = itemView.findViewById(R.id.btnMyItemEdit);
            btnDeactivate = itemView.findViewById(R.id.btnMyItemDeactivate);
        }
    }
}
