package com.yunbao.phonelive.bean;

import java.io.Serializable;

/**
 * Created by cxf on 2017/9/1.
 */

public class LiveFilterBean implements Serializable {
    private int id;
    private String text;
    private int img;
    private boolean checked;

    public LiveFilterBean(int id, String text, int img, boolean checked) {
        this.id = id;
        this.text = text;
        this.img = img;
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}
