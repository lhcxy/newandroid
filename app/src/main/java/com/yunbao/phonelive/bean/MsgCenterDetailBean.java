package com.yunbao.phonelive.bean;

import java.io.Serializable;

public class MsgCenterDetailBean implements Serializable {

    /**
     * create_time : 1542705700
     * content : <p>恭喜中奖了恭喜中奖了恭喜中奖了恭喜中奖了恭喜中奖了</p>
     * title : 恭喜中奖了
     */

    private String create_time;
    private String content;
    private String title;

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
