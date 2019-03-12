package com.yunbao.phonelive.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.math.BigDecimal;

/**
 * Created by cxf on 2017/8/9.
 */

public class LiveBean implements Parcelable {
    private String uid = "";
    private String avatar = "";
    private String avatar_thumb = "";
    private String user_nicename = "";
    private String title = "";
    private String city = "";
    private String stream = "";
    private String pull = "";
    private String thumb = "";
    private String isvideo = "";
    private String anyway = "";
    private String nums = "";
    private int firenum = 0;
    private String id = "";
    private int isAttention = 0;
    private int isattention = 0;
    private String livetag = ""; //直播类型
    private int islive = 0; // 0 未开播  1 开播
    private String bTitle = ""; // 已关注 未关注 头部标题
    private int level = 0;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getbTitle() {
        return bTitle;
    }

    public void setbTitle(String bTitle) {
        this.bTitle = bTitle;
    }

    public String getAnyway() {
        return anyway;
    }

    public void setAnyway(String anyway) {
        this.anyway = anyway;
    }

    public String getUid() {
        if (!TextUtils.isEmpty(uid)) {
            return uid;
        } else return id;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar_thumb() {
        return avatar_thumb;
    }

    public void setAvatar_thumb(String avatar_thumb) {
        this.avatar_thumb = avatar_thumb;
    }

    public String getUser_nicename() {
        return user_nicename;
    }

    public void setUser_nicename(String user_nicename) {
        this.user_nicename = user_nicename;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getPull() {
        return pull;
    }

    public void setPull(String pull) {
        this.pull = pull;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getIsvideo() {
        return isvideo;
    }

    public void setIsvideo(String isvideo) {
        this.isvideo = isvideo;
    }

    public String getNums() {
        return nums;
    }

    public void setNums(String nums) {
        this.nums = nums;
    }

    public int getFirenum() {
        return firenum;
    }

    public void setFirenum(int firenum) {
        this.firenum = firenum;
    }


    public String getFireNums() {
        if (firenum >= 10000) {
            return String.valueOf(new BigDecimal((float) firenum / 10000).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue()) + "万";
        }
        return String.valueOf(firenum);
    }

    public LiveBean() {

    }

    public int getIsAttention() {
        return isAttention;
    }

    public void setIsAttention(int isAttention) {
        this.isAttention = isAttention;
    }

    public String getLivetag() {
        return livetag;
    }

    public void setLivetag(String livetag) {
        this.livetag = livetag;
    }

    public int getIslive() {
        return islive;
    }

    public void setIslive(int islive) {
        this.islive = islive;
    }

    public int getIsattention() {
        return isAttention;
    }

    public void setIsattention(int isattention) {
        this.isattention = isattention;
        isAttention = isattention;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.avatar);
        dest.writeString(this.avatar_thumb);
        dest.writeString(this.user_nicename);
        dest.writeString(this.title);
        dest.writeString(this.city);
        dest.writeString(this.stream);
        dest.writeString(this.pull);
        dest.writeString(this.thumb);
        dest.writeString(this.isvideo);
        dest.writeString(this.anyway);
        dest.writeString(this.nums);
        dest.writeInt(this.firenum);
        dest.writeString(this.id);
        dest.writeInt(this.isAttention);
        dest.writeInt(this.isattention);
        dest.writeString(this.livetag);
        dest.writeInt(this.islive);
        dest.writeString(this.bTitle);
        dest.writeInt(this.level);
    }

    protected LiveBean(Parcel in) {
        this.uid = in.readString();
        this.avatar = in.readString();
        this.avatar_thumb = in.readString();
        this.user_nicename = in.readString();
        this.title = in.readString();
        this.city = in.readString();
        this.stream = in.readString();
        this.pull = in.readString();
        this.thumb = in.readString();
        this.isvideo = in.readString();
        this.anyway = in.readString();
        this.nums = in.readString();
        this.firenum = in.readInt();
        this.id = in.readString();
        this.isAttention = in.readInt();
        this.isattention = in.readInt();
        this.livetag = in.readString();
        this.islive = in.readInt();
        this.bTitle = in.readString();
        this.level = in.readInt();
    }

    public static final Creator<LiveBean> CREATOR = new Creator<LiveBean>() {
        @Override
        public LiveBean createFromParcel(Parcel source) {
            return new LiveBean(source);
        }

        @Override
        public LiveBean[] newArray(int size) {
            return new LiveBean[size];
        }
    };
}
