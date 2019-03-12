package com.yunbao.phonelive.event;

public class CameraZoomEvent {
    private int zoom;

    public CameraZoomEvent(int zoom) {
        this.zoom = zoom;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }
}
