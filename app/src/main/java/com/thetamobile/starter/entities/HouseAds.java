package com.thetamobile.starter.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class HouseAds {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String icon;
    private String name;
    private String packageName;
    private String detail;
    private int priority;
    private int interval;

    public HouseAds(int id, String icon, String name, String packageName, String detail, int priority, int interval) {
        this.id = id;
        this.icon = icon;
        this.name = name;
        this.packageName = packageName;
        this.detail = detail;
        this.priority = priority;
        this.interval = interval;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
