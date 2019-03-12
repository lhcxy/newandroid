package com.yunbao.phonelive.socket;

import com.yunbao.phonelive.ui.tools.StringUtil;
import com.yunbao.phonelive.utils.L;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by cxf on 2017/8/22.
 * 发送socket的实体类
 */

public class SendSocketBean implements Serializable {

    private JSONObject mResult;
    private JSONArray mMsg;
    private JSONObject mMsg0;

    public SendSocketBean() {
        mResult = new JSONObject();
        mMsg = new JSONArray();
        mMsg0 = new JSONObject();
    }

    public SendSocketBean param(String key, String value) {
        try {
            mMsg0.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public SendSocketBean param(String key, String[] value) {
        try {
            int[] ints = new int[value.length];
            for (int i = 0; i < value.length; i++) {
                if (StringUtil.isNumeric(value[i])) {
                    ints[i] = Integer.valueOf(value[i]);
                }
            }
            mMsg0.putOpt(key, ints);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public SendSocketBean param(String key, int value) {
        return param(key, String.valueOf(value));
    }

    public SendSocketBean param(String key, JSONObject value) {
        try {
            mMsg0.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public SendSocketBean jsonObjectParam(String key, String value) {
        L.e("jsonObjectParam---->" + value);
        try {
            mMsg0.put(key, new JSONObject(value));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JSONObject create() {
        try {
            mMsg.put(mMsg0);
            mResult.put("retcode", "000000");
            mResult.put("retmsg", "ok");
            mResult.put("msg", mMsg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        L.e("发送socket-->" + mResult.toString());
        return mResult;
    }
    public JSONObject create(Map<String,Object> map) {
        try {
            mMsg.put(new JSONObject(map));
            mResult.put("retcode", "000000");
            mResult.put("retmsg", "ok");
            mResult.put("msg", mMsg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        L.e("发送socket-->" + mResult.toString());
        return mResult;
    }

}
