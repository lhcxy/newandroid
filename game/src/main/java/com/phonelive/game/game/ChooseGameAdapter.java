package com.phonelive.game.game;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;

import java.util.List;

/**
 * Created by cxf on 2017/10/6.
 */

public class ChooseGameAdapter extends BaseAdapter {

    private List<ChooseGameBean> mList;
    private LayoutInflater mInflater;

    public ChooseGameAdapter(List<ChooseGameBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(AppContext.sInstance);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public ChooseGameBean getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.game_item_list_choose, parent, false);
            vh = new Vh();
            vh.img = (ImageView) convertView.findViewById(R.id.icon);
            vh.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(vh);
        } else {
            vh = (Vh) convertView.getTag();
        }
        ChooseGameBean bean = mList.get(position);
        vh.img.setImageResource(bean.getImg());
        vh.name.setText(bean.getName());
        return convertView;
    }

    private class Vh {
        ImageView img;
        TextView name;
    }
}
