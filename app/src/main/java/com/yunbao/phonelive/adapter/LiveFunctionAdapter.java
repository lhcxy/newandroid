package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.FunctionBean;

import java.util.List;

/**
 * Created by cxf on 2017/8/19.
 */

public class LiveFunctionAdapter extends BaseAdapter {

    private Context mContext;
    private List<FunctionBean> mList;
    private LayoutInflater mInflater;

    public LiveFunctionAdapter(Context context, List<FunctionBean> list) {
        mContext = context;
        mList = list;
        mInflater=LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Vh vh = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_list_live_function, parent, false);
            vh = new Vh();
            vh.img = (ImageView) convertView;
            convertView.setTag(vh);
        } else {
            vh = (Vh) convertView.getTag();
        }
        FunctionBean bean = mList.get(position);
        vh.img.setImageResource(bean.getIcon());
        return convertView;
    }

    private class Vh {
        ImageView img;
    }

}
