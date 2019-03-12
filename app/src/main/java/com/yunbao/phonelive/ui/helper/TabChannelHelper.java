package com.yunbao.phonelive.ui.helper;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yunbao.phonelive.bean.TabBean;
import com.yunbao.phonelive.utils.SharedPreferencesUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TabChannelHelper {
    private TabChannelHelper() {
    }

    private static TabChannelHelper sInstance;


    public static TabChannelHelper getInstance() {
        if (sInstance == null) {
            synchronized (TabChannelHelper.class) {
                if (sInstance == null) {
                    sInstance = new TabChannelHelper();
                }
            }
        }
        return sInstance;
    }

    public void saveShowData(ArrayList<TabBean> datas) {
        if (datas == null || datas.size() == 0) {
            SharedPreferencesUtil.getInstance().saveString(SharedPreferencesUtil.TAB_SHOW, "");
        } else {
            SharedPreferencesUtil.getInstance().saveString(SharedPreferencesUtil.TAB_SHOW, new Gson().toJson(datas));
        }
    }

    public void saveHideData(ArrayList<TabBean> datas) {
        if (datas == null || datas.size() == 0) {
            SharedPreferencesUtil.getInstance().saveString(SharedPreferencesUtil.TAB_HIDE, "");
        } else {
            SharedPreferencesUtil.getInstance().saveString(SharedPreferencesUtil.TAB_HIDE, new Gson().toJson(datas));
        }
    }

    public ArrayList<TabBean> getShowData(Context context) {
        String showDataJson = SharedPreferencesUtil.getInstance().getString(SharedPreferencesUtil.TAB_SHOW);
        String hideDataJson = SharedPreferencesUtil.getInstance().getString(SharedPreferencesUtil.TAB_HIDE);
        ArrayList<TabBean> showData;
        if (TextUtils.isEmpty(showDataJson) && TextUtils.isEmpty(hideDataJson)) {
            //如果没有数据 则默认显示全部频道
            return getFullData(context);
        } else {
            showData = new ArrayList<>();
            if (!TextUtils.isEmpty(showDataJson)) {
                showData = new Gson().fromJson(showDataJson, new TypeToken<ArrayList<TabBean>>() {
                }.getType());
            }
        }
        return showData;
    }

    public ArrayList<TabBean> getHideData() {
        ArrayList<TabBean> datas;
        String hideDataJson = SharedPreferencesUtil.getInstance().getString(SharedPreferencesUtil.TAB_HIDE);
        if (!TextUtils.isEmpty(hideDataJson)) {
            datas = new Gson().fromJson(hideDataJson, new TypeToken<ArrayList<TabBean>>() {
            }.getType());
        } else {
            datas = new ArrayList<>();
        }
        return datas;
    }

    public ArrayList<TabBean> getFullData(Context context) {
        ArrayList<TabBean> datas;
        String hideDataJson = SharedPreferencesUtil.getInstance().getString(SharedPreferencesUtil.TAB_ALL);
        if (!TextUtils.isEmpty(hideDataJson)) {
            datas = new Gson().fromJson(hideDataJson, new TypeToken<ArrayList<TabBean>>() {
            }.getType());
        } else {
            datas = new ArrayList<>();
        }
        return datas;
    }

    /**
     * 数据对比保存后返回
     *
     * @param newDatas
     * @return
     */
    public void saveFullData(Context context, List<TabBean> newDatas) {
        if (newDatas == null || newDatas.size() == 0) {
            SharedPreferencesUtil.getInstance().saveString(SharedPreferencesUtil.TAB_ALL, "");
        } else {
            SharedPreferencesUtil.getInstance().saveString(SharedPreferencesUtil.TAB_ALL, new Gson().toJson(newDatas));
            ArrayList<TabBean> showData = getShowData(context);
            HashSet<String> keySet = new HashSet<>();
            for (TabBean showDatum : showData) {
                keySet.add(showDatum.getId());
            }
            ArrayList<TabBean> hideData = getHideData();
            for (TabBean hideDatum : hideData) {
                keySet.add(hideDatum.getId());
            }

            for (TabBean newData : newDatas) {
                if (keySet.add(newData.getId())) {
                    showData.add(newData);
                }
            }
            saveShowData(showData);
        }

    }

    public String getJson(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

}
