package com.borrowbox.app.models;

public class OnboardingSlide {
    private String title;
    private String subtitle;
    private int iconRes;
    private String accentColor;
    private String bgStartColor;
    private String bgEndColor;

    public OnboardingSlide(String title, String subtitle, int iconRes, String accentColor, String bgStartColor, String bgEndColor) {
        this.title = title;
        this.subtitle = subtitle;
        this.iconRes = iconRes;
        this.accentColor = accentColor;
        this.bgStartColor = bgStartColor;
        this.bgEndColor = bgEndColor;
    }

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public int getIconRes() { return iconRes; }
    public String getAccentColor() { return accentColor; }
    public String getBgStartColor() { return bgStartColor; }
    public String getBgEndColor() { return bgEndColor; }
}
