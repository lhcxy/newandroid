package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LiveRecordBean;

import java.util.List;

/**
 * Created by cxf on 2017/8/12.
 */

public class LiveRecordAdapter extends BaseAdapter {

    private Context mContext;
    private List<LiveRecordBean> mList;
    private LayoutInflater mInflater;

    public LiveRecordAdapter(Context context, List<LiveRecordBean> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(context);
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
            convertView = mInflater.inflate(R.layout.item_list_live_record, parent, false);
            vh.title = (TextView) convertView.findViewById(R.id.title);
            vh.nums = (TextView) convertView.findViewById(R.id.nums);
            vh.time = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(vh);
        } else {
            vh = (Vh) convertView.getTag();
        }
        LiveRecordBean bean = mList.get(position);
        if ("".equals(bean.getTitle())) {
            bean.setTitle(mContext.getString(R.string.no_title));
        }
        vh.title.setText(bean.getTitle());
        vh.nums.setText(bean.getNums());
        vh.time.setText(bean.getDateendtime());
        return convertView;
    }

    private class Vh {
        TextView title;
        TextView nums;
        TextView time;
    }
}
