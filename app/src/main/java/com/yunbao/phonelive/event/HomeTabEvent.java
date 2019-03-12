package com.yunbao.phonelive.event;

public class HomeTabEvent {
    private String tabId;

    public HomeTabEvent(String tabId) {
        this.tabId = tabId;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }
}
