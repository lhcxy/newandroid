package com.yunbao.phonelive.bean;

import java.io.Serializable;

/**
 * Created by cxf on 2017/8/9.
 * 首页轮播图数据
 */

public class SliderBean  implements Serializable {
    private String slide_pic;
    private String slide_url;

    public String getSlide_pic() {
        return slide_pic;
    }

    public void setSlide_pic(String slide_pic) {
        this.slide_pic = slide_pic;
    }

    public String getSlide_url() {
        return slide_url;
    }

    public void setSlide_url(String slide_url) {
        this.slide_url = slide_url;
    }
}
