package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ChargeBean;

import java.util.List;

/**
 * Created by cxf on 2017/9/21.
 */

public class ChargeAdapter extends BaseAdapter {

    private Context mContext;
    private List<ChargeBean> mList;
    private LayoutInflater mInflater;
    private String mCoinName;

    public ChargeAdapter(Context context, List<ChargeBean> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
        mCoinName = AppConfig.getInstance().getConfig().getName_coin();
    }

    public void setList(List<ChargeBean> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public ChargeBean getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.item_list_charge, parent, false);
            vh = new Vh();
            vh.coin = (TextView) convertView.findViewById(R.id.coin);
            vh.give = (TextView) convertView.findViewById(R.id.give);
            vh.money = (TextView) convertView.findViewById(R.id.money);
            convertView.setTag(vh);
        } else {
            vh = (Vh) convertView.getTag();
        }
        ChargeBean bean = mList.get(position);
        vh.coin.setText(String.valueOf(bean.getCoin()));
        if (bean.getGive() == 0) {
            if (vh.give.getVisibility() == View.VISIBLE) {
                vh.give.setVisibility(View.GONE);
            }
        } else {
            if (vh.give.getVisibility() == View.GONE) {
                vh.give.setVisibility(View.VISIBLE);
            }
            vh.give.setText(mContext.getString(R.string.give) + bean.getGive() + mCoinName);
        }
        vh.money.setText(bean.getMoney());
        return convertView;
    }

    private class Vh {
        TextView coin;
        TextView give;
        TextView money;
    }
}
