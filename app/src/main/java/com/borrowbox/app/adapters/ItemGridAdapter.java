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
import com.borrowbox.app.data.repository.ItemRepository;
import com.borrowbox.app.data.repository.RepositoryCallback;
import com.borrowbox.app.models.Item;
import com.borrowbox.app.utils.AvatarDrawable;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class ItemGridAdapter extends RecyclerView.Adapter<ItemGridAdapter.ItemViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    private final Context context;
    private List<Item> itemList;
    private final OnItemClickListener listener;

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

        String ownerName = item.getOwner();
        String firstName = (ownerName != null && !ownerName.trim().isEmpty())
                ? (ownerName.contains(" ") ? ownerName.split(" ")[0] : ownerName)
                : "Student";
        holder.tvOwner.setText(firstName);
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

        // Bookmark status with smooth optimistic update & animation
        boolean isSaved = DataManager.getInstance().isSaved(item.getId());
        updateBookmarkIcon(holder.btnBookmark, isSaved);

        holder.btnBookmark.setOnClickListener(v -> {
            boolean currentSaved = DataManager.getInstance().isSaved(item.getId());
            boolean newState = !currentSaved;
            updateBookmarkIcon(holder.btnBookmark, newState);

            holder.btnBookmark.animate()
                    .scaleX(1.25f)
                    .scaleY(1.25f)
                    .setDuration(120)
                    .withEndAction(() -> holder.btnBookmark.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(100)
                            .start())
                    .start();

            ItemRepository itemRepo = DataManager.getInstance().getItemRepository();
            String userId = DataManager.getInstance().getCurrentUserId();
            if (itemRepo != null && !userId.isEmpty()) {
                itemRepo.toggleSaved(userId, item.getId(), new RepositoryCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean saved) {
                        updateBookmarkIcon(holder.btnBookmark, saved);
                    }

                    @Override
                    public void onError(Throwable t) {
                        updateBookmarkIcon(holder.btnBookmark, currentSaved);
                    }
                });
            }
        });

        // Item photo loading
        String photoPath = item.getImageUrl();
        if (photoPath != null && !photoPath.trim().isEmpty()) {
            String cleanPath = photoPath.trim();
            Object model = (cleanPath.startsWith("/") && !cleanPath.startsWith("content:"))
                    ? new File(cleanPath)
                    : cleanPath;
            Glide.with(context)
                    .load(model)
                    .placeholder(R.drawable.bg_card)
                    .error(R.drawable.bg_card)
                    .centerCrop()
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.bg_card);
        }

        // Owner avatar loading
        String avatar = item.getOwnerAvatar();
        if (avatar != null && !avatar.trim().isEmpty()) {
            String cleanPath = avatar.trim();
            Object model = (cleanPath.startsWith("/") && !cleanPath.startsWith("content:"))
                    ? new File(cleanPath)
                    : cleanPath;
            Glide.with(context)
                    .load(model)
                    .placeholder(new AvatarDrawable(ownerName))
                    .error(new AvatarDrawable(ownerName))
                    .circleCrop()
                    .into(holder.ivOwnerAvatar);
        } else {
            holder.ivOwnerAvatar.setImageDrawable(new AvatarDrawable(ownerName));
        }

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
