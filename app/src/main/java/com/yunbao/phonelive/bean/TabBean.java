package com.yunbao.phonelive.bean;

import java.io.Serializable;
import java.util.List;

public class TabBean implements Serializable {

    /**
     * id : 8
     * name : 电竞
     * icon : http://qiniu.2yx.cm/20180827/5b83c309a4211.png
     * create_time : 1535361803
     * update_time : 1535361803
     * status : 1
     * show : 0
     */

    private String id;
    private String name;
    private String icon;
    private String create_time;
    private String update_time;
    private String status;
    private String show;
    /**
     * pid : 0
     * sub : [{"id":"116","name":"直播"},{"id":"117","name":"其他"}]
     */

    private String pid;
    private List<TabBean> sub;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }


    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public List<TabBean> getSub() {
        return sub;
    }

    public void setSub(List<TabBean> sub) {
        this.sub = sub;
    }
}
