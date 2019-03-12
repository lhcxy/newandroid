package com.phonelive.game.game.nz;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.utils.WordUtil;

/**
 * Created by cxf on 2017/10/19.
 * 游戏胜负记录
 */

public class GameNzSfAdapter extends BaseAdapter {

    private int[][] mArrays;
    private LayoutInflater mInflater;
    private String mVictory;
    private String mLose;
    private Drawable mVictoryDrawable;
    private Drawable mLoseDrawable;

    public GameNzSfAdapter(Context context, int[][] arrays) {
        mArrays = arrays;
        mInflater = LayoutInflater.from(AppContext.sInstance);
        mVictory = WordUtil.getString(R.string.game_nz_victory);
        mLose = WordUtil.getString(R.string.game_nz_lose);
        mVictoryDrawable = ContextCompat.getDrawable(context, R.drawable.bg_nz_sf_s);
        mLoseDrawable = ContextCompat.getDrawable(context, R.drawable.bg_nz_sf_f);
    }

    @Override
    public int getCount() {
        return mArrays.length;
    }

    @Override
    public Object getItem(int position) {
        return mArrays[position];
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
            convertView = mInflater.inflate(R.layout.game_item_nz_sf, parent, false);
            vh.first = (TextView) convertView.findViewById(R.id.first);
            vh.second = (TextView) convertView.findViewById(R.id.second);
            vh.third = (TextView) convertView.findViewById(R.id.third);
            convertView.setTag(vh);
        } else {
            vh = (Vh) convertView.getTag();
        }
        int[] arr = mArrays[position];
        if (arr[0] == 1) {
            vh.first.setText(mVictory);
            vh.first.setBackground(mVictoryDrawable);
        } else {
            vh.first.setText(mLose);
            vh.first.setBackground(mLoseDrawable);
        }
        if (arr[1] == 1) {
            vh.second.setText(mVictory);
            vh.second.setBackground(mVictoryDrawable);
        } else {
            vh.second.setText(mLose);
            vh.second.setBackground(mLoseDrawable);
        }
        if (arr[2] == 1) {
            vh.third.setText(mVictory);
            vh.third.setBackground(mVictoryDrawable);
        } else {
            vh.third.setText(mLose);
            vh.third.setBackground(mLoseDrawable);
        }
        return convertView;
    }

    private class Vh {
        TextView first;
        TextView second;
        TextView third;
    }
}
