package com.yunbao.phonelive.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;

/**
 * Created by cxf on 2017/8/29.
 */

public class LiveTypeAdapter extends BaseAdapter {

    private String[][] mTypes;
    private LayoutInflater mInflater;

    public LiveTypeAdapter(String[][] types) {
        mTypes = types;
        mInflater = LayoutInflater.from(AppContext.sInstance);
    }

    public void setData(String[][] types) {
        mTypes = types;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mTypes.length;
    }

    @Override
    public String[] getItem(int position) {
        return mTypes[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) mInflater.inflate(R.layout.item_list_live_type, parent, false);
        String[] type = mTypes[position];
        textView.setText(type[1]);
        return textView;
    }
}
