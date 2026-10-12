package com.borrowbox.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.borrowbox.app.R;
import com.borrowbox.app.models.OnboardingSlide;

import java.util.List;

public class OnboardingPagerAdapter extends RecyclerView.Adapter<OnboardingPagerAdapter.SlideViewHolder> {

    private Context context;
    private List<OnboardingSlide> slides;

    public OnboardingPagerAdapter(Context context, List<OnboardingSlide> slides) {
        this.context = context;
        this.slides = slides;
    }

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_onboarding_slide, parent, false);
        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        OnboardingSlide slide = slides.get(position);

        holder.tvTitle.setText(slide.getTitle());
        holder.tvSubtitle.setText(slide.getSubtitle());
        holder.ivIllustration.setImageResource(slide.getIconRes());

        // Keep background clean using theme background
        holder.container.setBackgroundResource(R.color.background);
    }

    @Override
    public int getItemCount() {
        return slides != null ? slides.size() : 0;
    }

    public static class SlideViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;
        ImageView ivIllustration;
        TextView tvTitle, tvSubtitle;

        public SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.slideContainer);
            ivIllustration = itemView.findViewById(R.id.ivSlideIllustration);
            tvTitle = itemView.findViewById(R.id.tvSlideTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSlideSubtitle);
        }
    }
}
