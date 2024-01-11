package com.batteryanimation.neonbatteryeffects.charge.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LockThemeModel {
    private int id;
    private String title, imageURL;

    private boolean isSelected;

    @SerializedName("themes")
    private List<LockThemeModel> themes;

    public LockThemeModel(String imageURL) {
        this.imageURL = imageURL;
    }

    public LockThemeModel(List<LockThemeModel> themes) {
        this.themes = themes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public List<LockThemeModel> getThemes() {
        return themes;
    }

    public void setThemes(List<LockThemeModel> themes) {
        this.themes = themes;
    }
}
