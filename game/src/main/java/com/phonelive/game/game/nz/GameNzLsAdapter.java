package com.phonelive.game.game.nz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;

import java.util.List;

/**
 * Created by cxf on 2017/10/19.
 * 庄家流水adapter
 */

public class GameNzLsAdapter extends BaseAdapter {

    private List<GameNzLsBean> mList;
    private LayoutInflater mInflater;

    public GameNzLsAdapter(List<GameNzLsBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(AppContext.sInstance);
    }

    public void setList(List<GameNzLsBean> list) {
        mList = list;
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
            vh = new Vh();
            convertView = mInflater.inflate(R.layout.game_item_nz_ls, parent, false);
            vh.num = (TextView) convertView.findViewById(R.id.num);
            vh.card = (TextView) convertView.findViewById(R.id.card);
            vh.result = (TextView) convertView.findViewById(R.id.result);
            convertView.setTag(vh);
        } else {
            vh = (Vh) convertView.getTag();
        }
        GameNzLsBean bean = mList.get(position);
        vh.num.setText(String.valueOf(mList.size() - position));
        vh.card.setText(bean.getBanker_card());
        vh.result.setText(bean.getBanker_profit());
        return convertView;
    }

    private class Vh {
        TextView num;
        TextView card;
        TextView result;
    }
}
