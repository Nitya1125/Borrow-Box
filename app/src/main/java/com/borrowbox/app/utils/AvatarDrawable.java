package com.borrowbox.app.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AvatarDrawable extends Drawable {

    private static final int[] PALETTE = {
            0xFF4F46E5, // Indigo
            0xFF7C3AED, // Violet
            0xFF059669, // Emerald
            0xFF0284C7, // Sky
            0xFFD97706, // Amber
            0xFFDC2626, // Red
            0xFF9333EA  // Purple
    };

    private final Paint backgroundPaint;
    private final Paint textPaint;
    private final String initials;
    private final RectF boundsRect;

    public AvatarDrawable(String name) {
        String init = getInitials(name);
        this.initials = init;

        int color = getColorForName(name);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(color);
        backgroundPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        boundsRect = new RectF();
    }

    private static String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "U";
        }
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        } else {
            return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
        }
    }

    private static int getColorForName(String name) {
        if (name == null || name.isEmpty()) return PALETTE[0];
        int hash = Math.abs(name.hashCode());
        return PALETTE[hash % PALETTE.length];
    }

    @Override
    protected void onBoundsChange(@NonNull Rect bounds) {
        super.onBoundsChange(bounds);
        boundsRect.set(bounds);
        textPaint.setTextSize(bounds.height() * 0.42f);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        float cx = boundsRect.centerX();
        float cy = boundsRect.centerY();
        float radius = Math.min(boundsRect.width(), boundsRect.height()) / 2f;

        canvas.drawCircle(cx, cy, radius, backgroundPaint);

        // Center text vertically
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float textY = cy - (fm.ascent + fm.descent) / 2f;
        canvas.drawText(initials, cx, textY, textPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        backgroundPaint.setAlpha(alpha);
        textPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        backgroundPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
