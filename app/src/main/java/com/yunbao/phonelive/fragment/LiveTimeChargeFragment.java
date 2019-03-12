package com.yunbao.phonelive.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.utils.DpUtil;

/**
 * Created by cxf on 2017/8/30.
 */

public class LiveTimeChargeFragment extends DialogFragment implements View.OnClickListener {

    private Context mContext;
    private View mRootView;
    private String mTypeVal;
    private TimeChargeAdapter mAdapter;
    private ListView mListView;
    private CommonCallback<String> mOnConfrimClick;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_time_charge, null, false);
        dialog.setContentView(mRootView);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(300);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = (ListView) mRootView.findViewById(R.id.listView);
        if (mAdapter == null) {
            mAdapter = new TimeChargeAdapter();
        }
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mTypeVal = mAdapter.getItem(position);
                mAdapter.setCurPosition(position);
            }
        });
        mTypeVal = mAdapter.getCoin();
        mRootView.findViewById(R.id.btn_cancel).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_confirm).setOnClickListener(this);
    }


    public void setOnConfrimClick(CommonCallback<String> onConfrimClick) {
        mOnConfrimClick = onConfrimClick;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                break;
            case R.id.btn_confirm:
                if (mOnConfrimClick != null) {
                    mOnConfrimClick.callback(mTypeVal);
                }
                break;
        }
        dismiss();
    }


    private class TimeChargeAdapter extends BaseAdapter {

        private String[] mTimes;
        private LayoutInflater mInflater;
        private int mCurPosition;

        public TimeChargeAdapter() {
            mTimes = AppConfig.getInstance().getConfig().getLive_time_coin();
            mInflater = LayoutInflater.from(AppContext.sInstance);
        }

        public void setCurPosition(int curPosition) {
            if (mCurPosition != curPosition) {
                mCurPosition = curPosition;
                notifyDataSetChanged();
            }
        }

        public String getCoin() {
            return mTimes[mCurPosition];
        }

        @Override
        public int getCount() {
            return mTimes.length;
        }

        @Override
        public String getItem(int position) {
            return mTimes[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) mInflater.inflate(R.layout.view_text, parent, false);
            String s = mTimes[position];
            String coinName = AppConfig.getInstance().getConfig().getName_coin();
            textView.setText(s + coinName + "/" + mContext.getString(R.string.minute));
            if (mCurPosition == position) {
                textView.setTextColor(0xffffd350);
            } else {
                textView.setTextColor(0xff000000);
            }
            return textView;
        }
    }
}
