package com.phonelive.game.game.nz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.utils.WordUtil;

import java.util.List;

/**
 * Created by cxf on 2017/10/19.
 */

public class GameNzSzAdapter extends BaseAdapter {

    private List<GameNzSzBean> mList;
    private LayoutInflater mInflater;

    public GameNzSzAdapter(List<GameNzSzBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(AppContext.sInstance);
    }

    public void setList(List<GameNzSzBean> list) {
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
            convertView = mInflater.inflate(R.layout.game_item_nz_sz, parent, false);
            vh.player = (TextView) convertView.findViewById(R.id.player);
            vh.coin = (TextView) convertView.findViewById(R.id.coin);
            convertView.setTag(vh);
        } else {
            vh = (Vh) convertView.getTag();
        }
        GameNzSzBean bean = mList.get(position);
        vh.player.setText(bean.getUser_nicename());
        if ("0".equals(bean.getId())) {
            vh.coin.setText(bean.getCoin());
        } else {
            vh.coin.setText(bean.getDeposit());
        }
        return convertView;
    }

    private class Vh {
        TextView order;
        TextView player;
        TextView coin;
    }
}
