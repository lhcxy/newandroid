package com.yunbao.phonelive.event;

import java.io.Serializable;

public class TabChannelEvent implements Serializable{

    private int selectedPosion;
    private String selectedId;

    public int getSelectedPosion() {
        return selectedPosion;
    }

    public void setSelectedPosion(int selectedPosion) {
        this.selectedPosion = selectedPosion;
    }
    public TabChannelEvent(int selectedPosion) {
        this.selectedPosion = selectedPosion;
    }

    public String getSelectedId() {
        return selectedId;
    }

    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
    }
}
