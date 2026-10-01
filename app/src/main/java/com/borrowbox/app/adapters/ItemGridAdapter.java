package com.borrowbox.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.borrowbox.app.R;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.models.Item;
import com.bumptech.glide.Glide;

import java.util.List;

public class ItemGridAdapter extends RecyclerView.Adapter<ItemGridAdapter.ItemViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    private Context context;
    private List<Item> itemList;
    private OnItemClickListener listener;

    public ItemGridAdapter(Context context, List<Item> itemList, OnItemClickListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card_grid, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.tvName.setText(item.getName());
        holder.tvCategory.setText(item.getCategory());
        holder.tvOwner.setText(item.getOwner().split(" ")[0]);
        holder.tvDistance.setText(item.getDistance());

        // Status badge
        if (item.isAvailable()) {
            holder.tvStatusBadge.setText("Available");
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.teal_success));
            holder.tvStatusBadge.setBackgroundResource(R.drawable.badge_available);
        } else {
            holder.tvStatusBadge.setText("Borrowed");
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.coral_error));
            holder.tvStatusBadge.setBackgroundResource(R.drawable.badge_borrowed);
        }

        // Bookmark status
        boolean isSaved = DataManager.getInstance().isSaved(item.getId());
        updateBookmarkIcon(holder.btnBookmark, isSaved);

        holder.btnBookmark.setOnClickListener(v -> {
            DataManager.getInstance().toggleSaved(item.getId());
            boolean newState = DataManager.getInstance().isSaved(item.getId());
            updateBookmarkIcon(holder.btnBookmark, newState);
        });

        // Image loading
        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.bg_card)
                .centerCrop()
                .into(holder.ivImage);

        Glide.with(context)
                .load(item.getOwnerAvatar())
                .placeholder(R.drawable.ic_profile)
                .centerCrop()
                .into(holder.ivOwnerAvatar);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    private void updateBookmarkIcon(ImageButton btn, boolean saved) {
        if (saved) {
            btn.setImageResource(R.drawable.ic_bookmark_filled);
            btn.setColorFilter(ContextCompat.getColor(context, R.color.primary));
        } else {
            btn.setImageResource(R.drawable.ic_bookmark);
            btn.setColorFilter(ContextCompat.getColor(context, R.color.text_tertiary));
        }
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public void updateList(List<Item> newList) {
        this.itemList = newList;
        notifyDataSetChanged();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage, ivOwnerAvatar;
        ImageButton btnBookmark;
        TextView tvStatusBadge, tvName, tvCategory, tvOwner, tvDistance;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivItemImage);
            ivOwnerAvatar = itemView.findViewById(R.id.ivOwnerAvatar);
            btnBookmark = itemView.findViewById(R.id.btnBookmark);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvCategory = itemView.findViewById(R.id.tvItemCategory);
            tvOwner = itemView.findViewById(R.id.tvOwnerName);
            tvDistance = itemView.findViewById(R.id.tvDistance);
        }
    }
}
