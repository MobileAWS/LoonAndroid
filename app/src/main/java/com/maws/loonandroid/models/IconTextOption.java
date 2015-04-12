package com.maws.loonandroid.models;

/**
 * Created by Andrexxjc on 11/04/2015.
 */

import com.maws.loonandroid.enums.FragmentType;

/**
 * Created by Andrexxjc on 10/10/2014.
 */
public class IconTextOption {

    private String text;
    private int icon;
    private FragmentType type;

    public IconTextOption(String text, Integer icon) {
        this.text = text;
        this.icon = icon;
    }

    public IconTextOption(String text, Integer icon, FragmentType type) {
        this.text = text;
        this.icon = icon;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    @Override
    public String toString(){
        return this.text;
    }

    public FragmentType getType() {
        return type;
    }
}

