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

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchViewHolder> {

    public interface OnSearchItemClickListener {
        void onSearchItemClick(Item item);
    }

    private Context context;
    private List<Item> itemList;
    private OnSearchItemClickListener listener;

    public SearchResultAdapter(Context context, List<Item> itemList, OnSearchItemClickListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.tvName.setText(item.getName());
        holder.tvCategory.setText(item.getCategory());
        holder.tvDistance.setText(item.getDistance());

        if (item.isAvailable()) {
            holder.tvStatus.setText("Available");
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.teal_success));
            holder.tvStatus.setBackgroundResource(R.drawable.badge_available);
        } else {
            holder.tvStatus.setText("Borrowed");
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.coral_error));
            holder.tvStatus.setBackgroundResource(R.drawable.badge_borrowed);
        }

        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.bg_card)
                .centerCrop()
                .into(holder.ivImage);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSearchItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public void updateList(List<Item> newList) {
        this.itemList = newList;
        notifyDataSetChanged();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvCategory, tvStatus, tvDistance;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivSearchImage);
            tvName = itemView.findViewById(R.id.tvSearchName);
            tvCategory = itemView.findViewById(R.id.tvSearchCategory);
            tvStatus = itemView.findViewById(R.id.tvSearchStatusBadge);
            tvDistance = itemView.findViewById(R.id.tvSearchDistance);
        }
    }
}
