package com.yunbao.phonelive.bean;

import java.io.Serializable;

public class RankAdBean implements Serializable {

    /**
     *  榜单广告 bean
     * thumb : http://qiniu.2yx.cm/20180829/5b866665c056f.jpg  图片url
     * url : http://zhibo.2yx.cm:8282/index.php?g=portal&amp;m=page&amp;a=index&amp;id=25  html url
     */

    private String thumb;
    private String url;

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
