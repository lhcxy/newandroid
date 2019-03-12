package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.RewardBean;
import com.yunbao.phonelive.utils.L;

import java.util.List;

/**
 * Created by cxf on 2017/9/22.
 */

public class RewardAdapter extends BaseAdapter {

    private Context mContext;
    private List<RewardBean> mList;
    private LayoutInflater mInflater;
    private int mStartPosition;
    private View mAnimView;
    private int[] mStartLocation;

    public RewardAdapter(Context context, List<RewardBean> list, int position) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
        mStartPosition = position;
        mStartLocation=new int[2];
    }

    public void cancelAnimation() {
        if (mAnimView != null) {
            mAnimView.clearAnimation();
        }
    }

    public int[] getStartLocation() {
        return mStartLocation;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public RewardBean getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.item_list_login_reward, parent, false);
            vh = new Vh();
            vh.day = (TextView) convertView.findViewById(R.id.day);
            vh.coin = (TextView) convertView.findViewById(R.id.coin);
            vh.star = convertView.findViewById(R.id.star);
            vh.flash = convertView.findViewById(R.id.flash);
            vh.get = convertView.findViewById(R.id.get);
            convertView.setTag(vh);
        } else {
            vh = (Vh) convertView.getTag();
        }
        RewardBean bean = mList.get(position);
        L.e("position------>"+position);
        if (position < mStartPosition) {
            if (vh.day.getVisibility() == View.VISIBLE) {
                vh.day.setVisibility(View.INVISIBLE);
            }
            if (vh.coin.getVisibility() == View.VISIBLE) {
                vh.coin.setVisibility(View.INVISIBLE);
            }
            if (vh.star.getVisibility() == View.VISIBLE) {
                vh.star.setVisibility(View.INVISIBLE);
            }
            if (vh.flash.getVisibility() == View.VISIBLE) {
                vh.flash.setVisibility(View.INVISIBLE);
            }
            if (vh.get.getVisibility() == View.INVISIBLE) {
                vh.get.setVisibility(View.VISIBLE);
            }
        } else {
            if (vh.day.getVisibility() == View.INVISIBLE) {
                vh.day.setVisibility(View.VISIBLE);
            }
            if (vh.coin.getVisibility() == View.INVISIBLE) {
                vh.coin.setVisibility(View.VISIBLE);
            }
            if (vh.star.getVisibility() == View.INVISIBLE) {
                vh.star.setVisibility(View.VISIBLE);
            }
            if (vh.get.getVisibility() == View.VISIBLE) {
                vh.get.setVisibility(View.INVISIBLE);
            }
            vh.day.setText(mContext.getString(R.string.di) + bean.getDay() + mContext.getString(R.string.day));
            vh.coin.setText(bean.getCoin());
            if (position == mStartPosition) {
                if(mStartLocation[0]==0||mStartLocation[1]==0){
                    final Vh finalVh = vh;
                    vh.star.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mStartLocation[0]==0||mStartLocation[1]==0){
                                finalVh.star.getLocationOnScreen(mStartLocation);
                            }
                        }
                    });
                }
                if (vh.flash.getVisibility() == View.INVISIBLE) {
                    vh.flash.setVisibility(View.VISIBLE);
                }
                mAnimView = vh.flash;
                Animation a = AnimationUtils.loadAnimation(mContext, R.anim.login_reward_flash);
                a.setInterpolator(new LinearInterpolator());
                mAnimView.startAnimation(a);
            } else {
                if (vh.flash.getVisibility() == View.VISIBLE) {
                    vh.flash.setVisibility(View.INVISIBLE);
                }
            }
        }
        return convertView;
    }

    private class Vh {
        TextView day;
        TextView coin;
        View star;
        View flash;
        View get;
    }
}
