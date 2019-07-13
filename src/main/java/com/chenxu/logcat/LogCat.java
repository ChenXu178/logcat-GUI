package com.chenxu.logcat;

import java.awt.*;

public class LogCat {
    private String pid;
    private String level;
    private String time;
    private String tag;
    private String text;
    private Color color;
    private String original;

    public LogCat() {
    }

    public LogCat(String pid, String level, String time, String tag, String text, Color color, String original) {
        this.pid = pid;
        this.level = level;
        this.time = time;
        this.tag = tag;
        this.text = text;
        this.color = color;
        this.original = original;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    @Override
    public String toString() {
        return "LogCat{" +
                "pid='" + pid + '\'' +
                ", level='" + level + '\'' +
                ", time='" + time + '\'' +
                ", tag='" + tag + '\'' +
                ", text='" + text + '\'' +
                ", color=" + color +
                ", original='" + original + '\'' +
                '}';
    }
}
