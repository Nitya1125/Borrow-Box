package com.borrowbox.app.models;

import java.io.Serializable;

public class Category implements Serializable {
    private String id;
    private String label;
    private int iconRes;

    public Category(String id, String label, int iconRes) {
        this.id = id;
        this.label = label;
        this.iconRes = iconRes;
    }

    public String getId() { return id; }
    public String getLabel() { return label; }
    public int getIconRes() { return iconRes; }
}
