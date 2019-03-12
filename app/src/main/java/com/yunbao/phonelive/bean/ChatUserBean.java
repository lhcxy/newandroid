package com.yunbao.phonelive.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cxf on 2017/8/14.
 */

public class ChatUserBean extends UserBean implements Parcelable {
    private String lastMessage = "";
    private int unReadCount = 0;
    private String lastTime = "";

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.lastMessage);
        dest.writeInt(this.unReadCount);
        dest.writeString(this.lastTime);
    }

    public ChatUserBean() {

    }

    protected ChatUserBean(Parcel in) {
        super(in);
        this.lastMessage = in.readString();
        this.unReadCount = in.readInt();
        this.lastTime = in.readString();
    }

    public static final Parcelable.Creator<ChatUserBean> CREATOR = new Creator<ChatUserBean>() {
        @Override
        public ChatUserBean[] newArray(int size) {
            return new ChatUserBean[size];
        }

        @Override
        public ChatUserBean createFromParcel(Parcel in) {
            return new ChatUserBean(in);
        }
    };


}
