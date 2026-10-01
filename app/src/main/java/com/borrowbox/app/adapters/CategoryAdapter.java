package com.borrowbox.app.adapters;

import android.content.Context;
import android.graphics.Color;
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
import com.borrowbox.app.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category, int position);
    }

    private Context context;
    private List<Category> categoryList;
    private int selectedPosition = 0;
    private OnCategoryClickListener listener;

    public CategoryAdapter(Context context, List<Category> categoryList, OnCategoryClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_chip, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category cat = categoryList.get(position);
        holder.ivIcon.setImageResource(cat.getIconRes());
        holder.tvLabel.setText(cat.getLabel());

        boolean isSelected = (position == selectedPosition);
        if (isSelected) {
            holder.container.setBackgroundResource(R.drawable.bg_chip_selected);
            holder.ivIcon.setColorFilter(Color.WHITE);
            holder.tvLabel.setTextColor(Color.WHITE);
        } else {
            holder.container.setBackgroundResource(R.drawable.bg_chip_unselected);
            holder.ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.primary));
            holder.tvLabel.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
        }

        holder.itemView.setOnClickListener(v -> {
            int currentPos = holder.getBindingAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                int prev = selectedPosition;
                selectedPosition = currentPos;
                notifyItemChanged(prev);
                notifyItemChanged(selectedPosition);
                if (listener != null) {
                    listener.onCategoryClick(cat, selectedPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    public void setSelectedCategory(String categoryId) {
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getId().equalsIgnoreCase(categoryId)) {
                int prev = selectedPosition;
                selectedPosition = i;
                notifyItemChanged(prev);
                notifyItemChanged(selectedPosition);
                break;
            }
        }
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;
        ImageView ivIcon;
        TextView tvLabel;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.categoryContainer);
            ivIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvLabel = itemView.findViewById(R.id.tvCategoryLabel);
        }
    }
}
