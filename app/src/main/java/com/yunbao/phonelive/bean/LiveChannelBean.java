package com.yunbao.phonelive.bean;

import java.io.Serializable;
import java.util.List;

public class LiveChannelBean implements Serializable {


    /**
     * id : wealth
     * isshow : false
     * matchForbidDetail : [{"isForbid":false,"match":"直播"}]
     * name : 彩经
     * smallpic : http://resource.redirect.kukuplay.com/upload/cf0914-new_xxh.png
     */

    private String id;
    private boolean isshow;
    private String name;
    private String smallpic;
    private List<LiveChannelDetailBean> matchForbidDetail;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isIsshow() {
        return isshow;
    }

    public void setIsshow(boolean isshow) {
        this.isshow = isshow;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSmallpic() {
        return smallpic;
    }

    public void setSmallpic(String smallpic) {
        this.smallpic = smallpic;
    }

    public List<LiveChannelDetailBean> getMatchForbidDetail() {
        return matchForbidDetail;
    }

    public void setMatchForbidDetail(List<LiveChannelDetailBean> matchForbidDetail) {
        this.matchForbidDetail = matchForbidDetail;
    }


}
